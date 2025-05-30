package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
sealed class SlotState {
    @Serializable
    data object Opened : SlotState()
    @Serializable
    data object Available : SlotState()
    @Serializable
    data object Reserved : SlotState()
} 