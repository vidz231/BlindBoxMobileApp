package com.vidz.search.search

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
import com.vidz.domain.usecase.GetBlindBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    // TODO: Inject search use case here
    private val getBlindBoxesUseCase: GetBlindBoxesUseCase,

    ) : BaseViewModel<SearchEvent, SearchUiState, SearchViewModelState>(
    SearchViewModelState()
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
                    when (it) {
                        Init -> {
                            Log.d("SearchViewModel", "Initialization started")
                            viewModelState.update { state ->
                                state.copy(isLoading = true, error = null)
                            }
                        }

                        is ServerError.General -> {
                            Log.e("SearchViewModel", "General server error: ${it.message}")

                        }

                        is ServerError.MissingParam -> {
                            Log.e("SearchViewModel", "Missing parameter error: ${it.message}")
                        }

                        is ServerError.NotEnoughCredit ->
                            Log.e("SearchViewModel", "Not enough credit error: ${it.message}")

                        is ServerError.RequiredLogin ->
                            Log.e("SearchViewModel", "Required login error: ${it.message}")

                        is ServerError.RequiredVip ->
                            Log.e("SearchViewModel", "Required VIP error: ${it.message}")

                        is ServerError.Token ->
                            Log.e("SearchViewModel", "Token error: ${it.message}")

                        is ServerError.Internet
                            -> Log.e("SearchViewModel", "Internet error: ${it.message}")

                        is Success<List<BlindBox>> -> {
                            Log.d("SearchViewModel", "Successfully fetched blind boxes: ${it.data}")

                            viewModelState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    searchResults = it.data,
                                    error = null,
                                    hasSearched = true
                                )
                            }
                        }
                    }

                }
        }

    }

    override fun onTriggerEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChanged -> {
                updateSearchQuery(event.query)
            }

            SearchEvent.OnSearchClicked -> {
                performSearch()
            }

            SearchEvent.OnClearSearch -> {
                clearSearch()
            }

            is SearchEvent.OnFilterChanged -> {
                updateFilter(event.filter)
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        viewModelState.value = viewModelState.value.copy(
            searchQuery = query
        )
    }

    private fun performSearch() {
        viewModelScope.launch {
            val currentState = viewModelState.value

            viewModelState.value = currentState.copy(isLoading = true)

            try {
                getBlindBoxesUseCase.invoke(
                    page = 0,
                    size = 10,
                    search = currentState.searchQuery,
                    filter = null
                ).collect {
                    when (it) {
                        Init -> {
                            Log.d("SearchViewModel", "Initialization started")
                            viewModelState.update { state ->
                                state.copy(isLoading = true, error = null)
                            }
                        }

                        is ServerError.General -> {
                            Log.e("SearchViewModel", "General server error: ${it.message}")

                        }

                        is ServerError.MissingParam -> {
                            Log.e("SearchViewModel", "Missing parameter error: ${it.message}")
                        }

                        is ServerError.NotEnoughCredit ->
                            Log.e("SearchViewModel", "Not enough credit error: ${it.message}")

                        is ServerError.RequiredLogin ->
                            Log.e("SearchViewModel", "Required login error: ${it.message}")

                        is ServerError.RequiredVip ->
                            Log.e("SearchViewModel", "Required VIP error: ${it.message}")

                        is ServerError.Token ->
                            Log.e("SearchViewModel", "Token error: ${it.message}")

                        is ServerError.Internet
                            -> Log.e("SearchViewModel", "Internet error: ${it.message}")

                        is Success<List<BlindBox>> -> {
                            Log.d("SearchViewModel", "Successfully fetched blind boxes: ${it.data}")

                            viewModelState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    searchResults = it.data,
                                    error = null,
                                    hasSearched = true,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.value = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Search failed"
                )
            }
        }
    }

    private fun clearSearch() {
        val currentState = viewModelState.value
        viewModelState.value = currentState.copy(
            searchQuery = ""
        )

        performSearch();

    }

    private fun updateFilter(filter: SearchFilter) {
        val currentState = viewModelState.value
        viewModelState.value = currentState.copy(
            selectedFilter = filter,
            searchResults = applyFilter(currentState.searchResults, filter)
        )
    }

    private fun applyFilter(results: List<BlindBox>, filter: SearchFilter): List<BlindBox> {
        return when (filter) {
            SearchFilter.ALL -> results
            SearchFilter.POPULAR -> results.sortedByDescending { it.blindBoxId } // Mock popularity
            SearchFilter.NEWEST -> results.sortedByDescending { it.createdAt }
            SearchFilter.PRICE_LOW_TO_HIGH -> results.sortedBy {
                it.skus.minOfOrNull { sku -> sku.price } ?: 0.0
            }

            SearchFilter.PRICE_HIGH_TO_LOW -> results.sortedByDescending {
                it.skus.maxOfOrNull { sku -> sku.price } ?: 0.0
            }
        }
    }
}

data class SearchViewModelState(
    val searchQuery: String = "",
    val searchResults: List<BlindBox> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val selectedFilter: SearchFilter = SearchFilter.ALL
) : ViewModelState() {
    override fun toUiState(): ViewState = SearchUiState(
        searchQuery = searchQuery,
        searchResults = searchResults,
        isLoading = isLoading,
        error = error,
        hasSearched = hasSearched,
        selectedFilter = selectedFilter
    )
}

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<BlindBox> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val selectedFilter: SearchFilter = SearchFilter.ALL
) : ViewState()

sealed class SearchEvent : ViewEvent {
    data class OnSearchQueryChanged(val query: String) : SearchEvent()
    data object OnSearchClicked : SearchEvent()
    data object OnClearSearch : SearchEvent()
    data class OnFilterChanged(val filter: SearchFilter) : SearchEvent()
}

enum class SearchFilter(val displayName: String) {
    ALL("All"),
    POPULAR("Popular"),
    NEWEST("Newest"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low")
} 