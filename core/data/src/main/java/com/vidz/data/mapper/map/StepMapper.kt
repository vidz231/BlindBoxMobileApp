package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Step
import com.vidz.domain.model.map.Step as DomainStep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepMapper @Inject constructor(
    private val keyValueMapper: KeyValueMapper,
    private val locationMapper: LocationMapper,
    private val polylineMapper: PolylineMapper
) : BaseRemoteMapper<DomainStep, Step> {

    override fun toDomain(external: Step): DomainStep {
        return DomainStep(
            distance = external.distance?.let { keyValueMapper.toDomain(it) },
            duration = external.duration?.let { keyValueMapper.toDomain(it) },
            endLocation = external.endLocation?.let { locationMapper.toDomain(it) },
            htmlInstructions = external.htmlInstructions ?: "",
            polyline = external.polyline?.let { polylineMapper.toDomain(it) },
            startLocation = external.startLocation?.let { locationMapper.toDomain(it) },
            travelMode = external.travelMode ?: ""
        )
    }

    override fun toRemote(domain: DomainStep): Step {
        return Step(
            distance = domain.distance?.let { keyValueMapper.toRemote(it) },
            duration = domain.duration?.let { keyValueMapper.toRemote(it) },
            endLocation = domain.endLocation?.let { locationMapper.toRemote(it) },
            htmlInstructions = domain.htmlInstructions,
            polyline = domain.polyline?.let { polylineMapper.toRemote(it) },
            startLocation = domain.startLocation?.let { locationMapper.toRemote(it) },
            travelMode = domain.travelMode
        )
    }
} 