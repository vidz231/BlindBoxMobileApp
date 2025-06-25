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
import com.vidz.domain.usecase.GetCartSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSkusUseCase: GetSkusUseCase,
    private val getBlindBoxesUseCase: GetBlindBoxesUseCase,
    private val getBLindBoxesByIdUseCase: GetBlindBoxesByIdUseCase,
    private val getCartSummaryUseCase: GetCartSummaryUseCase
) : BaseViewModel<HomeViewModel.HomeViewEvent,
        HomeViewModel.HomeViewState,
        HomeViewModel.HomeViewModelState>(
    initState = HomeViewModelState()
) {

    init {
        loadBlindBoxes()
        observeCartSummary()
    }

    private fun loadBlindBoxes(isLoadMore: Boolean = false, isRefresh: Boolean = false) {
        Log.d("HomeViewModel", "loadBlindBoxes called - isLoadMore: $isLoadMore, isRefresh: $isRefresh")
        viewModelScope.launch {
            // Always work with the latest state to avoid accidentally resetting fields (e.g. cartItemsCount)
            viewModelState.value = viewModelState.value.copy(
                isLoading = if (!isLoadMore && !isRefresh) true else viewModelState.value.isLoading,
                isLoadingMore = if (isLoadMore) true else viewModelState.value.isLoadingMore,
                isRefreshing = if (isRefresh) true else viewModelState.value.isRefreshing
            )
            
            Log.d("HomeViewModel", "State updated - isLoading: ${viewModelState.value.isLoading}, isRefreshing: ${viewModelState.value.isRefreshing}")

            val stateBeforeRequest = viewModelState.value

            getBlindBoxesUseCase.invoke(
                page = if (isLoadMore) stateBeforeRequest.currentPage + 1 else 0,
                size = stateBeforeRequest.pageSize,
                search = null,
                filter = null
            ).collect { result ->
                when (result) {
                    is Init -> {
                        // Loading state - do nothing, keep current loading state
                        Log.d("HomeViewModel", "Loading initialized")
                    }
                    is Success<*> -> {
                        val newBlindBoxes = result.data as List<BlindBox>
                        val updatedBlindBoxes = if (isLoadMore) {
                            stateBeforeRequest.blindBoxes + newBlindBoxes
                        } else {
                            newBlindBoxes
                        }
                        
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            blindBoxes = updatedBlindBoxes,
                            currentPage = if (isLoadMore) stateBeforeRequest.currentPage + 1 else 0,
                            hasMoreData = newBlindBoxes.size >= stateBeforeRequest.pageSize,
                            error = null,
                            isInitialLoading = updatedBlindBoxes.isEmpty()
                        )
                        Log.d("HomeViewModel", "BlindBoxes loaded: ${updatedBlindBoxes.size}, page: ${if (isLoadMore) stateBeforeRequest.currentPage + 1 else 0}")
                    }
                    is ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = "Failed to load blind boxes"
                        )
                        Log.e("HomeViewModel", "Failed to load blind boxes: ${result.message}")
                    }
                    else -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = "Failed to load blind boxes"
                        )
                        Log.e("HomeViewModel", "Failed to load blind boxes: Unknown error")
                    }
                }
            }
        }
    }

    private fun observeCartSummary() {
        viewModelScope.launch {
            getCartSummaryUseCase().collect { summary ->    
                viewModelState.value = viewModelState.value.copy(
                    cartItemsCount = summary.totalQuantity
                )
                Log.d("HomeViewModel", "Cart summary updated: ${summary.totalQuantity} items")
            }
        }
    }

    override fun onTriggerEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.LoadMore -> {
                Log.d("HomeViewModel", "LoadMore event triggered")
                if (viewModelState.value.hasMoreData && !viewModelState.value.isLoadingMore) {
                    loadBlindBoxes(isLoadMore = true)
                }
            }
            is HomeViewEvent.Refresh -> {
                Log.d("HomeViewModel", "Refresh event triggered")
                loadBlindBoxes(isLoadMore = false, isRefresh = true)
            }
        }
    }

    data class HomeViewModelState(
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val blindBoxes: List<BlindBox> = emptyList(),
        val currentPage: Int = 0,
        val pageSize: Int = 10,
        val hasMoreData: Boolean = true,
        val cartItemsCount: Int = 0,
        val isInitialLoading: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = HomeViewState(
            isLoading = isLoading,
            isLoadingMore = isLoadingMore,
            isRefreshing = isRefreshing,
            error = error,
            blindBoxes = blindBoxes,
            hasMoreData = hasMoreData,
            cartItemsCount = cartItemsCount
        )
    }

    data class HomeViewState(
        val isLoading: Boolean,
        val isLoadingMore: Boolean,
        val isRefreshing: Boolean,
        val error: String?,
        val blindBoxes: List<BlindBox>,
        val hasMoreData: Boolean,
        val cartItemsCount: Int
    ) : ViewState()

    sealed class HomeViewEvent : ViewEvent {
        data object LoadMore : HomeViewEvent()
        data object Refresh : HomeViewEvent()
    }
}
