package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.map.PlaceDetailResponse
import com.vidz.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaceDetailUseCase @Inject constructor(
    private val mapRepository: MapRepository
) {
    operator fun invoke(
        placeId: String?
    ): Flow<Result<PlaceDetailResponse>> {
        return mapRepository.getPlaceDetail(placeId)
    }
} 