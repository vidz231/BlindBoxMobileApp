package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConversationDto(
    val conversationId: Long = 0L,
    val user: AccountDto = AccountDto(),
    val staff: AccountDto = AccountDto()
) 