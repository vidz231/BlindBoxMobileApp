package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName("distance")
    val distance: KeyValue? = null,
    @SerializedName("duration")
    val duration: KeyValue? = null,
    @SerializedName("end_location")
    val endLocation: Location? = null,
    @SerializedName("html_instructions")
    val htmlInstructions: String? = null,
    @SerializedName("polyline")
    val polyline: Polyline? = null,
    @SerializedName("start_location")
    val startLocation: Location? = null,
    @SerializedName("travel_mode")
    val travelMode: String? = null
) 