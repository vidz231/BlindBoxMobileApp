package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.Success
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Result<Unit>> = flow {
        authRepository.logout().collect { result ->
            when (result) {
                is Success -> {
                    // Clear tokens from secure storage
                    tokenRepository.clearTokens()
                    emit(result)
                }
                else -> {
                    // Even if logout fails on server, clear local tokens
                    tokenRepository.clearTokens()
                    emit(result)
                }
            }
        }
    }
} 