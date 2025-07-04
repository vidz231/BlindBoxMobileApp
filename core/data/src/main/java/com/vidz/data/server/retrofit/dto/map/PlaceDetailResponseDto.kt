package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponseDto(
    @SerializedName("result")
    val result: PlaceDetail? = null,
    @SerializedName("status")
    val status: String = ""
) 