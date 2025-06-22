package com.vidz.data.websocket

import android.util.Log
import com.vidz.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageWebSocketService @Inject constructor(
    private val stompWebSocketClient: StompWebSocketClient
) {
    
    val messageFlow: Flow<Message> = stompWebSocketClient.messageFlow
        .onEach { message ->
            Log.d("MessageWebSocketService", "Received message in service: ${message.messageId} - '${message.content}'")
        }
    val connectionStatusFlow: Flow<StompWebSocketClient.ConnectionStatus> = stompWebSocketClient.connectionStatusFlow
    
    fun connect(userEmail: String, token: String): Boolean {
        // Pass userEmail to connect method for auto-subscription after STOMP connection
        return stompWebSocketClient.connect(token, userEmail)
    }
    
    fun disconnect() {
        stompWebSocketClient.disconnect()
    }
    
    fun sendMessage(conversationId: Long, messageContent: String, senderId: Long, token: String): Boolean {
        return stompWebSocketClient.sendMessage(conversationId, messageContent, senderId)
    }
} 