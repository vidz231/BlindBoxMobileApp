package com.vidz.domain.usecase

import com.vidz.domain.model.Message
import com.vidz.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: Long): Flow<List<Message>> {
        return messageRepository.observeMessages(conversationId)
    }
    
    fun subscribeToMessages(userEmail: String): Flow<Message> {
        return messageRepository.subscribeToMessages(userEmail)
    }
} 