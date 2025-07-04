package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailResponse(
    val result: PlaceDetail? = null,
    val status: String = ""
) 