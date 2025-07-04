package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class KeyValue(
    val text: String = "",
    val value: Double = 0.0
) 