package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.OrderMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.data.server.retrofit.api.OrderApi
import com.vidz.data.server.retrofit.api.CreateOrderRequest
import com.vidz.data.server.retrofit.api.OrderDetailRequest as ApiOrderDetailRequest
import com.vidz.data.server.retrofit.api.PagedResponse
import com.vidz.data.server.retrofit.dto.OrderDto
import com.vidz.domain.Result
import com.vidz.domain.model.OrderDto as Order
import com.vidz.domain.model.CreateOrderResult
import com.vidz.domain.repository.OrderRepository
import com.vidz.domain.repository.OrderDetailRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    val retrofitServer: RetrofitServer,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override fun getOrders(
        page: Int,
        size: Int,
        search: String?,
        filter: String?
    ): Flow<Result<List<Order>>> {
        return ServerFlow(
            getData = {
                retrofitServer.orderApi.getOrders(page, size, search, filter).body()!!
            },
            convert = { response: PagedResponse<OrderDto> ->
                response.content.map { orderMapper.toDomain(it) }
            }
        ).execute()
    }

    override fun getOrderById(orderId: Long): Flow<Result<Order>> {
        return ServerFlow(
            getData = {
                retrofitServer.orderApi.getOrderById(orderId).body()!!
            },
            convert = { orderDto ->
                orderMapper.toDomain(orderDto)
            }
        ).execute()
    }

    override fun createOrder(
        accountId: Long,
        shippingInfoId: Long,
        items: List<OrderDetailRequest>,
        voucherId: Long?
    ): Flow<Result<CreateOrderResult>> {
        return ServerFlow(
            getData = {
                val createRequest = CreateOrderRequest(
                    accountId = accountId,
                    shippingInfoId = shippingInfoId,
                    items = items.map { 
                        ApiOrderDetailRequest(
                            skuId = it.skuId,
                            quantity = it.quantity,
                            slotId = it.slotId
                        )
                    },
                    voucherId = voucherId
                )
                retrofitServer.orderApi.createOrder(createRequest, accountId).body()!!
            },
            convert = { response ->
                // Handle the PlaceOrder200Response which contains both order and payment URL
                response.order?.let { 
                    CreateOrderResult(
                        order = orderMapper.toDomain(it),
                        paymentRedirectUrl = response.paymentRedirectUrl
                    )
                } ?: throw IllegalStateException("Order creation failed - no order returned")
            }
        ).execute()
    }

    override fun cancelOrder(orderId: Long): Flow<Result<Order>> {
        return ServerFlow(
            getData = {
                retrofitServer.orderApi.cancelOrder(orderId).body()!!
            },
            convert = { orderDto ->
                orderMapper.toDomain(orderDto)
            }
        ).execute()
    }
} 