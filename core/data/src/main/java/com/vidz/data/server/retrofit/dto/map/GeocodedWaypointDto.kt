package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class GeocodedWaypointDto(
    @SerializedName("geocoder_status")
    val status: String? = null,
    @SerializedName("place_id")
    val placeId: String? = null
) 