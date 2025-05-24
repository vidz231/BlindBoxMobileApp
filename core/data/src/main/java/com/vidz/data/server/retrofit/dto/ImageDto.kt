package com.vidz.data.server.retrofit.dto

data class ImageDto(
    val imageId: Long = 0L,
    val imageUrl: String = "",
    val uploader: AccountDto? = null,
    val skuId: Long? = null,
    val blindBoxId: Long? = null,
    val toyId: Long? = null,
    val isVisible: Boolean = true,
    val createdAt: String = ""
) 