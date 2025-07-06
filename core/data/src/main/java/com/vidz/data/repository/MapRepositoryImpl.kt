package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.map.AutoCompleteResponseMapper
import com.vidz.data.mapper.map.PlaceDetailResponseMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.data.server.retrofit.dto.map.AutoCompleteResponseDto
import com.vidz.data.server.retrofit.dto.map.PlaceDetailResponseDto
import com.vidz.domain.Result
import com.vidz.domain.model.map.AutoCompleteResponse
import com.vidz.domain.model.map.PlaceDetailResponse
import com.vidz.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.vidz.blindbox.core.data.BuildConfig

@Singleton
class MapRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer,
    private val autoCompleteResponseMapper: AutoCompleteResponseMapper,
    private val placeDetailResponseMapper: PlaceDetailResponseMapper
) : MapRepository {
    
    override fun getAutoComplete(
        input: String?
    ): Flow<Result<AutoCompleteResponse>> {
        return ServerFlow(
            getData = {
                retrofitServer.mapApi.getAutoComplete(input, BuildConfig.GOONG_API_KEY)
                    ?.body() ?: AutoCompleteResponseDto()
            },
            convert = { response ->
                autoCompleteResponseMapper.toDomain(response)
            }
        ).execute()
    }
    
    override fun getPlaceDetail(
        placeId: String?
    ): Flow<Result<PlaceDetailResponse>> {
        return ServerFlow(
            getData = {
                retrofitServer.mapApi.getPlaceDetail(placeId, BuildConfig.GOONG_API_KEY)
                    ?.body() ?: PlaceDetailResponseDto()
            },
            convert = { response ->
                placeDetailResponseMapper.toDomain(response)
            }
        ).execute()
    }
}