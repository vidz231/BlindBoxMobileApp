package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.TransactionDto
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {

    @GET("transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<TransactionDto>>

    @GET("transactions/{transactionId}")
    suspend fun getTransactionById(
        @Path("transactionId") transactionId: Long
    ): Response<TransactionDto>

    @POST("transactions")
    suspend fun createTransaction(
        @Body createRequest: CreateTransactionRequest
    ): Response<TransactionDto>
}

// Request DTOs
data class CreateTransactionRequest(
    val accountId: Long,
    val type: String, // DEPOSIT or ORDER
    val paymentMethod: String, // INTERNAL_WALLET, PAYPAL, VNPAY
    val amount: Double,
    val orderId: Long? = null
) 