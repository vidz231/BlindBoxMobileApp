package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.AutoCompleteDto
import com.vidz.domain.model.map.AutoComplete as DomainAutoComplete
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoCompleteMapper @Inject constructor() :
    BaseRemoteMapper<DomainAutoComplete, AutoCompleteDto> {

    override fun toDomain(external: AutoCompleteDto): DomainAutoComplete {
        return DomainAutoComplete(
            description = external.description ?: "",
            placeId = external.placeId ?: "",
            mainText = external.structuredFormatting?.mainText ?: "",
            secondaryText = external.structuredFormatting?.secondaryText ?: ""
        )
    }

    override fun toRemote(domain: DomainAutoComplete): AutoCompleteDto {
        return AutoCompleteDto(
            description = domain.description,
            placeId = domain.placeId,
            structuredFormatting = AutoCompleteDto.StructuredFormatting(
                mainText = domain.mainText,
                secondaryText = domain.secondaryText
            )
        )
    }
} 