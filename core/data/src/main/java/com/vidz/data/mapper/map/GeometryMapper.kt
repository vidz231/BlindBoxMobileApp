package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Geometry
import com.vidz.domain.model.map.Geometry as DomainGeometry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeometryMapper @Inject constructor(
    private val locationMapper: LocationMapper
) : BaseRemoteMapper<DomainGeometry, Geometry> {

    override fun toDomain(external: Geometry): DomainGeometry {
        return DomainGeometry(
            location = external.location?.let { locationMapper.toDomain(it) }
        )
    }

    override fun toRemote(domain: DomainGeometry): Geometry {
        return Geometry(
            location = domain.location?.let { locationMapper.toRemote(it) }
        )
    }
} 