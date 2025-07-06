package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class KeyValue(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("value")
    val value: Double? = null
) 