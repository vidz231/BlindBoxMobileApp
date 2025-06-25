package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.PromotionalCampaignDto
import com.vidz.domain.model.PromotionalCampaign
import com.vidz.domain.model.BlindBoxCampaign
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionalCampaignMapper @Inject constructor() : BaseRemoteMapper<PromotionalCampaign, PromotionalCampaignDto> {

    override fun toDomain(external: PromotionalCampaignDto): PromotionalCampaign {
        return PromotionalCampaign(
            campaignId = external.campaignId,
            title = external.title,
            description = external.description,
            startDate = external.startDate,
            endDate = external.endDate,
            discountRate = external.discountRate,
            isVisible = external.isVisible,
            blindBoxCampaigns = emptyList(), // Will be mapped separately to avoid circular dependencies
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: PromotionalCampaign): PromotionalCampaignDto {
        return PromotionalCampaignDto(
            campaignId = domain.campaignId,
            title = domain.title,
            description = domain.description,
            startDate = domain.startDate,
            endDate = domain.endDate,
            discountRate = domain.discountRate,
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 
