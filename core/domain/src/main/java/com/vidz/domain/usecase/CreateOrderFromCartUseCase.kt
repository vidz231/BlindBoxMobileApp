package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.CreateOrderResult
import com.vidz.domain.repository.CartRepository
import com.vidz.domain.repository.OrderDetailRequest
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateOrderFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        accountId: Long,
        shippingInfoId: Long,
        voucherId: Long? = null
    ): Flow<Result<CreateOrderResult>> = flow {
        // Get current cart items
        val cartItems = cartRepository.observeCartItems().first()
        
        if (cartItems.isEmpty()) {
            emit(ServerError.General("Cart is empty"))
            return@flow
        }
        
        // Convert cart items to order detail requests
        val orderDetailRequests = cartItems.map { cartItem ->
            OrderDetailRequest(
                skuId = cartItem.sku.skuId,
                quantity = cartItem.quantity,
                slotId = cartItem.slot?.slotId
            )
        }
        
        // Create order
        orderRepository.createOrder(
            accountId = accountId,
            shippingInfoId = shippingInfoId,
            items = orderDetailRequests,
            voucherId = voucherId
        ).collect { result ->
            when (result) {
                is Success -> {
                    // Clear cart after successful order creation
                    cartRepository.clearCart()
                    emit(result)
                }
                else -> {
                    emit(result)
                }
            }
        }
    }
} 