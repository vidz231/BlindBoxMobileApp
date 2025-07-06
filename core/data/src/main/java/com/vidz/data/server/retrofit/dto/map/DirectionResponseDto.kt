package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class DirectionResponseDto(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypointDto> = emptyList(),
    @SerializedName("routes")
    val routes: List<Route> = emptyList()
) 