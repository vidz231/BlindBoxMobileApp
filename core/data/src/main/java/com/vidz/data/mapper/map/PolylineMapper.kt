package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Polyline
import com.vidz.domain.model.map.Polyline as DomainPolyline
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PolylineMapper @Inject constructor() : BaseRemoteMapper<DomainPolyline, Polyline> {

    override fun toDomain(external: Polyline): DomainPolyline {
        return DomainPolyline(
            points = external.points ?: ""
        )
    }

    override fun toRemote(domain: DomainPolyline): Polyline {
        return Polyline(
            points = domain.points
        )
    }
} 