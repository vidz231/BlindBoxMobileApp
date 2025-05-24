package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<Result<Account>>
    
    fun login(email: String, password: String): Flow<Result<LoginResult>>
    
    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<Result<Account>>
    
    fun loginWithGoogle(googleToken: String): Flow<Result<LoginResult>>
    
    fun refreshToken(refreshToken: String): Flow<Result<String>>
    
    fun logout(): Flow<Result<Unit>>
    
    fun forgotPassword(email: String): Flow<Result<Unit>>
    
    fun resetPassword(token: String, newPassword: String): Flow<Result<Unit>>
}

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val account: Account
)