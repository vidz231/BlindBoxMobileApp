package com.vidz.data.flow

import com.vidz.domain.Init
import com.vidz.domain.Result
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

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
            } catch (netWorkException: HttpException) {
                if (netWorkException.code() == 401) {
                    emit(ServerError.Token("Token expired"))
                } else if (netWorkException.code() == 400) {
                    emit(ServerError.MissingParam("Missing parameter"))
                } else if (netWorkException.code() == 403) {
                    emit(ServerError.RequiredLogin("Login required"))
                } else if (netWorkException.code() == 404) {
                    emit(ServerError.RequiredVip("VIP required"))
                } else if (netWorkException.code() == 402) {
                    emit(ServerError.NotEnoughCredit("Not enough credit"))
                } else {
                    emit(ServerError.General(netWorkException.message()))
                }
            }
        }
    }
}
