package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.map.AutoCompleteResponse
import com.vidz.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAutoCompleteUseCase @Inject constructor(
    private val mapRepository: MapRepository
) {
    operator fun invoke(
        input: String?
    ): Flow<Result<AutoCompleteResponse>> {
        return mapRepository.getAutoComplete(input)
    }
} 