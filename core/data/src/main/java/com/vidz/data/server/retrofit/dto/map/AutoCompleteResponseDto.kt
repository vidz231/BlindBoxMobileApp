package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class AutoCompleteResponseDto(
    @SerializedName("predictions")
    val predictions: List<AutoCompleteDto> = emptyList(),

    @SerializedName("executed_time")
    val executedTime: Int? = null,

    @SerializedName("status")
    val status: String = ""
)