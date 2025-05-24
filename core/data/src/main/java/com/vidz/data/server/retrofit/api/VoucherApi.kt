package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.VoucherDto
import com.vidz.data.server.retrofit.dto.GetVouchers200Response
import retrofit2.Response
import retrofit2.http.*

interface VoucherApi {

    @POST("vouchers")
    suspend fun createVoucher(
        @Body voucherDto: VoucherDto
    ): Response<VoucherDto>

    @GET("vouchers/{voucherId}")
    suspend fun getVoucherById(
        @Path("voucherId") voucherId: Long
    ): Response<VoucherDto>

    @GET("vouchers")
    suspend fun getVouchers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetVouchers200Response>

    @PUT("vouchers/{voucherId}")
    suspend fun updateVoucher(
        @Path("voucherId") voucherId: Long,
        @Body voucherDto: VoucherDto
    ): Response<VoucherDto>

    @DELETE("vouchers/{voucherId}")
    suspend fun deleteVoucher(
        @Path("voucherId") voucherId: Long
    ): Response<Unit>

    @POST("vouchers/{voucherId}/validate")
    suspend fun validateVoucher(
        @Path("voucherId") voucherId: Long
    ): Response<VoucherDto>
} 