package com.vidz.domain.model


data class Transaction(
    val transactionId: Long = 0L,
    val account: Account = Account(),
    val type: TransactionType = TransactionType.Deposit,
    val paymentMethod: PaymentMethod = PaymentMethod.InternalWallet,
    val createdAt: String = "",
    val updatedAt: String = "",
    val amount: Double = 0.0,
    val oldBalance: Double = 0.0,
    val newBalance: Double = 0.0,
    val orderId: Long = 0L,
    val status: TransactionStatus = TransactionStatus.Pending,
)

sealed class TransactionType {
    data object Deposit : TransactionType()
    data object Order : TransactionType()
}

sealed class PaymentMethod {
    data object InternalWallet : PaymentMethod()
    data object Paypal : PaymentMethod()
    data object Vnpay : PaymentMethod()
}

sealed class TransactionStatus {
    data object Pending : TransactionStatus()
    data object Success : TransactionStatus()
    data object Failed : TransactionStatus()
} 