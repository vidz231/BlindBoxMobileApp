package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val videoId: Long = 0L,
    val account: Account = Account(),
    val slotId: Long = 0L,
    val url: String = "",
    val description: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVerified: Boolean = false
) 