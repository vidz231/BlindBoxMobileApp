package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.VideoDto
import com.vidz.data.server.retrofit.dto.GetVideos200Response
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface VideoApi {

    @Multipart
    @POST("videos")
    suspend fun uploadVideo(
        @Part("slotId") slotId: Int? = null,
        @Part videoBlob: MultipartBody.Part? = null
    ): Response<VideoDto>

    @GET("videos/{videoId}")
    suspend fun getVideoById(
        @Path("videoId") videoId: Long
    ): Response<VideoDto>

    @GET("videos")
    suspend fun getVideos(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetVideos200Response>

    @Multipart
    @PUT("videos/{videoId}")
    suspend fun updateVideo(
        @Path("videoId") videoId: Long,
        @Part("slotId") slotId: Int? = null,
        @Part videoBlob: MultipartBody.Part? = null
    ): Response<VideoDto>

    @DELETE("videos/{videoId}")
    suspend fun deleteVideo(
        @Path("videoId") videoId: Long
    ): Response<Unit>
} 