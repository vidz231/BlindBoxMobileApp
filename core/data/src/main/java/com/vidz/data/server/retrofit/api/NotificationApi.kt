package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.NotificationDto
import com.vidz.data.server.retrofit.dto.GetNotifications200Response
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {

    @POST("notifications")
    suspend fun createNotification(
        @Body notificationDto: NotificationDto
    ): Response<NotificationDto>

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetNotifications200Response>

    @PUT("notifications/{notificationId}")
    suspend fun updateNotification(
        @Path("notificationId") notificationId: Long,
        @Body notificationDto: NotificationDto
    ): Response<NotificationDto>
} 