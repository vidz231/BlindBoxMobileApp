package com.vidz.data.server.retrofit.dto

data class SlotDto(
    val slotId: Long = 0L,
    val position: Int = 0,
    val state: SlotState = SlotState.AVAILABLE,
    val isVisible: Boolean = true,
    val openedAt: String? = null,
    val toy: ToyDto? = null,
    val setId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = "",
    val video: VideoDto? = null
)

enum class SlotState {
    OPENED, AVAILABLE, RESERVED
} 