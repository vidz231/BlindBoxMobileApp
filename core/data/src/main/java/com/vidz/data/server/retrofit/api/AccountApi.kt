package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.AccountDto
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {
    @GET("accounts")
    suspend fun getAccounts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<AccountDto>>

    @GET("accounts/{accountId}")
    suspend fun getAccountById(
        @Path("accountId") accountId: Long
    ): Response<AccountDto>

    @PUT("accounts/{accountId}")
    suspend fun updateAccount(
        @Path("accountId") accountId: Long,
        @Body updateRequest: UpdateAccountRequest
    ): Response<AccountDto>

    @DELETE("accounts/{accountId}")
    suspend fun deleteAccount(
        @Path("accountId") accountId: Long
    ): Response<Unit>
}

// Request DTOs
data class UpdateAccountRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
    val balance: Double? = null,
    val role: String? = null,
    val isVerified: Boolean? = null,
    val isVisible: Boolean? = null
)