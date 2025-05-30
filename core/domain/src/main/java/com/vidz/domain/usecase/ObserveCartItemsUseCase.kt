package com.vidz.domain.usecase

import com.vidz.domain.model.CartItem
import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> {
        return cartRepository.observeCartItems()
    }
} 