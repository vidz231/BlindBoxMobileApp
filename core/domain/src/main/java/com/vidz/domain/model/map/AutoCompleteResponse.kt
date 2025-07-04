package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteResponse(
    val predictions: List<AutoComplete> = emptyList(),
    val executedTime: Int = 0,
    val status: String = ""
) 