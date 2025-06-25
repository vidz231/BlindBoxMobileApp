package com.vidz.datastore.dao

import androidx.room.*
import com.vidz.datastore.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>
    
    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    suspend fun getCartItem(cartItemId: String): CartEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartEntity)
    
    @Update
    suspend fun updateCartItem(cartItem: CartEntity)
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int)
    
    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItem(cartItemId: String)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemsCount(): Flow<Int>
    
    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getTotalQuantity(): Flow<Int?>
    
    @Query("SELECT SUM(sku_price * quantity) FROM cart_items")
    fun getTotalPrice(): Flow<Double?>
    
    @Query("SELECT EXISTS(SELECT 1 FROM cart_items WHERE id = :cartItemId)")
    suspend fun cartItemExists(cartItemId: String): Boolean
} 
