package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.SlotDto
import com.vidz.data.server.retrofit.dto.SlotState as DtoSlotState
import com.vidz.domain.model.Slot
import com.vidz.domain.model.SlotState
import com.vidz.domain.model.Toy
import com.vidz.domain.model.Video

class SlotMapper(
    private val toyMapper: ToyMapper,
    private val videoMapper: VideoMapper
) : BaseRemoteMapper<Slot, SlotDto> {

    override fun toDomain(external: SlotDto): Slot {
        return Slot(
            slotId = external.slotId,
            position = external.position,
            state = mapSlotStateToDomain(external.state),
            isVisible = external.isVisible,
            openedAt = external.openedAt.toString(),
            toy = external.toy?.let { toyMapper.toDomain(it) } ?: Toy(),
            setId = external.setId,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            video = external.video?.let { videoMapper.toDomain(it) } ?: Video()
        )
    }

    override fun toRemote(domain: Slot): SlotDto {
        return SlotDto(
            slotId = domain.slotId,
            position = domain.position,
            state = mapSlotStateToDto(domain.state),
            isVisible = domain.isVisible,
            openedAt = domain.openedAt,
            toy = toyMapper.toRemote(domain.toy),
            setId = domain.setId,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            video = videoMapper.toRemote(domain.video)
        )
    }

    private fun mapSlotStateToDomain(dtoState: DtoSlotState): SlotState {
        return when (dtoState) {
            DtoSlotState.OPENED -> SlotState.Opened
            DtoSlotState.AVAILABLE -> SlotState.Available
            DtoSlotState.RESERVED -> SlotState.Reserved
        }
    }

    private fun mapSlotStateToDto(domainState: SlotState): DtoSlotState {
        return when (domainState) {
            SlotState.Opened -> DtoSlotState.OPENED
            SlotState.Available -> DtoSlotState.AVAILABLE
            SlotState.Reserved -> DtoSlotState.RESERVED
        }
    }
} 