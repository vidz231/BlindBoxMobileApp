package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.ImageDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApi {

    @Multipart
    @POST("images")
    suspend fun uploadImage(
        @Part("skuId") skuId: Long? = null,
        @Part("blindBoxId") blindBoxId: Long? = null,
        @Part("toyId") toyId: Long? = null,
        @Part imageBlob: MultipartBody.Part? = null
    ): Response<ImageDto>

    @GET("images/{imageId}")
    suspend fun getImageById(
        @Path("imageId") imageId: Long
    ): Response<ImageDto>

    @Multipart
    @PUT("images/{imageId}")
    suspend fun updateImage(
        @Path("imageId") imageId: Long,
        @Part("skuId") skuId: Long? = null,
        @Part("blindBoxId") blindBoxId: Long? = null,
        @Part("toyId") toyId: Long? = null,
        @Part imageBlob: MultipartBody.Part? = null
    ): Response<ImageDto>

    @DELETE("images/{imageId}")
    suspend fun deleteImage(
        @Path("imageId") imageId: Long
    ): Response<Unit>
} 