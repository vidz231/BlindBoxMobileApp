package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.ShippingInfoDto
import com.vidz.domain.model.ShippingInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingInfoMapper
    @Inject constructor()
    : BaseRemoteMapper<ShippingInfo, ShippingInfoDto> {

    override fun toDomain(external: ShippingInfoDto): ShippingInfo {
        return ShippingInfo(
            shippingInfoId = external.shippingInfoId,
            address = external.address,
            ward = external.ward,
            district = external.district,
            city = external.city,
            name = external.name,
            phoneNumber = external.phoneNumber,
            isVisible = external.isVisible,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: ShippingInfo): ShippingInfoDto {
        return ShippingInfoDto(
            shippingInfoId = domain.shippingInfoId,
            address = domain.address,
            ward = domain.ward,
            district = domain.district,
            city = domain.city,
            name = domain.name,
            phoneNumber = domain.phoneNumber,
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
} 