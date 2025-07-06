package com.vidz.domain.model.map

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val location: Location? = null
) 