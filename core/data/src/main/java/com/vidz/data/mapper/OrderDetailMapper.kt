package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.OrderDetailDto
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.model.Slot
import javax.inject.Inject

class OrderDetailMapper @Inject constructor(
    private val stockKeepingUnitMapper: StockKeepingUnitMapper,
    private val promotionalCampaignMapper: PromotionalCampaignMapper,
) : BaseRemoteMapper<OrderDetail, OrderDetailDto> {

    override fun toDomain(external: OrderDetailDto): OrderDetail {
        return OrderDetail(
            orderDetailId = external.orderDetailId,
            orderId = external.orderId,
            sku = stockKeepingUnitMapper.toDomain(external.sku),
            quantity = external.quantity,
            promotionalCampaign = external.promotionalCampaign?.let { 
                promotionalCampaignMapper.toDomain(it) 
            },
            unitPrice = external.unitPrice,
            subTotal = external.subTotal,
            finalTotal = external.finalTotal,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: OrderDetail): OrderDetailDto {
        return OrderDetailDto(
            orderDetailId = domain.orderDetailId,
            orderId = domain.orderId,
            sku = stockKeepingUnitMapper.toRemote(domain.sku),
            quantity = domain.quantity,
            promotionalCampaign = domain.promotionalCampaign?.let { 
                promotionalCampaignMapper.toRemote(it) 
            },
            unitPrice = domain.unitPrice,
            subTotal = domain.subTotal,
            finalTotal = domain.finalTotal,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 