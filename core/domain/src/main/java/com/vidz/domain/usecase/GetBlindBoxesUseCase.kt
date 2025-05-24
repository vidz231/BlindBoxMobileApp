package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.BlindBox
import com.vidz.domain.model.OrderDto
import com.vidz.domain.repository.BlindBoxRepository
import com.vidz.domain.repository.OrderDetailRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlindBoxesUseCase @Inject constructor(
    private val blindBoxRepository: BlindBoxRepository
) {
    operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        filter: String? = null
    ): Flow<Result<List<BlindBox>>> {
        return blindBoxRepository.getBlindBoxes(page, size, search, filter)
    }
}