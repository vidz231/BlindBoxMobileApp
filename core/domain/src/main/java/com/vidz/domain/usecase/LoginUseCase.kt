package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.LoginResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Result<LoginResult>> {
        return authRepository.login(email, password)
    }
} 