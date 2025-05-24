package com.vidz.auth

import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<AuthViewModel.AuthViewEvent,
        AuthViewModel.AuthViewState,
        AuthViewModel.AuthViewModelState>(
    initState = AuthViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: AuthViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class AuthViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
        // Add other state properties here
    ) : ViewModelState() {
        override fun toUiState(): ViewState = AuthViewState(
            isLoading = isLoading,
            error = error
            // Map other properties here
        )
    }

    data class AuthViewState(
        val isLoading: Boolean,
        val error: String?
        // Add other UI state properties here
    ) : ViewState()

    sealed class AuthViewEvent : ViewEvent {
        // Define events here
    }
}
