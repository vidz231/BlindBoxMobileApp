package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class Leg(
    val distance: KeyValue? = null,
    val duration: KeyValue? = null,
    val startLocation: Location? = null,
    val endLocation: Location? = null,
    val startAddress: String = "",
    val endAddress: String = "",
    val steps: List<Step> = emptyList()
) 