package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

// Route for DirectionResponse

data class Route(
    @SerializedName("bounds")
    val bounds: Any? = null,
    @SerializedName("legs")
    val legs: List<Leg> = emptyList(),
    @SerializedName("overview_polyline")
    val overviewPolyline: Polyline? = null,
    val summary: String? = null,
    @SerializedName("warnings")
    val warnings: List<Any> = emptyList(),
    @SerializedName("waypoint_order")
    val waypointOrder: List<Any> = emptyList()
) 