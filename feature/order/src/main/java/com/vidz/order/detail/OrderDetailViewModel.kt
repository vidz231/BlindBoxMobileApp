package com.vidz.order.detail

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.order.model.OrderItem
import com.vidz.order.model.OrderProductItem
import com.vidz.order.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor() : BaseViewModel<
        OrderDetailViewModel.OrderDetailViewEvent,
        OrderDetailViewModel.OrderDetailViewState,
        OrderDetailViewModel.OrderDetailViewModelState>(
    initState = OrderDetailViewModelState()
) {

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(isLoading = true)
            // TODO: Implement order detail loading logic
            // For now, we'll just simulate loading
            kotlinx.coroutines.delay(1000)
            
            // Simulate data
            viewModelState.value = viewModelState.value.copy(
                isLoading = false,
                order = OrderItem(
                    id = orderId,
                    orderNumber = "ORD-$orderId",
                    date = "2024-03-20",
                    status = OrderStatus.PENDING,
                    totalAmount = 150000.0,
                    items = listOf(
                        OrderProductItem(
                            name = "Blind Box A",
                            quantity = 2,
                            price = 75000.0,
                            imageUrl = "https://example.com/blindbox-a.jpg"
                        ),
                        OrderProductItem(
                            name = "Blind Box B",
                            quantity = 1,
                            price = 100000.0,
                            imageUrl = "https://example.com/blindbox-b.jpg"
                        )
                    ),
                    shippingAddress = "123 Đường ABC, Quận XYZ, TP.HCM",
                    paymentMethod = "Chuyển khoản ngân hàng",
                    note = "Giao hàng giờ hành chính",
                    estimatedDeliveryDate = "2024-03-25"
                )
            )
        }
    }

    fun cancelOrder() {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(isLoading = true)
            // TODO: Implement cancel order logic
            kotlinx.coroutines.delay(1000)
            
            val currentOrder = viewModelState.value.order
            if (currentOrder != null) {
                viewModelState.value = viewModelState.value.copy(
                    isLoading = false,
                    order = currentOrder.copy(status = OrderStatus.CANCELLED),
                    isCancelled = true
                )
            }
        }
    }

    override fun onTriggerEvent(event: OrderDetailViewEvent) {
        when (event) {
            is OrderDetailViewEvent.CancelOrder -> cancelOrder()
            is OrderDetailViewEvent.RefreshOrder -> {
                viewModelState.value.order?.id?.let { loadOrderDetail(it) }
            }
        }
    }

    // Events
    sealed class OrderDetailViewEvent : ViewEvent {
        object CancelOrder : OrderDetailViewEvent()
        object RefreshOrder : OrderDetailViewEvent()
    }

    // View Model State
    data class OrderDetailViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val order: OrderItem? = null,
        val isCancelled: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = OrderDetailViewState(
            isLoading = isLoading,
            error = error,
            order = order,
            isCancelled = isCancelled
        )
    }

    // UI State
    data class OrderDetailViewState(
        val isLoading: Boolean,
        val error: String?,
        val order: OrderItem?,
        val isCancelled: Boolean
    ) : ViewState()
}

// Extended OrderItem for detail view
data class OrderProductItem(
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null
)

// Extended OrderItem with additional details
data class OrderItem(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: OrderStatus,
    val totalAmount: Double,
    val items: List<OrderProductItem>,
    val shippingAddress: String,
    val paymentMethod: String,
    val note: String? = null,
    val estimatedDeliveryDate: String? = null
) 