package com.vidz.data.flow

import com.vidz.domain.Init
import com.vidz.domain.Result
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ServerFlow<T, R>(
    private val getData: suspend () -> T,
    private val convert: (T) -> R,
) : IFlow<R> {

    override fun execute(): Flow<Result<R>> {
        return flow {
            emit(Init)
            try {
                val startTime = System.currentTimeMillis()
                val data = getData()
                val duration = System.currentTimeMillis() - startTime
                emit(Success(convert(data)))
            } catch (netWorkException: Exception) {
                emit(ServerError.General(""))
            }
        }
    }
}
