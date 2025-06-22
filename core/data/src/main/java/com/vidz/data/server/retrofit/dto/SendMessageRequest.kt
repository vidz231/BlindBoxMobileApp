package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    val content: String = "",
    val senderId: Long = 0L,
    val conversationId: Long = 0L
) 