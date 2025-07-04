package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.GeocodedWaypointDto
import com.vidz.domain.model.map.GeocodedWaypoint as DomainGeocodedWaypoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodedWaypointMapper @Inject constructor() :
    BaseRemoteMapper<DomainGeocodedWaypoint, GeocodedWaypointDto> {

    override fun toDomain(external: GeocodedWaypointDto): DomainGeocodedWaypoint {
        return DomainGeocodedWaypoint(
            status = external.status ?: "",
            placeId = external.placeId ?: ""
        )
    }

    override fun toRemote(domain: DomainGeocodedWaypoint): GeocodedWaypointDto {
        return GeocodedWaypointDto(
            status = domain.status,
            placeId = domain.placeId
        )
    }
} 