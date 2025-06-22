package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(): Flow<Result<String>> {
        return tokenRepository.getValidAccessToken();
    }
} 