package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.AutoCompleteResponseDto
import com.vidz.domain.model.map.AutoCompleteResponse as DomainAutoCompleteResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoCompleteResponseMapper @Inject constructor(
    private val autoCompleteMapper: AutoCompleteMapper
) : BaseRemoteMapper<DomainAutoCompleteResponse, AutoCompleteResponseDto> {

    override fun toDomain(external: AutoCompleteResponseDto): DomainAutoCompleteResponse {
        return DomainAutoCompleteResponse(
            predictions = external.predictions.map { autoCompleteMapper.toDomain(it) },
            executedTime = external.executedTime ?: 0,
            status = external.status
        )
    }

    override fun toRemote(domain: DomainAutoCompleteResponse): AutoCompleteResponseDto {
        return AutoCompleteResponseDto(
            predictions = domain.predictions.map { autoCompleteMapper.toRemote(it) },
            executedTime = domain.executedTime,
            status = domain.status
        )
    }
}