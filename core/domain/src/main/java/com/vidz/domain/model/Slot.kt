package com.vidz.domain.model

data class Slot(
    val slotId: Long = 0L,
    val position: Int = 0,
    val state: SlotState = SlotState.Available,
    val isVisible: Boolean = true,
    val openedAt: String = "",
    val toy: Toy = Toy(),
    val setId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = "",
    val video: Video = Video()
)

sealed class SlotState {
    data object Opened : SlotState()
    data object Available : SlotState()
    data object Reserved : SlotState()
} 