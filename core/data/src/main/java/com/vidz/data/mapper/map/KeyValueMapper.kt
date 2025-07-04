package com.vidz.data.mapper.map

import com.vidz.data.mapper.BaseRemoteMapper
import com.vidz.data.server.retrofit.dto.map.KeyValue
import com.vidz.domain.model.map.KeyValue as DomainKeyValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueMapper @Inject constructor() : BaseRemoteMapper<DomainKeyValue, KeyValue> {

    override fun toDomain(external: KeyValue): DomainKeyValue {
        return DomainKeyValue(
            text = external.text ?: "",
            value = external.value ?: 0.0
        )
    }

    override fun toRemote(domain: DomainKeyValue): KeyValue {
        return KeyValue(
            text = domain.text,
            value = domain.value
        )
    }
} 