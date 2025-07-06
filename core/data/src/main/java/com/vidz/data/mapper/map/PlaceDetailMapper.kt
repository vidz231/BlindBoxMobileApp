package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.PlaceDetail
import com.vidz.domain.model.map.PlaceDetail as DomainPlaceDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceDetailMapper @Inject constructor(
    private val geometryMapper: GeometryMapper
) : BaseRemoteMapper<DomainPlaceDetail, PlaceDetail> {

    override fun toDomain(external: PlaceDetail): DomainPlaceDetail {
        return DomainPlaceDetail(
            placeId = external.placeId ?: "",
            address = external.address ?: "",
            geometry = external.geometry?.let { geometryMapper.toDomain(it) },
            name = external.name ?: ""
        )
    }

    override fun toRemote(domain: DomainPlaceDetail): PlaceDetail {
        return PlaceDetail(
            placeId = domain.placeId,
            address = domain.address,
            geometry = domain.geometry?.let { geometryMapper.toRemote(it) },
            name = domain.name
        )
    }
} 