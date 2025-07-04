package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class AutoComplete(
    val description: String = "",
    val placeId: String = "",
    val mainText: String = "",
    val secondaryText: String = ""
) 