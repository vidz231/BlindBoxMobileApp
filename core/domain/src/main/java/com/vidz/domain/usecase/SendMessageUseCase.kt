package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.Message
import com.vidz.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: Long, content: String): Flow<Result<Message>> {
        return messageRepository.sendMessage(conversationId, content)
    }
} 