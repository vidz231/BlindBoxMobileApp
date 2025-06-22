package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectWebSocketUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(userEmail: String, token: String): Flow<Result<Unit>> {
        return messageRepository.connectWebSocket( userEmail, token)
    }
} 