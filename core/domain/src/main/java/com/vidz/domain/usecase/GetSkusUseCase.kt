package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.StockKeepingUnit
import com.vidz.domain.repository.SkuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSkusUseCase @Inject constructor(
    private val skuRepository: SkuRepository
) {
    operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<StockKeepingUnit>>> {
        return skuRepository.getSkus(page, size, search, filter)
    }
} 