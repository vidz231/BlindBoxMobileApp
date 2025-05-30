package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(token: String, newPassword: String): Flow<Result<Unit>> {
        return authRepository.resetPassword(token, newPassword)
    }
} 