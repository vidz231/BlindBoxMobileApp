package com.vidz.data.repository

import android.util.Log
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.ConversationMapper
import com.vidz.data.mapper.MessageMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.data.server.retrofit.api.PagedResponse
import com.vidz.data.server.retrofit.dto.SendMessageRequest
import com.vidz.data.websocket.MessageWebSocketService
import com.vidz.domain.Result
import com.vidz.domain.Success
import com.vidz.domain.ServerError
import com.vidz.domain.model.Conversation
import com.vidz.domain.model.Message
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer,
    private val conversationMapper: ConversationMapper,
    private val messageMapper: MessageMapper,
    private val webSocketService: MessageWebSocketService,
    private val authRepository: AuthRepository
) : MessageRepository {

    // Cache for messages per conversation
    private val messagesCache = MutableStateFlow<Map<Long, List<Message>>>(emptyMap())
    
    // Trigger for forcing UI updates
    private val messageUpdateTrigger = MutableStateFlow(0L)
    
    // Track if WebSocket listener is already started
    private var webSocketListenerStarted = false

    override fun findOrCreateConversation(userId: Long): Flow<Result<Conversation>> {
        return ServerFlow(
            getData = {
                retrofitServer.messageApi.findOrCreateConversation(userId)
                    .body() ?: throw Exception("Failed to get conversation")
            },
            convert = { conversationDto ->
                conversationMapper.toDomain(conversationDto)
            }
        ).execute()
    }

    override fun sendMessage(conversationId: Long, content: String): Flow<Result<Message>> {
        return ServerFlow(
            getData = {
                val user = runBlocking {
                    withTimeoutOrNull(10000) { // 10 second timeout
                        try {
                            authRepository.getCurrentUser().firstOrNull()
                        } catch (e: Exception) {
                            null // Return null on any exception
                        }
                    }
                }
                val request = when(user){
                    is Success -> {
                        SendMessageRequest(content = content, senderId = user.data.accountId, conversationId = conversationId)
                    }
                    else ->
                        SendMessageRequest(content = content, senderId = -1, conversationId = conversationId)

                }
                retrofitServer.messageApi.sendMessage(request)
                    .body() ?: throw Exception("Failed to send message")
            },
            convert = { messageDto ->
                messageMapper.toDomain(messageDto)
            }
        ).execute()
    }

    override fun observeMessages(conversationId: Long): Flow<List<Message>> {
        // Start listening to WebSocket messages immediately
        startWebSocketListener()
        
        return combine(
            messagesCache,
            messageUpdateTrigger
        ) { cache, _ ->
            cache[conversationId] ?: emptyList()
        }.onStart {
            // Fetch initial messages from API
            try {
                Log.d("MessageRepositoryImpl", "Fetching initial messages for conversation $conversationId")
                val response = retrofitServer.messageApi.getMessages(conversationId)
                if (response.isSuccessful) {
                    val messageDtos = response.body() ?: PagedResponse()
                    val messages = messageDtos.content.map { messageMapper.toDomain(it) }
                        .sortedBy { it.createdAt }
                    
                    // Update cache
                    messagesCache.value = messagesCache.value.toMutableMap().apply {
                        put(conversationId, messages)
                    }
                    
                    Log.d("MessageRepositoryImpl", "Loaded ${messages.size} initial messages for conversation $conversationId")
                    messageUpdateTrigger.value = System.currentTimeMillis()
                } else {
                    Log.e("MessageRepositoryImpl", "Failed to fetch messages: ${response.errorBody()?.string()}")
                    messagesCache.value = messagesCache.value.toMutableMap().apply {
                        put(conversationId, emptyList())
                    }
                    messageUpdateTrigger.value = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                Log.e("MessageRepositoryImpl", "Error fetching initial messages", e)
                messagesCache.value = messagesCache.value.toMutableMap().apply {
                    put(conversationId, emptyList())
                }
                messageUpdateTrigger.value = System.currentTimeMillis()
            }
        }
    }
    
    private fun startWebSocketListener() {
        // Only start if not already started
        if (webSocketListenerStarted) {
            Log.d("MessageRepositoryImpl", "WebSocket listener already started")
            return
        }
        webSocketListenerStarted = true
        
        Log.d("MessageRepositoryImpl", "Starting WebSocket listener...")
        
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MessageRepositoryImpl", "WebSocket listener coroutine started, beginning collection")
            webSocketService.messageFlow.collect { newMessage ->
                Log.d("MessageRepositoryImpl", "Repository: Received message ${newMessage.messageId} for conversation ${newMessage.conversationId}")
                
                val currentMessages = messagesCache.value[newMessage.conversationId] ?: emptyList()
                
                // Check if message already exists (avoid duplicates)
                val messageExists = currentMessages.any { it.messageId == newMessage.messageId }
                if (!messageExists) {
                    val updatedMessages = (currentMessages + newMessage)
                        .distinctBy { it.messageId }
                        .sortedBy { it.createdAt }
                    
                    // Update cache
                    messagesCache.value = messagesCache.value.toMutableMap().apply {
                        put(newMessage.conversationId, updatedMessages)
                    }
                    
                    Log.d("MessageRepositoryImpl", "Repository: Added message ${newMessage.messageId} to conversation ${newMessage.conversationId}")
                    Log.d("MessageRepositoryImpl", "Repository: Updated messages count: ${updatedMessages.size}")
                    Log.d("MessageRepositoryImpl", "Repository: New message - Content: '${newMessage.content}', CreatedAt: '${newMessage.createdAt}'")
                    
                    // Trigger UI update
                    val oldTrigger = messageUpdateTrigger.value
                    val newTrigger = System.currentTimeMillis()
                    messageUpdateTrigger.value = newTrigger
                    Log.d("MessageRepositoryImpl", "Repository: Triggered UI update - old: $oldTrigger, new: $newTrigger")
                } else {
                    Log.d("MessageRepositoryImpl", "Repository: Message ${newMessage.messageId} already exists, skipping")
                }
            }
        }
    }
    


    override fun connectWebSocket(userEmail: String, token: String): Flow<Result<Unit>> {
        return flow {
            try {
                val connected = webSocketService.connect(userEmail, token)
                if (connected) {
                    emit(Success(Unit))
                } else {
                    emit(ServerError.General("Failed to connect to WebSocket"))
                }
            } catch (e: Exception) {
                emit(ServerError.General("WebSocket connection error: ${e.message}"))
            }
        }
    }

    override fun disconnectWebSocket() {
        webSocketService.disconnect()
    }

    override fun subscribeToMessages(userEmail: String): Flow<Message> {
        return webSocketService.messageFlow
    }
} 