package com.vidz.domain.repository

import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface ShippingRepository {
    
    suspend fun createShippingInfo(shippingInfo: ShippingInfo): Flow<Result<ShippingInfo>>
    
    suspend fun getShippingInfoById(shippingInfoId: Long): Flow<Result<ShippingInfo>>
    
    suspend fun getShippingInfos(
        page: Int = 0,
        size: Int = 20,
        accountId: Long? = null
    ): Flow<Result<List<ShippingInfo>>>
    
    suspend fun updateShippingInfo(
        shippingInfoId: Long,
        shippingInfo: ShippingInfo
    ): Flow<Result<ShippingInfo>>
    
    suspend fun deleteShippingInfo(shippingInfoId: Long): Flow<Result<Unit>>
} 