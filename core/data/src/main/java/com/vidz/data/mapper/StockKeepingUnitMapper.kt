package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.StockKeepingUnitDto
import com.vidz.domain.model.StockKeepingUnit
import com.vidz.domain.model.Image
import com.vidz.domain.model.BlindBox
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockKeepingUnitMapper @Inject constructor(
    private val imageMapper: ImageMapper
) : BaseRemoteMapper<StockKeepingUnit, StockKeepingUnitDto> {

    override fun toDomain(external: StockKeepingUnitDto): StockKeepingUnit {
        return StockKeepingUnit(
            skuId = external.skuId,
            name = external.name,
            image = external.image.let { imageMapper.toDomain(it) },
            price = external.price,
            stock = external.stock,
            specCount = external.specCount,
            blindBox = BlindBox(), // Will be set separately to avoid circular dependencies
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            isVisible = external.isVisible
        )
    }

    override fun toRemote(domain: StockKeepingUnit): StockKeepingUnitDto {
        return StockKeepingUnitDto(
            skuId = domain.skuId,
            name = domain.name,
            image = imageMapper.toRemote(domain.image),
            price = domain.price,
            stock = domain.stock,
            specCount = domain.specCount,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isVisible = domain.isVisible
        )
    }
} 