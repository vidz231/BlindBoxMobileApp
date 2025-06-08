package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.OrderDto
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<OrderDto>>> {
        return orderRepository.getOrders(page, size, search, filter)
    }
}