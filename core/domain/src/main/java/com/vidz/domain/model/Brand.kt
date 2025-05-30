package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Brand(
    val brandId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 