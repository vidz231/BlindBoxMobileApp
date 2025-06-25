package com.vidz.domain.usecase

import com.vidz.domain.repository.ShippingRepository
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteShippingInfoUseCase @Inject constructor(
    private val shippingRepository: ShippingRepository
) {
    
    suspend operator fun invoke(shippingInfoId: Long): Flow<Result<Unit>> {
        return shippingRepository.deleteShippingInfo(shippingInfoId)
    }
} 