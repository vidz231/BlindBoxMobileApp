package com.vidz.home.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.usecase.GetBlindBoxesByIdUseCase
import com.vidz.domain.usecase.GetBlindBoxesUseCase
import com.vidz.domain.usecase.GetSkusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSkusUseCase: GetSkusUseCase,
    private val getBlindBoxesUseCase: GetBlindBoxesUseCase,
    private val getBLindBoxesByIdUseCase: GetBlindBoxesByIdUseCase
) : BaseViewModel<HomeViewModel.HomeViewEvent,
        HomeViewModel.HomeViewState,
        HomeViewModel.HomeViewModelState>(
    initState = HomeViewModelState()
) {

    init {
        viewModelScope.launch {
            getBlindBoxesUseCase.invoke(
                page = 0,
                size = 10,
                search = null,
                filter = null
            )
                    .collect {
                        when(it){
                            is Success<*> -> {
                                Log.d("HomeViewModel", "Result: ${it.data}")
                            }
                            else -> {}
                        }

                    }
        }

    }

    override fun onTriggerEvent(event: HomeViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class HomeViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
        // Add other state properties here
    ) : ViewModelState() {
        override fun toUiState(): ViewState = HomeViewState(
            isLoading = isLoading,
            error = error
            // Map other properties here
        )
    }

    data class HomeViewState(
        val isLoading: Boolean,
        val error: String?
        // Add other UI state properties here
    ) : ViewState()

    sealed class HomeViewEvent : ViewEvent {
        // Define events here
    }
}
