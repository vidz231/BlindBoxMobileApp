package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.NotificationDto
import com.vidz.domain.model.Notification

class NotificationMapper : BaseRemoteMapper<Notification, NotificationDto> {

    override fun toDomain(external: NotificationDto): Notification {
        return Notification(
            notificationId = external.notificationId,
            accountId = external.accountId,
            message = external.message,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            isRead = external.isRead
        )
    }

    override fun toRemote(domain: Notification): NotificationDto {
        return NotificationDto(
            notificationId = domain.notificationId,
            accountId = domain.accountId,
            message = domain.message,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isRead = domain.isRead
        )
    }
} 