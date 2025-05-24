package com.vidz.data.server.retrofit.dto

data class GetShippingInfos200Response(
    val content: List<ShippingInfoDto> = emptyList(),
    val totalElements: Long = 0L,
    val totalPages: Int = 0,
    val last: Boolean = false,
    val first: Boolean = false,
    val numberOfElements: Int = 0,
    val empty: Boolean = false
) 