package com.vidz.domain.repository
import com.vidz.domain.Result

import com.vidz.domain.model.BlindBox
import kotlinx.coroutines.flow.Flow


interface BlindBoxRepository {
    fun getBlindBoxById(blindBoxId: Long): Flow<Result<BlindBox>>
    fun getBlindBoxes(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<BlindBox>>>
}