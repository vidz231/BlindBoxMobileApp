package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.ToyDto
import com.vidz.data.server.retrofit.dto.ToyRarity as DtoToyRarity
import com.vidz.domain.model.Toy
import com.vidz.domain.model.ToyRarity

class ToyMapper(
    private val imageMapper: ImageMapper
) : BaseRemoteMapper<Toy, ToyDto> {

    override fun toDomain(external: ToyDto): Toy {
        return Toy(
            toyId = external.toyId,
            name = external.name,
            description = external.description,
            weight = external.weight,
            rarity = mapToyRarityToDomain(external.rarity),
            isVisible = external.isVisible,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            images = external.images.map { imageMapper.toDomain(it) }
        )
    }

    override fun toRemote(domain: Toy): ToyDto {
        return ToyDto(
            toyId = domain.toyId,
            name = domain.name,
            description = domain.description,
            weight = domain.weight,
            rarity = mapToyRarityToDto(domain.rarity),
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            images = domain.images.map { imageMapper.toRemote(it) }
        )
    }

    private fun mapToyRarityToDomain(dtoRarity: DtoToyRarity): ToyRarity {
        return when (dtoRarity) {
            DtoToyRarity.REGULAR -> ToyRarity.Regular
            DtoToyRarity.SECRET -> ToyRarity.Secret
        }
    }

    private fun mapToyRarityToDto(domainRarity: ToyRarity): DtoToyRarity {
        return when (domainRarity) {
            ToyRarity.Regular -> DtoToyRarity.REGULAR
            ToyRarity.Secret -> DtoToyRarity.SECRET
        }
    }
} 