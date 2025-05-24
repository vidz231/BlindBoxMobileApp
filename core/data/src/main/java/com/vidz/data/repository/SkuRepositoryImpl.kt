package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.StockKeepingUnitMapper
import com.vidz.data.server.retrofit.api.SkuApi
import com.vidz.data.server.retrofit.api.PagedResponse
import com.vidz.data.server.retrofit.dto.StockKeepingUnitDto
import com.vidz.domain.Result
import com.vidz.domain.model.StockKeepingUnit
import com.vidz.domain.repository.SkuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SkuRepositoryImpl @Inject constructor(
    private val skuApi: SkuApi,
    private val stockKeepingUnitMapper: StockKeepingUnitMapper
) : SkuRepository {

    override fun getSkus(
        page: Int,
        size: Int,
        search: String?,
        filter: String?
    ): Flow<Result<List<StockKeepingUnit>>> {
        return ServerFlow(
            getData = {
                skuApi.getSkus(page, size, search, filter).body()!!
            },
            convert = { response: PagedResponse<StockKeepingUnitDto> ->
                response.content.map { stockKeepingUnitMapper.toDomain(it) }
            }
        ).execute()
    }

    override fun getSkuById(skuId: Long): Flow<Result<StockKeepingUnit>> {
        return ServerFlow(
            getData = {
                skuApi.getSkuById(skuId).body()!!
            },
            convert = { skuDto ->
                stockKeepingUnitMapper.toDomain(skuDto)
            }
        ).execute()
    }
} 