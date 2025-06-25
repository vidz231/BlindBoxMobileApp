package com.vidz.order

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.model.OrderDto
import com.vidz.domain.model.OrderStatus
import com.vidz.domain.usecase.GetOrdersUseCase
import com.vidz.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : BaseViewModel<
        OrderViewModel.OrderViewEvent,
        OrderViewModel.OrderViewState,
        OrderViewModel.OrderViewModelState>(
    initState = OrderViewModelState()
) {

    private var onNavigateToOrderDetail: ((String) -> Unit)? = null

    fun setOnNavigateToOrderDetail(callback: (String) -> Unit) {
        onNavigateToOrderDetail = callback
    }

    init {
        loadOrders()
    }

    private fun loadOrders(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            val currentState = viewModelState.value
            viewModelState.value = currentState.copy(isLoading = true, error = null)
            getOrdersUseCase.invoke(page, size).collect { result ->
                when (result) {
                    is Success -> {
                        val orders = result.data // List<OrderDto>
                        viewModelState.value = currentState.copy(
                            isLoading = false,
                            orders = orders,
                            error = null
                        )
                    }
                    is ServerError -> {
                        viewModelState.value = currentState.copy(
                            isLoading = false,
                            error = result.message ?: "Đã có lỗi xảy ra"
                        )
                    }
                    is Init -> {
                        viewModelState.value = currentState.copy(isLoading = false)
                    }
                }
            }
        }
    }

    override fun onTriggerEvent(event: OrderViewEvent) {
        when (event) {
            is OrderViewEvent.RefreshOrders -> loadOrders()
            is OrderViewEvent.ViewOrderDetails -> {
                onNavigateToOrderDetail?.invoke(event.orderId)
            }
        }
    }

    // Events
    sealed class OrderViewEvent : ViewEvent {
        object RefreshOrders : OrderViewEvent()
        data class ViewOrderDetails(val orderId: String) : OrderViewEvent()
    }

    // View Model State
    data class OrderViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val orders: List<OrderDto> = emptyList()
    ) : ViewModelState() {
        override fun toUiState(): ViewState = OrderViewState(
            isLoading = isLoading,
            error = error,
            orders = orders
        )
    }

    // UI State
    data class OrderViewState(
        val isLoading: Boolean,
        val error: String?,
        val orders: List<OrderDto>
    ) : ViewState()
} 