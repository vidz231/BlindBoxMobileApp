package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.map.AutoCompleteResponse
import com.vidz.domain.model.map.PlaceDetailResponse
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getAutoComplete(
        input: String?
    ): Flow<Result<AutoCompleteResponse>>
    
    fun getPlaceDetail(
        placeId: String?
    ): Flow<Result<PlaceDetailResponse>>
//    fun getDirection(origin: String, destination: String, vehicle: String, apiKey: String): Result<DirectionResponse>
}