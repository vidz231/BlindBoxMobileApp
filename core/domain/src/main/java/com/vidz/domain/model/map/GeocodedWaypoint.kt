package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class GeocodedWaypoint(
    val status: String = "",
    val placeId: String = ""
) 