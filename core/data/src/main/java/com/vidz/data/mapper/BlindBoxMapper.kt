package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.BlindBoxDto
import com.vidz.data.server.retrofit.dto.BlindBoxCampaignDto
import com.vidz.domain.model.BlindBox
import com.vidz.domain.model.BlindBoxCampaign

class BlindBoxMapper(
    private val brandMapper: BrandMapper,
    private val imageMapper: ImageMapper,
    private val toyMapper: ToyMapper,
    private val stockKeepingUnitMapper: StockKeepingUnitMapper
) : BaseRemoteMapper<BlindBox, BlindBoxDto> {

    override fun toDomain(external: BlindBoxDto): BlindBox {
        return BlindBox(
            blindBoxId = external.blindBoxId,
            brand = brandMapper.toDomain(external.brand),
            name = external.name,
            description = external.description,
            images = external.images.map { imageMapper.toDomain(it) },
            blindBoxCampaigns = external.blindBoxCampaigns.map { blindBoxCampaignDto ->
                BlindBoxCampaign(
                    blindBoxId = blindBoxCampaignDto.blindBoxId,
                    promotionalCampaignId = blindBoxCampaignDto.promotionalCampaignId,
                    createdAt = blindBoxCampaignDto.createdAt,
                    updatedAt = blindBoxCampaignDto.updatedAt,
                    isVisible = blindBoxCampaignDto.isVisible
                )
            },
            isVisible = external.isVisible,
            toys = external.toys.map { toyMapper.toDomain(it) },
            skus = external.skus.map { stockKeepingUnitMapper.toDomain(it) },
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: BlindBox): BlindBoxDto {
        return BlindBoxDto(
            blindBoxId = domain.blindBoxId,
            brand = brandMapper.toRemote(domain.brand),
            name = domain.name,
            description = domain.description,
            images = domain.images.map { imageMapper.toRemote(it) },
            blindBoxCampaigns = domain.blindBoxCampaigns.map { blindBoxCampaign ->
                BlindBoxCampaignDto(
                    blindBoxId = blindBoxCampaign.blindBoxId,
                    promotionalCampaignId = blindBoxCampaign.promotionalCampaignId,
                    createdAt = blindBoxCampaign.createdAt,
                    updatedAt = blindBoxCampaign.updatedAt,
                    isVisible = blindBoxCampaign.isVisible
                )
            },
            isVisible = domain.isVisible,
            toys = domain.toys.map { toyMapper.toRemote(it) },
            skus = domain.skus.map { stockKeepingUnitMapper.toRemote(it) },
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 