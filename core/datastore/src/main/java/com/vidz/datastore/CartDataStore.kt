package com.vidz.datastore

import com.vidz.datastore.dao.CartDao
import com.vidz.datastore.entity.toDomain
import com.vidz.datastore.entity.toEntity
import com.vidz.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartDataStore @Inject constructor(
    private val cartDao: CartDao
) {

    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addCartItem(cartItem: CartItem) {
        cartDao.insertCartItem(cartItem.toEntity())
    }

    suspend fun removeCartItem(cartItemId: String) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        cartDao.updateCartItemQuantity(cartItemId, quantity)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun getCartItem(cartItemId: String): CartItem? {
        return cartDao.getCartItem(cartItemId)?.toDomain()
    }

    fun getCartItemsCount(): Flow<Int> {
        return cartDao.getCartItemsCount()
    }

    fun getTotalQuantity(): Flow<Int> {
        return cartDao.getTotalQuantity().map { it ?: 0 }
    }

    fun getTotalPrice(): Flow<Double> {
        return cartDao.getTotalPrice().map { it ?: 0.0 }
    }

    suspend fun cartItemExists(cartItemId: String): Boolean {
        return cartDao.cartItemExists(cartItemId)
    }
} 