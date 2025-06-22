package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.Conversation
import com.vidz.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindOrCreateConversationUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(userId: Long): Flow<Result<Conversation>> {
        return messageRepository.findOrCreateConversation(userId)
    }
} 