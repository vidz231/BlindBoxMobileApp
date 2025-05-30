package com.vidz.domain.usecase

import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsUserLoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return tokenRepository.isAuthenticated()
    }
} 