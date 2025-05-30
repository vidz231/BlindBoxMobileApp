package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return cartRepository.clearCart()
    }
} 