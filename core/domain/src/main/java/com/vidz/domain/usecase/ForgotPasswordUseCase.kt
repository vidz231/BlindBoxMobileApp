package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String): Flow<Result<Unit>> {
        return authRepository.forgotPassword(email)
    }
} 