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
import com.vidz.domain.model.BlindBox
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
        loadBlindBoxes()
    }

    private fun loadBlindBoxes(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            val currentState = viewModelState.value
            
            if (!isLoadMore) {
                viewModelState.value = currentState.copy(isLoading = true)
            } else {
                viewModelState.value = currentState.copy(isLoadingMore = true)
            }

            getBlindBoxesUseCase.invoke(
                page = if (isLoadMore) currentState.currentPage + 1 else 0,
                size = currentState.pageSize,
                search = null,
                filter = null
            ).collect { result ->
                when (result) {
                    is Success<*> -> {
                        val newBlindBoxes = result.data as List<BlindBox>
                        val updatedBlindBoxes = if (isLoadMore) {
                            currentState.blindBoxes + newBlindBoxes
                        } else {
                            newBlindBoxes
                        }
                        
                        viewModelState.value = currentState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            blindBoxes = updatedBlindBoxes,
                            currentPage = if (isLoadMore) currentState.currentPage + 1 else 0,
                            hasMoreData = newBlindBoxes.size >= currentState.pageSize,
                            error = null
                        )
                        Log.d("HomeViewModel", "BlindBoxes loaded: ${updatedBlindBoxes.size}, page: ${if (isLoadMore) currentState.currentPage + 1 else 0}")
                    }
                    else -> {
                        viewModelState.value = currentState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = "Failed to load blind boxes"
                        )
                        Log.e("HomeViewModel", "Failed to load blind boxes")
                    }
                }
            }
        }
    }

    override fun onTriggerEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.LoadMore -> {
                if (viewModelState.value.hasMoreData && !viewModelState.value.isLoadingMore) {
                    loadBlindBoxes(isLoadMore = true)
                }
            }
            is HomeViewEvent.Refresh -> {
                loadBlindBoxes(isLoadMore = false)
            }
        }
    }

    data class HomeViewModelState(
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val error: String? = null,
        val blindBoxes: List<BlindBox> = emptyList(),
        val currentPage: Int = 0,
        val pageSize: Int = 10,
        val hasMoreData: Boolean = true
    ) : ViewModelState() {
        override fun toUiState(): ViewState = HomeViewState(
            isLoading = isLoading,
            isLoadingMore = isLoadingMore,
            error = error,
            blindBoxes = blindBoxes,
            hasMoreData = hasMoreData
        )
    }

    data class HomeViewState(
        val isLoading: Boolean,
        val isLoadingMore: Boolean,
        val error: String?,
        val blindBoxes: List<BlindBox>,
        val hasMoreData: Boolean
    ) : ViewState()

    sealed class HomeViewEvent : ViewEvent {
        data object LoadMore : HomeViewEvent()
        data object Refresh : HomeViewEvent()
    }
}
