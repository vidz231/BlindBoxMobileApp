package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartItemId: String, quantity: Int): Result<Unit> {
        return cartRepository.updateCartItemQuantity(cartItemId, quantity)
    }
} 