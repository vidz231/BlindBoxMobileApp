package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class PlaceDetail(
    @SerializedName("place_id")
    val placeId: String? = null,
    @SerializedName("formatted_address")
    val address: String? = null,
    @SerializedName("geometry")
    val geometry: Geometry? = null,
    @SerializedName("name")
    val name: String? = null
) 