package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Location
import com.vidz.domain.model.map.Location as DomainLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationMapper @Inject constructor() : BaseRemoteMapper<DomainLocation, Location> {

    override fun toDomain(external: Location): DomainLocation {
        return DomainLocation(
            lat = external.lat?.toDoubleOrNull() ?: 0.0,
            lng = external.lng?.toDoubleOrNull() ?: 0.0
        )
    }

    override fun toRemote(domain: DomainLocation): Location {
        return Location(
            lat = domain.lat.toString(),
            lng = domain.lng.toString()
        )
    }
} 