package com.vidz.data.server.retrofit.dto

data class SetDto(
    val setId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val totalToys: Int = 0,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val toys: List<ToyDto> = emptyList()
) 