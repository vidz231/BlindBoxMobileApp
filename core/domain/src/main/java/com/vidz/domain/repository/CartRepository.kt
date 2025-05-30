package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    
    fun observeCartItems(): Flow<List<CartItem>>
    
    suspend fun addToCart(cartItem: CartItem): Result<Unit>
    
    suspend fun removeFromCart(cartItemId: String): Result<Unit>
    
    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Result<Unit>
    
    suspend fun clearCart(): Result<Unit>
    
    suspend fun getCartItem(cartItemId: String): Result<CartItem?>
    
    suspend fun getCartItemsCount(): Flow<Int>
    
    suspend fun getTotalPrice(): Flow<Double>
} 