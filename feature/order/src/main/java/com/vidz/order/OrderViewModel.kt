package com.vidz.order

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor() : BaseViewModel<
        OrderViewModel.OrderViewEvent,
        OrderViewModel.OrderViewState,
        OrderViewModel.OrderViewModelState>(
    initState = OrderViewModelState()
) {

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(isLoading = true)
            // TODO: Implement order loading logic
            // For now, we'll just simulate loading
            kotlinx.coroutines.delay(1000)
            viewModelState.value = viewModelState.value.copy(
                isLoading = false,
                orders = listOf(
                    OrderItem(
                        id = "1",
                        orderNumber = "ORD-001",
                        date = "2024-03-20",
                        status = OrderStatus.PENDING,
                        totalAmount = 150000.0,
                        items = listOf(
                            OrderProductItem(
                                name = "Blind Box A",
                                quantity = 2,
                                price = 75000.0
                            )
                        )
                    ),
                    OrderItem(
                        id = "2",
                        orderNumber = "ORD-002",
                        date = "2024-03-19",
                        status = OrderStatus.COMPLETED,
                        totalAmount = 250000.0,
                        items = listOf(
                            OrderProductItem(
                                name = "Blind Box B",
                                quantity = 1,
                                price = 250000.0
                            )
                        )
                    )
                )
            )
        }
    }

    override fun onTriggerEvent(event: OrderViewEvent) {
        when (event) {
            is OrderViewEvent.RefreshOrders -> loadOrders()
            is OrderViewEvent.ViewOrderDetails -> {
                // TODO: Implement order details navigation
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
        val orders: List<OrderItem> = emptyList()
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
        val orders: List<OrderItem>
    ) : ViewState()
}

// Models
data class OrderItem(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: OrderStatus,
    val totalAmount: Double,
    val items: List<OrderProductItem>
)

data class OrderProductItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED
} 