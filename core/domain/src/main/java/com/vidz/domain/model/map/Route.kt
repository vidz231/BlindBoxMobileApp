package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val legs: List<Leg> = emptyList(),
    val overviewPolyline: Polyline? = null,
    val summary: String = ""
) 