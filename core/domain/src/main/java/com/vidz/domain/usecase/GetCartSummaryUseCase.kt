package com.vidz.domain.usecase

import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class CartSummary(
    val itemsCount: Int,
    val totalQuantity: Int,
    val totalPrice: Double
)

class GetCartSummaryUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<CartSummary> {
        return combine(
            cartRepository.getCartItemsCount(),
            cartRepository.getTotalPrice(),
            cartRepository.observeCartItems()
        ) { count, totalPrice, cartItems ->
            val totalQuantity = cartItems.sumOf { it.quantity }
            CartSummary(
                itemsCount = count,
                totalQuantity = totalQuantity,
                totalPrice = totalPrice
            )
        }
    }
}
 