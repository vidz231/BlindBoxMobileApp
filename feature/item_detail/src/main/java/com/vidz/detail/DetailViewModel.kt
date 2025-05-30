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
import com.vidz.domain.model.StockKeepingUnit
import com.vidz.domain.model.Slot
import com.vidz.domain.usecase.GetBlindBoxesByIdUseCase
import com.vidz.domain.usecase.AddToCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getBlindBoxesByIdUseCase: GetBlindBoxesByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase
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
                            // Do nothing during init state
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

    fun addToCart(sku: StockKeepingUnit, quantity: Int = 1, slot: Slot? = null) {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(
                isAddingToCart = true,
                addToCartError = null
            )
            
            val result = addToCartUseCase.invoke(sku, quantity, slot)
            
            when (result) {
                is Success -> {
                    viewModelState.value = viewModelState.value.copy(
                        isAddingToCart = false,
                        addToCartSuccess = true,
                        addToCartError = null
                    )
                    Log.d("DetailViewModel", "Successfully added ${sku.name} to cart")
                }
                else -> {
                    val errorMessage = if (result is com.vidz.domain.ServerError) {
                        result.message
                    } else {
                        "Failed to add item to cart"
                    }
                    
                    viewModelState.value = viewModelState.value.copy(
                        isAddingToCart = false,
                        addToCartSuccess = false,
                        addToCartError = errorMessage
                    )
                    Log.e("DetailViewModel", "Failed to add to cart: $errorMessage")
                }
            }
        }
    }

    fun clearAddToCartState() {
        viewModelState.value = viewModelState.value.copy(
            addToCartSuccess = false,
            addToCartError = null
        )
    }

    override fun onTriggerEvent(event: DetailViewEvent) {
        when (event) {
            is DetailViewEvent.AddToCart -> {
                addToCart(event.sku, event.quantity, event.slot)
            }
            is DetailViewEvent.ClearAddToCartState -> {
                clearAddToCartState()
            }
        }
    }

    data class DetailViewModelState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val blindBox: BlindBox? = null,
        val isAddingToCart: Boolean = false,
        val addToCartSuccess: Boolean = false,
        val addToCartError: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = DetailViewState(
            isLoading = isLoading,
            error = error,
            blindBox = blindBox,
            isAddingToCart = isAddingToCart,
            addToCartSuccess = addToCartSuccess,
            addToCartError = addToCartError
        )
    }

    data class DetailViewState(
        val isLoading: Boolean,
        val error: String? = null,
        val blindBox: BlindBox?,
        val isAddingToCart: Boolean,
        val addToCartSuccess: Boolean,
        val addToCartError: String?
    ) : ViewState()

    sealed class DetailViewEvent : ViewEvent {
        data class AddToCart(
            val sku: StockKeepingUnit,
            val quantity: Int = 1,
            val slot: Slot? = null
        ) : DetailViewEvent()
        
        data object ClearAddToCartState : DetailViewEvent()
    }
} 