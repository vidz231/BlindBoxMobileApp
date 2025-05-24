package com.vidz.data.server.retrofit.dto

data class SlotDto(
    val slotId: Int = 0,
    val toy: ToyDto? = null,
    val isOpened: Boolean = false,
    val openedBy: AccountDto? = null,
    val openedAt: String = "",
    val setId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class SlotState {
    OPENED, AVAILABLE, RESERVED
} 