package com.vidz.domain.model


data class Video(
    val videoId: Long = 0L,
    val account: AccountDto? = null,
    val slotId: Long = 0L,
    val url: String = "",
    val description: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVerified: Boolean = false
) 