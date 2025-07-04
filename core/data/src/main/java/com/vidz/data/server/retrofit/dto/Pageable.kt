package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pageable(
    val page: Int = 0,
    val size: Int = 20,
    val sort: List<String> = emptyList()
) 