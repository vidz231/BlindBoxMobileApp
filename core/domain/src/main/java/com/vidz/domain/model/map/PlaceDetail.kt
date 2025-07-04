package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetail(
    val placeId: String = "",
    val address: String = "",
    val geometry: Geometry? = null,
    val name: String = ""
) 