package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.OrderDto as Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<Order>>>
    
    fun getOrderById(orderId: Long): Flow<Result<Order>>
    
    fun createOrder(
        accountId: Long,
        shippingInfoId: Long,
        items: List<OrderDetailRequest>,
        voucherId: Long? = null
    ): Flow<Result<Order>>
    
    fun cancelOrder(orderId: Long): Flow<Result<Order>>
}

data class OrderDetailRequest(
    val skuId: Long,
    val quantity: Int,
    val slotId: Long? = null
)