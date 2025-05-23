package com.vidz.data.server.retrofit.dto


data class TransactionDto(
    val transactionId: Long = 0L,
    val account: AccountDto = AccountDto(),
    val type: TransactionType = TransactionType.DEPOSIT,
    val paymentMethod: PaymentMethod = PaymentMethod.INTERNAL_WALLET,
    val createdAt: String = "",
    val updatedAt: String = "",
    val amount: Double = 0.0,
    val oldBalance: Double = 0.0,
    val newBalance: Double = 0.0,
    val orderId: Long = 0L,
    val status: TransactionStatus = TransactionStatus.PENDING,
)

enum class TransactionType {
    DEPOSIT, ORDER
}

enum class PaymentMethod {
    INTERNAL_WALLET, PAYPAL, VNPAY
}

enum class TransactionStatus {
    PENDING, SUCCESS, FAILED
} 