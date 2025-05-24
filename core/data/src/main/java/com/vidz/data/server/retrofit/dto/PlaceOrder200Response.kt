package com.vidz.data.server.retrofit.dto

data class PlaceOrder200Response(
    val order: OrderDto? = null,
    val paymentRedirectUrl: String = ""
) 