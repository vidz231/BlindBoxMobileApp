package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.CreateOrderResult
import com.vidz.domain.repository.OrderRepository
import com.vidz.domain.repository.OrderDetailRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        accountId: Long,
        shippingInfoId: Long,
        items: List<OrderDetailRequest>,
        voucherId: Long? = null
    ): Flow<Result<CreateOrderResult>> {
        return orderRepository.createOrder(accountId, shippingInfoId, items, voucherId)
    }
} 