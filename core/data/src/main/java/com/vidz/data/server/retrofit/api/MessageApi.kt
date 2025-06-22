package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.ConversationDto
import com.vidz.data.server.retrofit.dto.MessageDto
import com.vidz.data.server.retrofit.dto.SendMessageRequest
import retrofit2.Response
import retrofit2.http.*

interface MessageApi {
    
    @POST("conversations/conversations/find-or-create")
    suspend fun findOrCreateConversation(
        @Query("userId") userId: Long
    ): Response<ConversationDto>
    
    @POST("messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<MessageDto>
    
    @GET("messages/conversation/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<PagedResponse<MessageDto>>
} 