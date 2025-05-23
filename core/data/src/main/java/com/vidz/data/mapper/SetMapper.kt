package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.SetDto
import com.vidz.domain.model.SetDto as Set

class SetMapper(
    private val stockKeepingUnitMapper: StockKeepingUnitMapper,
    private val slotMapper: SlotMapper,
    private val blindBoxMapper: BlindBoxMapper
) : BaseRemoteMapper<Set, SetDto> {

    override fun toDomain(external: SetDto): Set {
        return Set(
            setId = external.setId,
            sku = stockKeepingUnitMapper.toDomain(external.sku),
            isVisible = external.isVisible,
            slots = external.slots.map { slotMapper.toDomain(it) },
            blindBox = blindBoxMapper.toDomain(external.blindBox),
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: Set): SetDto {
        return SetDto(
            setId = domain.setId,
            sku = stockKeepingUnitMapper.toRemote(domain.sku),
            isVisible = domain.isVisible,
            slots = domain.slots.map { slotMapper.toRemote(it) },
            blindBox = blindBoxMapper.toRemote(domain.blindBox),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 