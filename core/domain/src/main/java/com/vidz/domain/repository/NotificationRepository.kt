package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null,
        filter: String? = null,
        search: String? = null
    ): Flow<Result<List<Notification>>>
    
    fun createNotification(notification: Notification): Flow<Result<Notification>>
    
    fun updateNotification(notificationId: Long, notification: Notification): Flow<Result<Notification>>
    
    fun markNotificationAsRead(notificationId: Long): Flow<Result<Notification>>
    
    fun deleteNotification(notificationId: Long): Flow<Result<Unit>>
}