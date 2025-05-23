package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.TransactionDto
import com.vidz.data.server.retrofit.dto.TransactionType as DtoTransactionType
import com.vidz.data.server.retrofit.dto.PaymentMethod as DtoPaymentMethod
import com.vidz.data.server.retrofit.dto.TransactionStatus as DtoTransactionStatus
import com.vidz.domain.model.Transaction
import com.vidz.domain.model.TransactionType
import com.vidz.domain.model.PaymentMethod
import com.vidz.domain.model.TransactionStatus

class TransactionMapper(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Transaction, TransactionDto> {

    override fun toDomain(external: TransactionDto): Transaction {
        return Transaction(
            transactionId = external.transactionId,
            account = accountMapper.toDomain(external.account),
            type = mapTransactionTypeToDomain(external.type),
            paymentMethod = mapPaymentMethodToDomain(external.paymentMethod),
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            amount = external.amount,
            oldBalance = external.oldBalance,
            newBalance = external.newBalance,
            orderId = external.orderId,
            status = mapTransactionStatusToDomain(external.status)
        )
    }

    override fun toRemote(domain: Transaction): TransactionDto {
        return TransactionDto(
            transactionId = domain.transactionId,
            account = accountMapper.toRemote(domain.account),
            type = mapTransactionTypeToDto(domain.type),
            paymentMethod = mapPaymentMethodToDto(domain.paymentMethod),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            amount = domain.amount,
            oldBalance = domain.oldBalance,
            newBalance = domain.newBalance,
            orderId = domain.orderId,
            status = mapTransactionStatusToDto(domain.status)
        )
    }

    private fun mapTransactionTypeToDomain(dtoType: DtoTransactionType): TransactionType {
        return when (dtoType) {
            DtoTransactionType.DEPOSIT -> TransactionType.Deposit
            DtoTransactionType.ORDER -> TransactionType.Order
        }
    }

    private fun mapTransactionTypeToDto(domainType: TransactionType): DtoTransactionType {
        return when (domainType) {
            TransactionType.Deposit -> DtoTransactionType.DEPOSIT
            TransactionType.Order -> DtoTransactionType.ORDER
        }
    }

    private fun mapPaymentMethodToDomain(dtoMethod: DtoPaymentMethod): PaymentMethod {
        return when (dtoMethod) {
            DtoPaymentMethod.INTERNAL_WALLET -> PaymentMethod.InternalWallet
            DtoPaymentMethod.PAYPAL -> PaymentMethod.Paypal
            DtoPaymentMethod.VNPAY -> PaymentMethod.Vnpay
        }
    }

    private fun mapPaymentMethodToDto(domainMethod: PaymentMethod): DtoPaymentMethod {
        return when (domainMethod) {
            PaymentMethod.InternalWallet -> DtoPaymentMethod.INTERNAL_WALLET
            PaymentMethod.Paypal -> DtoPaymentMethod.PAYPAL
            PaymentMethod.Vnpay -> DtoPaymentMethod.VNPAY
        }
    }

    private fun mapTransactionStatusToDomain(dtoStatus: DtoTransactionStatus): TransactionStatus {
        return when (dtoStatus) {
            DtoTransactionStatus.PENDING -> TransactionStatus.Pending
            DtoTransactionStatus.SUCCESS -> TransactionStatus.Success
            DtoTransactionStatus.FAILED -> TransactionStatus.Failed
        }
    }

    private fun mapTransactionStatusToDto(domainStatus: TransactionStatus): DtoTransactionStatus {
        return when (domainStatus) {
            TransactionStatus.Pending -> DtoTransactionStatus.PENDING
            TransactionStatus.Success -> DtoTransactionStatus.SUCCESS
            TransactionStatus.Failed -> DtoTransactionStatus.FAILED
        }
    }
} 