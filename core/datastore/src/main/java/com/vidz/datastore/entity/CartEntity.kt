package com.vidz.datastore.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidz.domain.model.CartItem
import com.vidz.domain.model.Slot
import com.vidz.domain.model.StockKeepingUnit

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey
    val id: String,
    
    // SKU fields embedded
    @Embedded(prefix = "sku_")
    val sku: StockKeepingUnitEntity,
    
    val quantity: Int,
    
    // Slot fields embedded (nullable)
    @Embedded(prefix = "slot_")
    val slot: SlotEntity?,
    
    val addedAt: Long
)

// Simplified SKU entity for cart storage
data class StockKeepingUnitEntity(
    val skuId: Long,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val isVisible: Boolean
)

// Simplified Slot entity for cart storage
data class SlotEntity(
    val slotId: Long,
    val position: Int,
    val isVisible: Boolean
)

// Extension functions to convert between domain and entity
fun CartItem.toEntity(): CartEntity {
    return CartEntity(
        id = this.id,
        sku = StockKeepingUnitEntity(
            skuId = this.sku.skuId,
            name = this.sku.name,
            price = this.sku.price,
            imageUrl = this.sku.image.imageUrl,
            isVisible = this.sku.isVisible
        ),
        quantity = this.quantity,
        slot = this.slot?.let { slot ->
            SlotEntity(
                slotId = slot.slotId,
                position = slot.position,
                isVisible = slot.isVisible
            )
        },
        addedAt = this.addedAt
    )
}

fun CartEntity.toDomain(): CartItem {
    return CartItem(
        id = this.id,
        sku = StockKeepingUnit(
            skuId = this.sku.skuId,
            name = this.sku.name,
            price = this.sku.price,
            image = com.vidz.domain.model.Image(imageUrl = this.sku.imageUrl),
            isVisible = this.sku.isVisible
        ),
        quantity = this.quantity,
        slot = this.slot?.let { slot ->
            Slot(
                slotId = slot.slotId,
                position = slot.position,
                isVisible = slot.isVisible
            )
        },
        addedAt = this.addedAt
    )
} 