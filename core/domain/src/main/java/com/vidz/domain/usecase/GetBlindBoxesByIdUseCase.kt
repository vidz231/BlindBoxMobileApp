package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.BlindBox
import com.vidz.domain.repository.BlindBoxRepository
import kotlinx.coroutines.flow.Flow
import java.nio.file.Files.size
import javax.inject.Inject

class GetBlindBoxesByIdUseCase @Inject constructor(
    private val blindBoxRepository: BlindBoxRepository
) {
    operator fun invoke(
        blindBoxId : Long
    ): Flow<Result<BlindBox>> {
        return blindBoxRepository.getBlindBoxById(blindBoxId = blindBoxId)
    }
}