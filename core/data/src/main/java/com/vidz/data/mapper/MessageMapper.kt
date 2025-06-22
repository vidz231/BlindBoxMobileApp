package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.MessageDto
import com.vidz.domain.model.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageMapper @Inject constructor(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Message, MessageDto> {

    override fun toDomain(external: MessageDto): Message {
        return Message(
            messageId = external.messageId,
            conversationId = external.conversationId,
            sender = accountMapper.toDomain(external.sender),
            content = external.content,
            createdAt = external.createdAt,
            isFromCurrentUser = false // This will be set based on current user context
        )
    }

    override fun toRemote(domain: Message): MessageDto {
        return MessageDto(
            messageId = domain.messageId,
            conversationId = domain.conversationId,
            sender = accountMapper.toRemote(domain.sender),
            content = domain.content,
            createdAt = domain.createdAt
        )
    }
} 