package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartItemId: String): Result<Unit> {
        return cartRepository.removeFromCart(cartItemId)
    }
} 