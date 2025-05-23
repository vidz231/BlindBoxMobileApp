package com.vidz.domain.model


data class Image(
    val imageId: Long = 0L,
    val uploader: AccountDto? = null,
    val blindBoxId: Long? = null,
    val toyId: Long? = null,
    val skuId: Long? = null,
    val imageUrl: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = ""
) 