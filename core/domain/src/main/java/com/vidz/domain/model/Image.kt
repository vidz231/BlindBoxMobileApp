package com.vidz.domain.model



data class Image(
    val imageId: Long = 0L,
    val uploader: Account = Account(),
    val blindBoxId: Long = 0L,
    val toyId: Long = 0L,
    val skuId: Long = 0L,
    val imageUrl: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = ""
) 