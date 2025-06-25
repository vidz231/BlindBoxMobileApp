package com.vidz.domain.usecase

import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.repository.ShippingRepository
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShippingInfoByIdUseCase @Inject constructor(
    private val shippingRepository: ShippingRepository
) {
    
    suspend operator fun invoke(shippingInfoId: Long): Flow<Result<ShippingInfo>> {
        return shippingRepository.getShippingInfoById(shippingInfoId)
    }
} 