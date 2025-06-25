package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.OrderStatusHistoryDto
import com.vidz.domain.model.OrderStatusHistory
import com.vidz.domain.model.OrderStatus
import javax.inject.Inject

class OrderStatusHistoryMapper @Inject constructor() : BaseRemoteMapper<OrderStatusHistory, OrderStatusHistoryDto> {

    override fun toDomain(external: OrderStatusHistoryDto): OrderStatusHistory {
        return OrderStatusHistory(
            id = external.id,
            orderId = external.orderId,
            state = OrderStatus.Created, // Will need to map from string/enum in DTO
            createdAt = external.createdAt
        )
    }

    override fun toRemote(domain: OrderStatusHistory): OrderStatusHistoryDto {
        return OrderStatusHistoryDto(
            id = domain.id,
            orderId = domain.orderId,
            createdAt = domain.createdAt
        )
    }
} 