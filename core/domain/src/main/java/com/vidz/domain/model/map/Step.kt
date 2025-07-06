package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val distance: KeyValue? = null,
    val duration: KeyValue? = null,
    val endLocation: Location? = null,
    val htmlInstructions: String = "",
    val polyline: Polyline? = null,
    val startLocation: Location? = null,
    val travelMode: String = ""
) 