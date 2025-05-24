package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.AccountMapper
import com.vidz.data.server.retrofit.api.AuthApi
import com.vidz.data.server.retrofit.api.LoginRequest
import com.vidz.data.server.retrofit.api.RegisterRequest
import com.vidz.data.server.retrofit.api.GoogleLoginRequest
import com.vidz.data.server.retrofit.api.RefreshTokenRequest
import com.vidz.data.server.retrofit.api.ForgotPasswordRequest
import com.vidz.data.server.retrofit.api.ResetPasswordRequest
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.LoginResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val accountMapper: AccountMapper
) : AuthRepository {

    override fun getCurrentUser(): Flow<Result<Account>> {
        return ServerFlow(
            getData = {
                authApi.getCurrentUser().body()!!
            },
            convert = { accountDto ->
                accountMapper.toDomain(accountDto)
            }
        ).execute()
    }

    override fun login(email: String, password: String): Flow<Result<LoginResult>> {
        return ServerFlow(
            getData = {
                authApi.login(LoginRequest(email, password)).body()!!
            },
            convert = { loginResponse ->
                LoginResult(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    account = accountMapper.toDomain(loginResponse.account)
                )
            }
        ).execute()
    }

    override fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<Result<Account>> {
        return ServerFlow(
            getData = {
                authApi.register(
                    RegisterRequest(email, password, firstName, lastName)
                ).body()!!
            },
            convert = { accountDto ->
                accountMapper.toDomain(accountDto)
            }
        ).execute()
    }

    override fun loginWithGoogle(googleToken: String): Flow<Result<LoginResult>> {
        return ServerFlow(
            getData = {
                authApi.loginWithGoogle(GoogleLoginRequest(googleToken)).body()!!
            },
            convert = { loginResponse ->
                LoginResult(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    account = accountMapper.toDomain(loginResponse.account)
                )
            }
        ).execute()
    }

    override fun refreshToken(refreshToken: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                authApi.refreshToken(RefreshTokenRequest(refreshToken)).body()!!
            },
            convert = { refreshTokenDto ->
                refreshTokenDto.token
            }
        ).execute()
    }

    override fun logout(): Flow<Result<Unit>> {
        return ServerFlow(
            getData = {
                authApi.logout().body()!!
            },
            convert = { _ ->
                Unit
            }
        ).execute()
    }

    override fun forgotPassword(email: String): Flow<Result<Unit>> {
        return ServerFlow(
            getData = {
                authApi.forgotPassword(ForgotPasswordRequest(email)).body()!!
            },
            convert = { _ ->
                Unit
            }
        ).execute()
    }

    override fun resetPassword(token: String, newPassword: String): Flow<Result<Unit>> {
        return ServerFlow(
            getData = {
                authApi.resetPassword(ResetPasswordRequest(token, newPassword)).body()!!
            },
            convert = { _ ->
                Unit
            }
        ).execute()
    }
} 