package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.VideoDto
import com.vidz.domain.model.Video
import com.vidz.domain.model.Account

class VideoMapper(
    private val accountMapper: AccountMapper
) : BaseRemoteMapper<Video, VideoDto> {

    override fun toDomain(external: VideoDto): Video {
        return Video(
            videoId = external.videoId,
            account = external.account?.let { accountMapper.toDomain(it) } ?: Account(),
            slotId = external.slotId,
            url = external.url,
            description = external.description,
            isVisible = external.isVisible,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt,
            isVerified = external.isVerified
        )
    }

    override fun toRemote(domain: Video): VideoDto {
        return VideoDto(
            videoId = domain.videoId,
            account = accountMapper.toRemote(domain.account),
            slotId = domain.slotId,
            url = domain.url,
            description = domain.description,
            isVisible = domain.isVisible,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isVerified = domain.isVerified
        )
    }
} 