package com.vidz.domain.usecase

import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.repository.ShippingRepository
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateShippingInfoUseCase @Inject constructor(
    private val shippingRepository: ShippingRepository
) {
    
    suspend operator fun invoke(
        shippingInfoId: Long,
        shippingInfo: ShippingInfo
    ): Flow<Result<ShippingInfo>> {
        return shippingRepository.updateShippingInfo(shippingInfoId, shippingInfo)
    }
} 