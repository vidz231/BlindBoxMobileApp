package com.vidz.domain.usecase

import com.vidz.domain.Init
import com.vidz.domain.Result
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.CartItem
import com.vidz.domain.model.StockKeepingUnit
import com.vidz.domain.model.Slot
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(
        sku: StockKeepingUnit,
        quantity: Int = 1,
        slot: Slot? = null
    ): Result<Unit> {
        val cartItemId = generateCartItemId(sku.skuId, slot?.slotId)
        
        // Check if item already exists in cart
        val existingItem = cartRepository.getCartItem(cartItemId)
        
        return when (existingItem) {
            is Success -> {
                if (existingItem.data != null) {
                    // Update quantity if item exists
                    val newQuantity = existingItem.data.quantity + quantity
                    cartRepository.updateCartItemQuantity(cartItemId, newQuantity)
                } else {
                    // Add new item
                    val cartItem = CartItem(
                        id = cartItemId,
                        sku = sku,
                        quantity = quantity,
                        slot = slot
                    )
                    cartRepository.addToCart(cartItem)
                }
            }
            else -> {
                // Add new item if error getting existing item or init state
                val cartItem = CartItem(
                    id = cartItemId,
                    sku = sku,
                    quantity = quantity,
                    slot = slot
                )
                cartRepository.addToCart(cartItem)
            }
        }
    }
    
    private fun generateCartItemId(skuId: Long, slotId: Long?): String {
        return if (slotId != null) {
            "${skuId}_${slotId}"
        } else {
            "${skuId}"
        }
    }
} 