package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.data.server.retrofit.dto.GetAccounts200Response
import com.vidz.data.server.retrofit.dto.ErrorResponse
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {

    @POST("accounts")
    suspend fun createAccount(
        @Body accountDto: AccountDto
    ): Response<AccountDto>

    @GET("accounts/{accountId}")
    suspend fun getAccountById(
        @Path("accountId") accountId: Long
    ): Response<AccountDto>

    @GET("accounts")
    suspend fun getAccounts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetAccounts200Response>

    @PUT("accounts/{accountId}")
    suspend fun updateAccount(
        @Path("accountId") accountId: Long,
        @Body accountDto: AccountDto
    ): Response<AccountDto>

    @DELETE("accounts/{accountId}")
    suspend fun deleteAccount(
        @Path("accountId") accountId: Long
    ): Response<Unit>
} 