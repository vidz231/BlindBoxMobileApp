package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class DirectionResponse(
    val geocodedWaypoints: List<GeocodedWaypoint> = emptyList(),
    val routes: List<Route> = emptyList()
) 