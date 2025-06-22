package com.vidz.data.websocket

import android.util.Log
import com.vidz.data.mapper.MessageMapper
import com.vidz.data.server.retrofit.dto.MessageDto
import com.vidz.domain.model.Message
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompWebSocketClient @Inject constructor(
    private val messageMapper: MessageMapper,
    private val webSocketConfig: WebSocketConfig
) {
    
    private val client = OkHttpClient.Builder()
        .build()
    
    private var webSocket: WebSocket? = null
    private var currentToken: String? = null
    private var isConnected = false
    private var isStompConnected = false
    private var pendingUserEmail: String? = null // Store user email for auto-subscription
    
    private val _messageFlow = MutableSharedFlow<Message>(
        replay = 1, // Keep the last message for late subscribers
        extraBufferCapacity = 64 // Buffer messages if subscriber is slow
    )
    val messageFlow: Flow<Message> = _messageFlow.asSharedFlow()
    
    private val _connectionStatusFlow = MutableSharedFlow<ConnectionStatus>(replay = 1)
    val connectionStatusFlow: Flow<ConnectionStatus> = _connectionStatusFlow.asSharedFlow()
    
    enum class ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, ERROR
    }
    
    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("StompWebSocketClient", "WebSocket connection opened")
            isConnected = true
            _connectionStatusFlow.tryEmit(ConnectionStatus.CONNECTED)
            
            // Send CONNECT frame with authorization
            currentToken?.let { token ->
                val connectFrame = "CONNECT\n" +
                        "Authorization:Bearer $token\n" +
                        "accept-version:1.0,1.1,2.0\n" +
                        "heart-beat:10000,10000\n" +
                        "\n" +
                        "\u0000"
                webSocket.send(connectFrame)
                Log.d("StompWebSocketClient", "Sent CONNECT frame with authorization")
            }
        }
        
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("StompWebSocketClient", "Received message: $text")
            handleStompMessage(text)
        }
        
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("StompWebSocketClient", "Received binary message: ${bytes.utf8()}")
            handleStompMessage(bytes.utf8())
        }
        
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("StompWebSocketClient", "WebSocket closing: $code / $reason")
            isConnected = false
            isStompConnected = false
            _connectionStatusFlow.tryEmit(ConnectionStatus.DISCONNECTED)
        }
        
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("StompWebSocketClient", "WebSocket closed: $code / $reason")
            isConnected = false
            isStompConnected = false
            _connectionStatusFlow.tryEmit(ConnectionStatus.DISCONNECTED)
        }
        
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("StompWebSocketClient", "WebSocket error", t)
            isConnected = false
            isStompConnected = false
            _connectionStatusFlow.tryEmit(ConnectionStatus.ERROR)
        }
    }
    
    private val request = Request.Builder()
        .url(webSocketConfig.getWebSocketUrl())
        .build()
    
    fun connect(token: String, userEmail: String? = null): Boolean {
        return try {
            currentToken = token
            pendingUserEmail = userEmail
            _connectionStatusFlow.tryEmit(ConnectionStatus.CONNECTING)
            webSocket = client.newWebSocket(request, webSocketListener)
            true
        } catch (e: Exception) {
            Log.e("StompWebSocketClient", "Failed to connect WebSocket", e)
            _connectionStatusFlow.tryEmit(ConnectionStatus.ERROR)
            false
        }
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        isConnected = false
        isStompConnected = false
        pendingUserEmail = null
        _connectionStatusFlow.tryEmit(ConnectionStatus.DISCONNECTED)
    }
    
    fun subscribeToMessages(userEmail: String) {
        if (!isConnected || !isStompConnected || webSocket == null) {
            Log.w("StompWebSocketClient", "Cannot subscribe: WebSocket not connected or STOMP not established")
            // Store userEmail for later subscription if STOMP isn't connected yet
            if (isConnected && !isStompConnected) {
                pendingUserEmail = userEmail
                Log.d("StompWebSocketClient", "Stored userEmail for pending subscription: $userEmail")
            }
            return
        }
        try {
            val destination = webSocketConfig.getMessageSubscriptionDestination(userEmail)
            Log.d("StompWebSocketClient", "Subscribing to destination: $destination")
            
            currentToken?.let { token ->
                val subscribeFrame = "SUBSCRIBE\n" +
                        "id:sub-$userEmail\n" +
                        "destination:$destination\n" +
                        "Authorization:Bearer $token\n" +
                        "\n" +
                        "\u0000"
                webSocket?.send(subscribeFrame)
                Log.d("StompWebSocketClient", "Successfully sent subscription frame for $userEmail")
            }
        } catch (e: Exception) {
            Log.e("StompWebSocketClient", "subscribeToMessages failed", e)
        }
    }
    
    fun sendMessage(conversationId: Long, messageContent: String, senderId: Long): Boolean {
        if (!isConnected || webSocket == null) {
            Log.w("StompWebSocketClient", "Cannot send message: WebSocket not connected")
            return false
        }
        
        return try {
            val destination = webSocketConfig.getSendMessageDestination(conversationId)
            val messageBody = """{"content":"$messageContent","senderId":$senderId,"conversationId":$conversationId}"""
            
            val sendFrame = "SEND\n" +
                    "destination:$destination\n" +
                    "content-type:application/json\n" +
                    "\n" +
                    "$messageBody\n" +
                    "\u0000"
            
            webSocket?.send(sendFrame)
            Log.d("StompWebSocketClient", "Sent message to $destination: $messageBody")
            true
        } catch (e: Exception) {
            Log.e("StompWebSocketClient", "Failed to send message", e)
            false
        }
    }
    
    private fun handleStompMessage(message: String) {
        try {
            when {
                message.startsWith("CONNECTED") -> {
                    Log.d("StompWebSocketClient", "STOMP connection established")
                    isStompConnected = true
                    
                    // Auto-subscribe to messages if userEmail was provided
                    pendingUserEmail?.let { userEmail ->
                        Log.d("StompWebSocketClient", "Auto-subscribing to messages for user: $userEmail")
                        subscribeToMessages(userEmail)
                    }
                }
                message.startsWith("MESSAGE") -> {
                    // Parse the message body from STOMP frame
                    val bodyStart = message.indexOf("\n\n") + 2
                    if (bodyStart > 1 && bodyStart < message.length) {
                        // Clean the message body by removing null terminators and trimming
                        val messageBody = message.substring(bodyStart)
                            .replace("\u0000", "")
                            .replace("??", "")
                            .trim()
                        
                        Log.d("StompWebSocketClient", "Processing message body: $messageBody")
                        
                        try {
                            // Parse JSON message and emit to flow
                            val json = Json { ignoreUnknownKeys = true }
                            val messageDto = json.decodeFromString<com.vidz.data.server.retrofit.dto.MessageDto>(messageBody)
                            val domainMessage = messageMapper.toDomain(messageDto)
                            
                            Log.d("StompWebSocketClient", "Parsed message - ID: ${domainMessage.messageId}, ConversationID: ${domainMessage.conversationId}, Content: '${domainMessage.content}'")
                            Log.d("StompWebSocketClient", "Flow subscribers count: ${_messageFlow.subscriptionCount.value}")
                            
                            val emitted = _messageFlow.tryEmit(domainMessage)
                            Log.d("StompWebSocketClient", "Emitted message to flow: success=$emitted")
                            
                            if (!emitted) {
                                Log.w("StompWebSocketClient", "Failed to emit message to flow - buffer might be full or no subscribers")
                            }
                        } catch (e: Exception) {
                            Log.e("StompWebSocketClient", "Failed to parse message JSON: $messageBody", e)
                        }
                    }
                }
                message.startsWith("ERROR") -> {
                    Log.e("StompWebSocketClient", "STOMP error: $message")
                }
                message.startsWith("RECEIPT") -> {
                    Log.d("StompWebSocketClient", "STOMP receipt: $message")
                }
            }
        } catch (e: Exception) {
            Log.e("StompWebSocketClient", "Error handling STOMP message", e)
        }
    }
} 