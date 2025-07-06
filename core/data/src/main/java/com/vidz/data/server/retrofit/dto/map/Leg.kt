package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class Leg(
    @SerializedName("distance")
    val distance: KeyValue? = null,
    @SerializedName("duration")
    val duration: KeyValue? = null,
    @SerializedName("start_location")
    val startLocation: Location? = null,
    @SerializedName("end_location")
    val endLocation: Location? = null,
    @SerializedName("start_address")
    val startAddress: String? = null,
    @SerializedName("end_address")
    val endAddress: String? = null,
    @SerializedName("steps")
    val steps: List<Step> = emptyList()
) 