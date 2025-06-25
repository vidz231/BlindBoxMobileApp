package com.vidz.order_detail

import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Init
import com.vidz.domain.ServerError
import com.vidz.domain.Success
import com.vidz.domain.usecase.GetOrderByIdUseCase
import com.vidz.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vidz.domain.model.PaymentMethod
import com.vidz.domain.model.OrderStatus as DomainOrderStatus
import com.vidz.domain.model.OrderDto
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.model.OrderStatus

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

    private fun mapOrderStatus(status: DomainOrderStatus): DomainOrderStatus = when(status) {
        is DomainOrderStatus.Created -> DomainOrderStatus.Created
        is DomainOrderStatus.Preparing -> DomainOrderStatus.Preparing
        is DomainOrderStatus.PaymentFailed -> DomainOrderStatus.PaymentFailed
        is DomainOrderStatus.PaymentExpired -> DomainOrderStatus.PaymentExpired
        is DomainOrderStatus.Canceled -> DomainOrderStatus.Canceled
        is DomainOrderStatus.ReadyForPickup -> DomainOrderStatus.ReadyForPickup
        is DomainOrderStatus.Shipping -> DomainOrderStatus.Shipping
        is DomainOrderStatus.Delivered -> DomainOrderStatus.Delivered
        is DomainOrderStatus.Received -> DomainOrderStatus.Received
        is DomainOrderStatus.Completed -> DomainOrderStatus.Completed
    }

    fun getStatusTextVi(status: DomainOrderStatus): String = when(status) {
        DomainOrderStatus.Created -> "Chờ xác nhận"
        DomainOrderStatus.Preparing -> "Đang xử lý/giao hàng"
        DomainOrderStatus.Completed -> "Hoàn thành"
        DomainOrderStatus.Canceled -> "Đã hủy/Thanh toán thất bại"
        OrderStatus.Delivered -> TODO()
        OrderStatus.PaymentExpired -> TODO()
        OrderStatus.PaymentFailed -> TODO()
        OrderStatus.ReadyForPickup -> TODO()
        OrderStatus.Received -> TODO()
        OrderStatus.Shipping -> TODO()
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
                    order = currentOrder.copy(latestStatus = DomainOrderStatus.Canceled),
                    isCancelled = true
                )
            }
        }
    }

    override fun onTriggerEvent(event: OrderDetailViewEvent) {
        when (event) {
            is OrderDetailViewEvent.CancelOrder -> cancelOrder()
            is OrderDetailViewEvent.RefreshOrder -> {
                viewModelState.value.order?.orderId?.let { loadOrderDetail(it.toString()) }
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
    val status: DomainOrderStatus,
    val totalAmount: Double,
    val items: List<OrderProductItem>,
    val shippingAddress: String,
    val paymentMethod: String,
    val note: String? = null,
    val estimatedDeliveryDate: String? = null
) 