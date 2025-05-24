package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.BlindBoxMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.data.server.retrofit.api.PagedResponse
import com.vidz.data.server.retrofit.dto.BlindBoxDto
import com.vidz.domain.Result
import com.vidz.domain.model.BlindBox
import com.vidz.domain.repository.BlindBoxRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlindBoxRepositoryImpl @Inject constructor(
    val retrofitServer: RetrofitServer,
    private val blindBoxMapper: BlindBoxMapper
) : BlindBoxRepository {
    override fun getBlindBoxById(blindBoxId: Long): Flow<Result<BlindBox>> {
        return ServerFlow(
            getData = {
                retrofitServer.blindBoxApi.getBlindBoxById(blindBoxId)
                        .body() ?: BlindBoxDto()
            },
            convert = { response ->
                blindBoxMapper.toDomain(response)
            }
        ).execute()

    };

    override fun getBlindBoxes(
        page: Int,
        size: Int,
        search: String?,
        filter: String?
    ): Flow<Result<List<BlindBox>>> {
        return ServerFlow(
            getData = {
                retrofitServer.blindBoxApi.getBlindBoxes(page, size, search, filter)
                        .body() ?: PagedResponse()
            },
            convert = { response ->
                response.content.map { blindBoxMapper.toDomain(it) }
            }
        ).execute()
    }

}