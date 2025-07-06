package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.PlaceDetailResponseDto
import com.vidz.domain.model.map.PlaceDetailResponse as DomainPlaceDetailResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceDetailResponseMapper @Inject constructor(
    private val placeDetailMapper: PlaceDetailMapper
) : BaseRemoteMapper<DomainPlaceDetailResponse, PlaceDetailResponseDto> {

    override fun toDomain(external: PlaceDetailResponseDto): DomainPlaceDetailResponse {
        return DomainPlaceDetailResponse(
            result = external.result?.let { placeDetailMapper.toDomain(it) },
            status = external.status
        )
    }

    override fun toRemote(domain: DomainPlaceDetailResponse): PlaceDetailResponseDto {
        return PlaceDetailResponseDto(
            result = domain.result?.let { placeDetailMapper.toRemote(it) },
            status = domain.status
        )
    }
} 