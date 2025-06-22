package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val messageId: Long = 0L,
    val conversationId: Long = 0L,
    val sender: Account = Account(),
    val content: String = "",
    val createdAt: String = "",
    val isFromCurrentUser: Boolean = false
) 