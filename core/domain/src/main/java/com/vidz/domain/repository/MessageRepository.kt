package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.Conversation
import com.vidz.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun findOrCreateConversation(userId: Long): Flow<Result<Conversation>>
    
    fun sendMessage(conversationId: Long, content: String): Flow<Result<Message>>
    
    fun observeMessages(conversationId: Long): Flow<List<Message>>
    
    fun connectWebSocket(userEmail: String, token: String): Flow<Result<Unit>>
    
    fun disconnectWebSocket()
    
    fun subscribeToMessages(userEmail: String): Flow<Message>
} 