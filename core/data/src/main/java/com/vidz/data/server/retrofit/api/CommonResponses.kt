package com.vidz.data.server.retrofit.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
// Common response DTOs used across multiple APIs
data class PagedResponse<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 20,
    val totalElements: Long = 0L,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String = "",
    val data: T? = null,
    val errorCode: String? = null
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String = "",
    val message: String = "",
    val timestamp: String = "",
    val status: Int = 0
) 