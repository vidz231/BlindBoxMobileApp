package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.BrandDto
import com.vidz.domain.model.Brand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrandMapper @Inject constructor() : BaseRemoteMapper<Brand, BrandDto> {

    override fun toDomain(external: BrandDto): Brand {
        return Brand(
            brandId = external.brandId,
            name = external.name,
            description = external.description,
            isVisible = external.isVisible,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: Brand): BrandDto {
        return BrandDto(
            brandId = domain.brandId,
            name = domain.name,
            description = domain.description,
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 