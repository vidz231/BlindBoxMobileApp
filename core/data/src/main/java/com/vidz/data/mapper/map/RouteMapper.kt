package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Route
import com.vidz.domain.model.map.Route as DomainRoute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteMapper @Inject constructor(
    private val legMapper: LegMapper,
    private val polylineMapper: PolylineMapper
) : BaseRemoteMapper<DomainRoute, Route> {

    override fun toDomain(external: Route): DomainRoute {
        return DomainRoute(
            legs = external.legs.map { legMapper.toDomain(it) },
            overviewPolyline = external.overviewPolyline?.let { polylineMapper.toDomain(it) },
            summary = external.summary ?: ""
        )
    }

    override fun toRemote(domain: DomainRoute): Route {
        return Route(
            legs = domain.legs.map { legMapper.toRemote(it) },
            overviewPolyline = domain.overviewPolyline?.let { polylineMapper.toRemote(it) },
            summary = domain.summary
        )
    }
} 