package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.Notification
import com.vidz.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null,
        filter: String? = null,
        search: String? = null
    ): Flow<Result<List<Notification>>> {
        return notificationRepository.getNotifications(page, size, sort, filter, search)
    }
} 