package com.vidz.data.flow

import com.vidz.domain.Init
import com.vidz.domain.Result
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException

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
            } catch (connectException: ConnectException) {
                // Handle network connectivity issues
                emit(ServerError.Internet("No internet connection. Please check your network and try again."))
            } catch (nullPointerException: NullPointerException) {
                // Handle cases where response.body() returns null (usually due to error responses)
                emit(ServerError.General("Request failed: Invalid credentials or server error"))
            } catch (netWorkException: HttpException) {
                val serverErrorMessage = extractServerErrorMessage(netWorkException)
                
                when (netWorkException.code()) {
                    401 -> emit(ServerError.Token(serverErrorMessage ?: "Token expired"))
                    400 -> emit(ServerError.MissingParam(serverErrorMessage ?: "Missing parameter"))
                    403 -> emit(ServerError.RequiredLogin(serverErrorMessage ?: "Login required"))
                    404 -> emit(ServerError.RequiredVip(serverErrorMessage ?: "VIP required"))
                    402 -> emit(ServerError.NotEnoughCredit(serverErrorMessage ?: "Not enough credit"))
                    else -> emit(ServerError.General(serverErrorMessage ?: netWorkException.message() ?: "Unknown server error"))
                }
            } catch (exception: Exception) {
                // Handle any other unexpected exceptions
                emit(ServerError.General("Unexpected error: ${exception.message}"))
            }
        }
    }
    
    private fun extractServerErrorMessage(httpException: HttpException): String? {
        return try {
            val errorBody = httpException.response()?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val jsonObject = JSONObject(errorBody)
                jsonObject.optString("error").takeIf { it.isNotEmpty() }
            } else {
                null
            }
        } catch (e: Exception) {
            // If parsing fails, return null to fall back to default messages
            null
        }
    }
}
