package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class MessageDto(
    val messageId: Long = 0L,
    val conversationId: Long = 0L,
    val sender: AccountDto = AccountDto(),
    val content: String = "",
    val createdAt: String = ""
) 