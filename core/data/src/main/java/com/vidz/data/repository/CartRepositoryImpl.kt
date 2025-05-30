package com.vidz.data.repository

import com.vidz.datastore.CartDataStore
import com.vidz.domain.Result
import com.vidz.domain.Success
import com.vidz.domain.model.CartItem
import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDataStore: CartDataStore
) : CartRepository {

    override fun observeCartItems(): Flow<List<CartItem>> {
        return cartDataStore.getCartItems()
    }

    override suspend fun addToCart(cartItem: CartItem): Result<Unit> {
        return try {
            cartDataStore.addCartItem(cartItem)
            Success(Unit)
        } catch (e: Exception) {
            com.vidz.domain.ServerError.General(e.message ?: "Failed to add item to cart")
        }
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            cartDataStore.removeCartItem(cartItemId)
            Success(Unit)
        } catch (e: Exception) {
            com.vidz.domain.ServerError.General(e.message ?: "Failed to remove item from cart")
        }
    }

    override suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                cartDataStore.removeCartItem(cartItemId)
            } else {
                cartDataStore.updateCartItemQuantity(cartItemId, quantity)
            }
            Success(Unit)
        } catch (e: Exception) {
            com.vidz.domain.ServerError.General(e.message ?: "Failed to update cart item quantity")
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartDataStore.clearCart()
            Success(Unit)
        } catch (e: Exception) {
            com.vidz.domain.ServerError.General(e.message ?: "Failed to clear cart")
        }
    }

    override suspend fun getCartItem(cartItemId: String): Result<CartItem?> {
        return try {
            val cartItem = cartDataStore.getCartItem(cartItemId)
            Success(cartItem)
        } catch (e: Exception) {
            com.vidz.domain.ServerError.General(e.message ?: "Failed to get cart item")
        }
    }

    override suspend fun getCartItemsCount(): Flow<Int> {
        return cartDataStore.getTotalQuantity()
    }

    override suspend fun getTotalPrice(): Flow<Double> {
        return cartDataStore.getTotalPrice()
    }
} 