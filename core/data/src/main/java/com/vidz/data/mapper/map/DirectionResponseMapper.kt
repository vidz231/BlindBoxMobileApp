package com.vidz.data.mapper.map
//
//import com.vidz.data.mapper.BaseRemoteMapper
//import com.vidz.data.server.retrofit.dto.map.DirectionResponse
//import com.vidz.domain.model.map.DirectionResponse as DomainDirectionResponse
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class DirectionResponseMapper @Inject constructor(
//    private val geocodedWaypointMapper: GeocodedWaypointMapper,
//    private val routeMapper: RouteMapper
//) : BaseRemoteMapper<DomainDirectionResponse, DirectionResponse> {
//
//    override fun toDomain(external: DirectionResponse): DomainDirectionResponse {
//        return DomainDirectionResponse(
//            geocodedWaypoints = external.geocodedWaypoints.map { geocodedWaypointMapper.toDomain(it) },
//            routes = external.routes.map { routeMapper.toDomain(it) }
//        )
//    }
//
//    override fun toRemote(domain: DomainDirectionResponse): DirectionResponse {
//        return DirectionResponse(
//            geocodedWaypoints = domain.geocodedWaypoints.map { geocodedWaypointMapper.toRemote(it) },
//            routes = domain.routes.map { routeMapper.toRemote(it) }
//        )
//    }
//}