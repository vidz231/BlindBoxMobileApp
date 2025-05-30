package com.vidz.domain.usecase

import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class CartSummary(
    val itemsCount: Int,
    val totalPrice: Double
)

class GetCartSummaryUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<CartSummary> {
        return combine(
            cartRepository.getCartItemsCount(),
            cartRepository.getTotalPrice()
        ) { count, totalPrice ->
            CartSummary(
                itemsCount = count,
                totalPrice = totalPrice
            )
        }
    }
}
 