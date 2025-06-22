package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.ConversationDto
import com.vidz.domain.model.Conversation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationMapper @Inject constructor(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Conversation, ConversationDto> {

    override fun toDomain(external: ConversationDto): Conversation {
        return Conversation(
            conversationId = external.conversationId,
            user = accountMapper.toDomain(external.user),
            staff = accountMapper.toDomain(external.staff),
            lastMessage = null, // This can be populated separately if needed
            createdAt = "",
            updatedAt = ""
        )
    }

    override fun toRemote(domain: Conversation): ConversationDto {
        return ConversationDto(
            conversationId = domain.conversationId,
            user = accountMapper.toRemote(domain.user),
            staff = accountMapper.toRemote(domain.staff)
        )
    }
} 