package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.OrderDto
import com.vidz.data.server.retrofit.dto.OrderStatus as DtoOrderStatus
import com.vidz.domain.model.OrderDto as Order
import com.vidz.domain.model.OrderStatus
import com.vidz.domain.model.Transaction
import com.vidz.domain.model.Voucher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderMapper @Inject constructor(
    private val accountMapper: AccountMapper,
    private val orderStatusHistoryMapper: OrderStatusHistoryMapper,
    private val orderDetailMapper: OrderDetailMapper,
    private val transactionMapper: TransactionMapper,
    private val shippingInfoMapper: ShippingInfoMapper
) : BaseRemoteMapper<Order, OrderDto> {

    override fun toDomain(external: OrderDto): Order {
        return Order(
            orderId = external.orderId,
            account = accountMapper.toDomain(external.account),
            orderStatusHistories = external.orderStatusHistories.map { 
                orderStatusHistoryMapper.toDomain(it) 
            },
            latestStatus = mapOrderStatusToDomain(external.latestStatus),
            orderDetails = external.orderDetails.map { orderDetailMapper.toDomain(it) },
            transaction = external.transaction?.let { transactionMapper.toDomain(it) } ?: Transaction(),
            shippingInfo = shippingInfoMapper.toDomain(external.shippingInfo),
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            subTotal = external.subTotal,
            finalTotal = external.finalTotal
        )
    }

    override fun toRemote(domain: Order): OrderDto {
        return OrderDto(
            orderId = domain.orderId,
            account = accountMapper.toRemote(domain.account),
            orderStatusHistories = domain.orderStatusHistories.map { 
                orderStatusHistoryMapper.toRemote(it) 
            },
            latestStatus = mapOrderStatusToDto(domain.latestStatus),
            orderDetails = domain.orderDetails.map { orderDetailMapper.toRemote(it) },
            transaction = transactionMapper.toRemote(domain.transaction),
            shippingInfo = shippingInfoMapper.toRemote(domain.shippingInfo),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            subTotal = domain.subTotal,
            finalTotal = domain.finalTotal
        )
    }

    private fun mapOrderStatusToDomain(dtoStatus: DtoOrderStatus): OrderStatus {
        return when (dtoStatus) {
            DtoOrderStatus.CREATED -> OrderStatus.Created
            DtoOrderStatus.PREPARING -> OrderStatus.Preparing
            DtoOrderStatus.PAYMENT_FAILED -> OrderStatus.PaymentFailed
            DtoOrderStatus.PAYMENT_EXPIRED -> OrderStatus.PaymentExpired
            DtoOrderStatus.CANCELED -> OrderStatus.Canceled
            DtoOrderStatus.READY_FOR_PICKUP -> OrderStatus.ReadyForPickup
            DtoOrderStatus.SHIPPING -> OrderStatus.Shipping
            DtoOrderStatus.DELIVERED -> OrderStatus.Delivered
            DtoOrderStatus.RECEIVED -> OrderStatus.Received
            DtoOrderStatus.COMPLETED -> OrderStatus.Completed
        }
    }

    private fun mapOrderStatusToDto(domainStatus: OrderStatus): DtoOrderStatus {
        return when (domainStatus) {
            OrderStatus.Created -> DtoOrderStatus.CREATED
            OrderStatus.Preparing -> DtoOrderStatus.PREPARING
            OrderStatus.PaymentFailed -> DtoOrderStatus.PAYMENT_FAILED
            OrderStatus.PaymentExpired -> DtoOrderStatus.PAYMENT_EXPIRED
            OrderStatus.Canceled -> DtoOrderStatus.CANCELED
            OrderStatus.ReadyForPickup -> DtoOrderStatus.READY_FOR_PICKUP
            OrderStatus.Shipping -> DtoOrderStatus.SHIPPING
            OrderStatus.Delivered -> DtoOrderStatus.DELIVERED
            OrderStatus.Received -> DtoOrderStatus.RECEIVED
            OrderStatus.Completed -> DtoOrderStatus.COMPLETED
        }
    }
} 
