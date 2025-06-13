package com.vidz.domain.usecase

import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.repository.ShippingRepository
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShippingInfosUseCase @Inject constructor(
    private val shippingRepository: ShippingRepository
) {
    
    suspend operator fun invoke(
        accountId: Long? = null,
        page: Int = 0,
        size: Int = 20
    ): Flow<Result<List<ShippingInfo>>> {
        return shippingRepository.getShippingInfos(
            page = page,
            size = size,
            accountId = accountId
        )
    }
} 