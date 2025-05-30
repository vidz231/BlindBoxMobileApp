package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Result<Account>> {
        return authRepository.getCurrentUser()
    }
} 