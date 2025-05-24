package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.StockKeepingUnit
import kotlinx.coroutines.flow.Flow

interface SkuRepository {
    fun getSkus(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<StockKeepingUnit>>>
    
    fun getSkuById(skuId: Long): Flow<Result<StockKeepingUnit>>
}