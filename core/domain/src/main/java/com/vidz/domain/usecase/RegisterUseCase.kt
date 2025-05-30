package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String
    ): Flow<Result<Account>> {
        return authRepository.register(email, password, confirmPassword, firstName, lastName)
    }
}