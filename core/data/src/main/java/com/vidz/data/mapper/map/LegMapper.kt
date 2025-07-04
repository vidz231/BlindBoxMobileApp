package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.Leg
import com.vidz.domain.model.map.Leg as DomainLeg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LegMapper @Inject constructor(
    private val keyValueMapper: KeyValueMapper,
    private val locationMapper: LocationMapper,
    private val stepMapper: StepMapper
) : BaseRemoteMapper<DomainLeg, Leg> {

    override fun toDomain(external: Leg): DomainLeg {
        return DomainLeg(
            distance = external.distance?.let { keyValueMapper.toDomain(it) },
            duration = external.duration?.let { keyValueMapper.toDomain(it) },
            startLocation = external.startLocation?.let { locationMapper.toDomain(it) },
            endLocation = external.endLocation?.let { locationMapper.toDomain(it) },
            startAddress = external.startAddress ?: "",
            endAddress = external.endAddress ?: "",
            steps = external.steps.map { stepMapper.toDomain(it) }
        )
    }

    override fun toRemote(domain: DomainLeg): Leg {
        return Leg(
            distance = domain.distance?.let { keyValueMapper.toRemote(it) },
            duration = domain.duration?.let { keyValueMapper.toRemote(it) },
            startLocation = domain.startLocation?.let { locationMapper.toRemote(it) },
            endLocation = domain.endLocation?.let { locationMapper.toRemote(it) },
            startAddress = domain.startAddress,
            endAddress = domain.endAddress,
            steps = domain.steps.map { stepMapper.toRemote(it) }
        )
    }
} 