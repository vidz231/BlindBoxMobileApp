package com.vidz.data.server.retrofit.dto

data class VideoDto(
    val videoId: Long = 0L,
    val videoUrl: String = "",
    val slotId: Int? = null,
    val uploader: AccountDto? = null,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 