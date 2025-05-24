package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.ImageDto
import com.vidz.domain.model.Image
import com.vidz.domain.model.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageMapper @Inject constructor(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Image, ImageDto> {

    override fun toDomain(external: ImageDto): Image {
        return Image(
            imageId = external.imageId,
            uploader = external.uploader?.let { accountMapper.toDomain(it) } ?: Account(),
            blindBoxId = external.blindBoxId ?: 0L,
            toyId = external.toyId ?: 0L,
            skuId = external.skuId ?: 0L,
            imageUrl = external.imageUrl,
            isVisible = external.isVisible,
            createdAt = external.createdAt
        )
    }

    override fun toRemote(domain: Image): ImageDto {
        return ImageDto(
            imageId = domain.imageId,
            uploader = accountMapper.toRemote(domain.uploader),
            blindBoxId = domain.blindBoxId.takeIf { it != 0L },
            toyId = domain.toyId.takeIf { it != 0L },
            skuId = domain.skuId.takeIf { it != 0L },
            imageUrl = domain.imageUrl,
            isVisible = domain.isVisible,
            createdAt = domain.createdAt
        )
    }
} 