package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(refreshToken: String): Flow<Result<String>> {
        return authRepository.refreshToken(refreshToken)
    }
} 