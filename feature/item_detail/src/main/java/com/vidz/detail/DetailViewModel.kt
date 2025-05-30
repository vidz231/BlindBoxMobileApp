package com.vidz.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.Success
import com.vidz.domain.model.BlindBox
import com.vidz.domain.usecase.GetBlindBoxesByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getBlindBoxesByIdUseCase: GetBlindBoxesByIdUseCase
) : BaseViewModel<DetailViewModel.DetailViewEvent,
        DetailViewModel.DetailViewState,
        DetailViewModel.DetailViewModelState>(
    initState = DetailViewModelState()
) {

    fun loadBlindBoxDetails(blindBoxId: Long) {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(isLoading = true)
            getBlindBoxesByIdUseCase.invoke(blindBoxId)
                .collect { result ->
                    when (result) {
                        is Success<*> -> {
                            val blindBox = result.data as BlindBox
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                blindBox = blindBox,
                                error = null
                            )
                            Log.d("DetailViewModel", "BlindBox loaded: ${blindBox.name}")
                        }
                        is Init -> {

                        }
                        else -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                error = "Failed to load blind box details"
                            )
                            Log.e("DetailViewModel", "Failed to load blind box")
                        }
                    }
                }
        }
    }

    override fun onTriggerEvent(event: DetailViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class DetailViewModelState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val blindBox: BlindBox? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = DetailViewState(
            isLoading = isLoading,
            error = error,
            blindBox = blindBox
        )
    }

    data class DetailViewState(
        val isLoading: Boolean,
        val error: String? =null,
        val blindBox: BlindBox?
    ) : ViewState()

    sealed class DetailViewEvent : ViewEvent {
        // Define events here
    }
} 