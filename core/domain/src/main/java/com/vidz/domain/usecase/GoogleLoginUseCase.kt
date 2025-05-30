package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.Success
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.LoginResult
import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(googleToken: String): Flow<Result<LoginResult>> = flow {
        authRepository.loginWithGoogle(googleToken).collect { result ->
            when (result) {
                is Success -> {
                    // Save tokens to secure storage
                    tokenRepository.saveTokens(
                        accessToken = result.data.accessToken,
                        refreshToken = result.data.refreshToken
                    )
                    emit(result)
                }
                else -> emit(result)
            }
        }
    }
} 