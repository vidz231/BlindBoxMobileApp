package com.vidz.auth.login

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.auth.login.LoginViewModel.LoginEvent
import com.vidz.auth.login.LoginViewModel.LoginUiState
import com.vidz.auth.login.LoginViewModel.LoginViewModelState
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginEvent, LoginUiState, LoginViewModelState>(
    initState = LoginViewModelState()
) {

    override fun onTriggerEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> updateEmail(event.email)
            is LoginEvent.UpdatePassword -> updatePassword(event.password)
            is LoginEvent.Login -> login()
            is LoginEvent.ClearError -> clearError()
        }
    }

    private fun updateEmail(email: String) {
        viewModelState.value = viewModelState.value.copy(
            email = email,
            emailError = null
        )
    }

    private fun updatePassword(password: String) {
        viewModelState.value = viewModelState.value.copy(
            password = password,
            passwordError = null
        )
    }

    private fun login() {
        val currentState = viewModelState.value
        
        // Validate input
        if (!validateInput(currentState.email, currentState.password)) {
            return
        }

        viewModelState.value = currentState.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            loginUseCase(currentState.email, currentState.password).collect { result ->
                when (result) {
                    is Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            loginResult = result.data
                        )
                    }
                    is ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true
        val currentState = viewModelState.value

        if (email.isBlank()) {
            viewModelState.value = currentState.copy(
                emailError = "Email is required"
            )
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            viewModelState.value = currentState.copy(
                emailError = "Please enter a valid email"
            )
            isValid = false
        }

        if (password.isBlank()) {
            viewModelState.value = currentState.copy(
                passwordError = "Password is required"
            )
            isValid = false
        } else if (password.length < 1) {
            viewModelState.value = currentState.copy(
                passwordError = "Password must be at least 1 characters"
            )
            isValid = false
        }

        return isValid
    }

    private fun clearError() {
        viewModelState.value = viewModelState.value.copy(error = null)
    }

// Events
sealed class LoginEvent : ViewEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    object Login : LoginEvent()
    object ClearError : LoginEvent()
}

// View Model State
data class LoginViewModelState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loginResult: com.vidz.domain.repository.LoginResult? = null
) : ViewModelState() {
    override fun toUiState(): ViewState = LoginUiState(
        email = email,
        password = password,
        emailError = emailError,
        passwordError = passwordError,
        isLoading = isLoading,
        error = error,
        isLoginSuccessful = isLoginSuccessful,
        isFormValid = email.isNotBlank() && password.isNotBlank()
    )
}

// UI State
data class LoginUiState(
    val email: String,
    val password: String,
    val emailError: String?,
    val passwordError: String?,
    val isLoading: Boolean,
    val error: String?,
    val isLoginSuccessful: Boolean,
    val isFormValid: Boolean
) : ViewState()
}
