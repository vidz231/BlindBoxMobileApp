package com.vidz.domain.model


data class Notification(
    val notificationId: Long = 0L,
    val accountId: Long = 0L,
    val message: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val isRead: Boolean = false
) 