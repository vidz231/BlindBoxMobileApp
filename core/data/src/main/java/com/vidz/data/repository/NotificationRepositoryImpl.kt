package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.NotificationMapper
import com.vidz.data.server.retrofit.api.NotificationApi
import com.vidz.domain.Result
import com.vidz.domain.model.Notification
import com.vidz.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    private val notificationMapper: NotificationMapper
) : NotificationRepository {

    override fun getNotifications(
        page: Int,
        size: Int,
        sort: List<String>?,
        filter: String?,
        search: String?
    ): Flow<Result<List<Notification>>> {
        return ServerFlow(
            getData = {
                notificationApi.getNotifications(page, size, sort, filter, search).body()!!
            },
            convert = { response ->
                response.content.map { notificationMapper.toDomain(it) }
            }
        ).execute()
    }

    override fun createNotification(notification: Notification): Flow<Result<Notification>> {
        return ServerFlow(
            getData = {
                notificationApi.createNotification(notificationMapper.toRemote(notification)).body()!!
            },
            convert = { notificationDto ->
                notificationMapper.toDomain(notificationDto)
            }
        ).execute()
    }

    override fun updateNotification(
        notificationId: Long,
        notification: Notification
    ): Flow<Result<Notification>> {
        return ServerFlow(
            getData = {
                notificationApi.updateNotification(notificationId, notificationMapper.toRemote(notification)).body()!!
            },
            convert = { notificationDto ->
                notificationMapper.toDomain(notificationDto)
            }
        ).execute()
    }

    override fun markNotificationAsRead(notificationId: Long): Flow<Result<Notification>> {
        // Create a minimal notification with just isRead set to true
        val readNotification = Notification(
            notificationId = notificationId,
            isRead = true
        )
        return updateNotification(notificationId, readNotification)
    }

    override fun deleteNotification(notificationId: Long): Flow<Result<Unit>> {
        // Note: The API doesn't seem to have a delete endpoint in NotificationApi
        // This would need to be added to the API or handled differently
        throw NotImplementedError("Delete notification endpoint not available in API")
    }
} 