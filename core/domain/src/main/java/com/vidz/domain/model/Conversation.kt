package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val conversationId: Long = 0L,
    val user: Account = Account(),
    val staff: Account = Account(),
    val lastMessage: Message? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
) 