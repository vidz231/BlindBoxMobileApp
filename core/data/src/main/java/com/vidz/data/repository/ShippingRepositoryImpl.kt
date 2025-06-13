package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.ShippingInfoMapper
import com.vidz.data.server.retrofit.api.ShippingInfoApi
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.repository.ShippingRepository
import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingRepositoryImpl @Inject constructor(
    private val shippingInfoApi: ShippingInfoApi,
    private val shippingInfoMapper: ShippingInfoMapper
) : ShippingRepository {

    override suspend fun createShippingInfo(shippingInfo: ShippingInfo): Flow<Result<ShippingInfo>> {
        return ServerFlow(
            getData = {
                shippingInfoApi.createShippingInfo(shippingInfoMapper.toRemote(shippingInfo)).body()!!
            },
            convert = { shippingInfoDto ->
                shippingInfoMapper.toDomain(shippingInfoDto)
            }
        ).execute()
    }

    override suspend fun getShippingInfoById(shippingInfoId: Long): Flow<Result<ShippingInfo>> {
        return ServerFlow(
            getData = {
                shippingInfoApi.getShippingInfoById(shippingInfoId).body()!!
            },
            convert = { shippingInfoDto ->
                shippingInfoMapper.toDomain(shippingInfoDto)
            }
        ).execute()
    }

    override suspend fun getShippingInfos(
        page: Int,
        size: Int,
        accountId: Long?
    ): Flow<Result<List<ShippingInfo>>> {
        return ServerFlow(
            getData = {
                shippingInfoApi.getShippingInfos(page = page, size = size).body()!!
            },
            convert = { response ->
                response.content.map { shippingInfoMapper.toDomain(it) }
            }
        ).execute()
    }

    override suspend fun updateShippingInfo(
        shippingInfoId: Long,
        shippingInfo: ShippingInfo
    ): Flow<Result<ShippingInfo>> {
        return ServerFlow(
            getData = {
                shippingInfoApi.updateShippingInfo(shippingInfoId, shippingInfoMapper.toRemote(shippingInfo)).body()!!
            },
            convert = { shippingInfoDto ->
                shippingInfoMapper.toDomain(shippingInfoDto)
            }
        ).execute()
    }

    override suspend fun deleteShippingInfo(shippingInfoId: Long): Flow<Result<Unit>> {
        return ServerFlow(
            getData = {
                shippingInfoApi.deleteShippingInfo(shippingInfoId).body()!!
            },
            convert = { _ ->
                Unit
            }
        ).execute()
    }
} 