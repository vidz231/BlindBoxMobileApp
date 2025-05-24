package com.vidz.data.server.retrofit.dto

data class TrendingProductDto(
    val blindbox: BlindBoxDto? = null,
    val sku: StockKeepingUnitDto? = null,
    val totalSales: Int? = null
) 