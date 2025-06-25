package com.vidz.order_detail

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
import com.vidz.domain.usecase.GetOrderByIdUseCase
import com.vidz.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vidz.domain.model.PaymentMethod
import com.vidz.domain.model.OrderStatus as DomainOrderStatus
import com.vidz.domain.model.OrderDto
import com.vidz.domain.model.OrderDetail

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : BaseViewModel<
        OrderDetailViewModel.OrderDetailViewEvent,
        OrderDetailViewModel.OrderDetailViewState,
        OrderDetailViewModel.OrderDetailViewModelState>(
    initState = OrderDetailViewModelState()
) {

    private fun paymentMethodToString(pm: PaymentMethod): String = when(pm) {
        is PaymentMethod.InternalWallet -> "Ví nội bộ"
        is PaymentMethod.Paypal -> "Paypal"
        is PaymentMethod.Vnpay -> "Vnpay"
    }

    private fun mapOrderStatus(status: DomainOrderStatus): OrderStatus = when(status) {
        is DomainOrderStatus.Created -> OrderStatus.PENDING
        is DomainOrderStatus.Preparing -> OrderStatus.PROCESSING
        is DomainOrderStatus.PaymentFailed -> OrderStatus.CANCELLED
        is DomainOrderStatus.PaymentExpired -> OrderStatus.CANCELLED
        is DomainOrderStatus.Canceled -> OrderStatus.CANCELLED
        is DomainOrderStatus.ReadyForPickup -> OrderStatus.PROCESSING
        is DomainOrderStatus.Shipping -> OrderStatus.PROCESSING
        is DomainOrderStatus.Delivered -> OrderStatus.COMPLETED
        is DomainOrderStatus.Received -> OrderStatus.COMPLETED
        is DomainOrderStatus.Completed -> OrderStatus.COMPLETED
    }

    fun getStatusTextVi(status: OrderStatus): String = when(status) {
        OrderStatus.PENDING -> "Chờ xác nhận"
        OrderStatus.PROCESSING -> "Đang xử lý/giao hàng"
        OrderStatus.COMPLETED -> "Hoàn thành"
        OrderStatus.CANCELLED -> "Đã hủy/Thanh toán thất bại"
    }

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            val currentState = viewModelState.value
            viewModelState.value = currentState.copy(isLoading = true, error = null)
            getOrderByIdUseCase.invoke(orderId.toLong()).collect { result ->
                when (result) {
                    is Success -> {
                        val order = result.data as OrderDto
                        viewModelState.value = currentState.copy(
                            isLoading = false,
                            order = order,
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
        val order: OrderDto? = null,
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
        val order: OrderDto?,
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