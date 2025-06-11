package com.vidz.cart

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.model.CartItem
import com.vidz.domain.usecase.ClearCartUseCase
import com.vidz.domain.usecase.GetCartSummaryUseCase
import com.vidz.domain.usecase.ObserveCartItemsUseCase
import com.vidz.domain.usecase.RemoveFromCartUseCase
import com.vidz.domain.usecase.UpdateCartItemQuantityUseCase
import com.vidz.domain.usecase.CartSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getCartSummaryUseCase: GetCartSummaryUseCase
) : BaseViewModel<CartViewModel.CartViewEvent,
        CartViewModel.CartViewState,
        CartViewModel.CartViewModelState>(
    initState = CartViewModelState()
) {

    init {
        observeCartData()
    }

    private fun observeCartData() {
        viewModelScope.launch {
            combine(
                observeCartItemsUseCase(),
                getCartSummaryUseCase()
            ) { cartItems, cartSummary ->
                Pair(cartItems, cartSummary)
            }.collect { (cartItems, cartSummary) ->
                viewModelState.value = viewModelState.value.copy(
                    cartItems = cartItems,
                    cartSummary = cartSummary,
                    isLoading = false
                )
            }
        }
    }

    private fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            val result = removeFromCartUseCase.invoke(cartItemId)
            when (result) {
                is Success -> {
                    // Success is handled by observeCartData
                }
                else -> {
                    viewModelState.value = viewModelState.value.copy(
                        error = "Failed to remove item from cart"
                    )
                }
            }
        }
    }

    private fun updateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val result = updateCartItemQuantityUseCase.invoke(cartItemId, newQuantity)
            when (result) {
                is Success -> {
                    // Success is handled by observeCartData
                }
                else -> {
                    viewModelState.value = viewModelState.value.copy(
                        error = "Failed to update item quantity"
                    )
                }
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(
                isClearingCart = true
            )
            
            val result = clearCartUseCase.invoke()
            when (result) {
                is Success -> {
                    viewModelState.value = viewModelState.value.copy(
                        isClearingCart = false
                    )
                }
                else -> {
                    viewModelState.value = viewModelState.value.copy(
                        isClearingCart = false,
                        error = "Failed to clear cart"
                    )
                }
            }
        }
    }

    private fun dismissError() {
        viewModelState.value = viewModelState.value.copy(
            error = null
        )
    }

    override fun onTriggerEvent(event: CartViewEvent) {
        when (event) {
            is CartViewEvent.RemoveItem -> removeItem(event.cartItemId)
            is CartViewEvent.UpdateQuantity -> updateQuantity(event.cartItemId, event.newQuantity)
            is CartViewEvent.ClearCart -> clearCart()
            is CartViewEvent.DismissError -> dismissError()
        }
    }

    data class CartViewModelState(
        val isLoading: Boolean = true,
        val cartItems: List<CartItem> = emptyList(),
        val cartSummary: CartSummary = CartSummary(0, 0, 0.0),
        val isClearingCart: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = CartViewState(
            isLoading = isLoading,
            cartItems = cartItems,
            cartSummary = cartSummary,
            isClearingCart = isClearingCart,
            error = error
        )
    }

    data class CartViewState(
        val isLoading: Boolean,
        val cartItems: List<CartItem>,
        val cartSummary: CartSummary,
        val isClearingCart: Boolean,
        val error: String?
    ) : ViewState()

    sealed class CartViewEvent : ViewEvent {
        data class RemoveItem(val cartItemId: String) : CartViewEvent()
        data class UpdateQuantity(val cartItemId: String, val newQuantity: Int) : CartViewEvent()
        data object ClearCart : CartViewEvent()
        data object DismissError : CartViewEvent()
    }
} 