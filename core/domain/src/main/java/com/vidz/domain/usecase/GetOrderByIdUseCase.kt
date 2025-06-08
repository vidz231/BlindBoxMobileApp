package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.OrderDto
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        orderId : Long
    ): Flow<Result<OrderDto>> {
        return orderRepository.getOrderById(orderId)
    }
}