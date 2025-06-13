package com.vidz.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.model.Voucher
import com.vidz.domain.usecase.CreateOrderFromCartUseCase
import com.vidz.domain.usecase.CreateOrderUseCase
import com.vidz.domain.usecase.GetShippingInfoByIdUseCase
import com.vidz.domain.usecase.ObserveCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrderFromCartUseCase: CreateOrderFromCartUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getShippingInfoByIdUseCase: GetShippingInfoByIdUseCase,
    private val observeCartItemsUseCase: ObserveCartItemsUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<
        CheckoutViewModel.CheckoutViewEvent,
        CheckoutViewModel.CheckoutViewState,
        CheckoutViewModel.CheckoutViewModelState
        >(CheckoutViewModelState()) {

    sealed class CheckoutViewEvent : ViewEvent {
        data object LoadCartItems : CheckoutViewEvent()
        data class LoadBuyNowItems(val items: List<CheckoutItemData>) : CheckoutViewEvent()
        data class SelectShippingInfo(val shippingInfo: ShippingInfo) : CheckoutViewEvent()
        data class ApplyCoupon(val code: String) : CheckoutViewEvent()
        data object RemoveCoupon : CheckoutViewEvent()
        data object CreateOrderFromCart : CheckoutViewEvent()
        data object CreateOrderBuyNow : CheckoutViewEvent()
    }

    data class CheckoutViewState(
        val isLoading: Boolean = true,
        val checkoutItems: List<CheckoutItemData> = emptyList(),
        val selectedShippingInfo: ShippingInfo? = null,
        val appliedCoupon: Voucher? = null,
        val subtotal: Double = 0.0,
        val shippingFee: Double = 0.0,
        val discountAmount: Double = 0.0,
        val finalTotal: Double = 0.0,
        val isProcessingOrder: Boolean = false,
        val orderCreated: Boolean = false,
        val paymentRedirectUrl: String = "",
        val errorMessage: String = ""
    ) : ViewState()

    data class CheckoutViewModelState(
        val isLoading: Boolean = true,
        val checkoutItems: List<CheckoutItemData> = emptyList(),
        val selectedShippingInfo: ShippingInfo? = null,
        val appliedCoupon: Voucher? = null,
        val subtotal: Double = 0.0,
        val shippingFee: Double = 15000.0, // Default shipping fee
        val discountAmount: Double = 0.0,
        val finalTotal: Double = 0.0,
        val isProcessingOrder: Boolean = false,
        val orderCreated: Boolean = false,
        val paymentRedirectUrl: String = "",
        val errorMessage: String = "",
        val buyNowItems: List<CheckoutItemData> = emptyList(),
        val checkoutType: CheckoutType = CheckoutType.FROM_CART
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return CheckoutViewState(
                isLoading = isLoading,
                checkoutItems = checkoutItems,
                selectedShippingInfo = selectedShippingInfo,
                appliedCoupon = appliedCoupon,
                subtotal = subtotal,
                shippingFee = shippingFee,
                discountAmount = discountAmount,
                finalTotal = calculateFinalTotal(),
                isProcessingOrder = isProcessingOrder,
                orderCreated = orderCreated,
                paymentRedirectUrl = paymentRedirectUrl,
                errorMessage = errorMessage
            )
        }

        private fun calculateFinalTotal(): Double {
            val totalBeforeDiscount = subtotal + shippingFee
            return totalBeforeDiscount - discountAmount
        }
    }

    override fun onTriggerEvent(event: CheckoutViewEvent) {
        when (event) {
            is CheckoutViewEvent.LoadCartItems -> loadCartItems()
            is CheckoutViewEvent.LoadBuyNowItems -> loadBuyNowItems(event.items)
            is CheckoutViewEvent.SelectShippingInfo -> selectShippingInfo(event.shippingInfo)
            is CheckoutViewEvent.ApplyCoupon -> applyCoupon(event.code)
            is CheckoutViewEvent.RemoveCoupon -> removeCoupon()
            is CheckoutViewEvent.CreateOrderFromCart -> createOrderFromCart()
            is CheckoutViewEvent.CreateOrderBuyNow -> createOrderBuyNow()
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            try {
                viewModelState.update { it.copy(isLoading = true) }
                
                val cartItems = observeCartItemsUseCase().first()
                val checkoutItems = cartItems.map { cartItem ->
                    CheckoutItemData(
                        skuId = cartItem.sku.skuId,
                        quantity = cartItem.quantity,
                        slotId = cartItem.slot?.slotId,
                        name = cartItem.sku.name,
                        price = cartItem.sku.price,
                        imageUrl = cartItem.sku.image.imageUrl,
                        blindBoxName = cartItem.sku.blindBox.name,
                        slotNumber = cartItem.slot?.position
                    )
                }

                val subtotal = checkoutItems.sumOf { it.price * it.quantity }

                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        checkoutItems = checkoutItems,
                        subtotal = subtotal,
                        checkoutType = CheckoutType.FROM_CART,
                        errorMessage = ""
                    )
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load cart items: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadBuyNowItems(items: List<CheckoutItemData>) {
        viewModelScope.launch {
            val subtotal = items.sumOf { it.price * it.quantity }

            viewModelState.update {
                it.copy(
                    isLoading = false,
                    checkoutItems = items,
                    buyNowItems = items,
                    subtotal = subtotal,
                    checkoutType = CheckoutType.BUY_NOW,
                    errorMessage = ""
                )
            }
        }
    }

    private fun selectShippingInfo(shippingInfo: ShippingInfo) {
        viewModelState.update {
            it.copy(selectedShippingInfo = shippingInfo)
        }
    }

    fun updateSelectedShippingInfo(shippingInfo: ShippingInfo) {
        selectShippingInfo(shippingInfo)
    }

    fun loadSelectedShippingInfo() {
        viewModelScope.launch {
            try {
                val shippingInfoId = savedStateHandle.get<Long>("selected_shipping_info_id")
                if (shippingInfoId != null && shippingInfoId > 0) {
                    getShippingInfoByIdUseCase(shippingInfoId).collect { result ->
                        when (result) {
                            is Success -> {
                                selectShippingInfo(result.data)
                            }
                            else -> {
                                viewModelState.update {
                                    it.copy(
                                        errorMessage = "Failed to load selected shipping information"
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        errorMessage = "Failed to load shipping information: ${e.message}"
                    )
                }
            }
        }
    }

    private fun applyCoupon(code: String) {
        viewModelScope.launch {
            // TODO: Implement coupon validation API call
            // For now, create a mock voucher
            val mockVoucher = Voucher(
                voucherId = 1L,
                code = code,
                discountRate = 0.1, // 10% discount
                limitAmount = 50000.0
            )

            val discountAmount = minOf(
                viewModelState.value.subtotal * mockVoucher.discountRate,
                mockVoucher.limitAmount
            )

            viewModelState.update {
                it.copy(
                    appliedCoupon = mockVoucher,
                    discountAmount = discountAmount,
                    errorMessage = ""
                )
            }
        }
    }

    private fun removeCoupon() {
        viewModelState.update {
            it.copy(
                appliedCoupon = null,
                discountAmount = 0.0
            )
        }
    }

    private fun createOrderFromCart() {
        viewModelScope.launch {
            try {
                viewModelState.update { 
                    it.copy(
                        isProcessingOrder = true,
                        errorMessage = ""
                    ) 
                }

                val shippingInfo = viewModelState.value.selectedShippingInfo
                if (shippingInfo == null) {
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "Please select shipping information"
                        )
                    }
                    return@launch
                }

                createOrderFromCartUseCase(
                    accountId = 1L, // TODO: Get from user session
                    shippingInfoId = shippingInfo.shippingInfoId,
                    voucherId = viewModelState.value.appliedCoupon?.voucherId
                ).collect { result ->
                    when (result) {
                        is Success -> {
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    orderCreated = true,
                                    paymentRedirectUrl = result.data.paymentRedirectUrl
                                )
                            }
                        }
                        else -> {
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Failed to create order. Please try again."
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        isProcessingOrder = false,
                        errorMessage = "An error occurred: ${e.message}"
                    )
                }
            }
        }
    }

    private fun createOrderBuyNow() {
        viewModelScope.launch {
            try {
                viewModelState.update { 
                    it.copy(
                        isProcessingOrder = true,
                        errorMessage = ""
                    ) 
                }

                val shippingInfo = viewModelState.value.selectedShippingInfo
                if (shippingInfo == null) {
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "Please select shipping information"
                        )
                    }
                    return@launch
                }

                val orderDetailRequests = viewModelState.value.buyNowItems.map { item ->
                    com.vidz.domain.repository.OrderDetailRequest(
                        skuId = item.skuId,
                        quantity = item.quantity,
                        slotId = item.slotId
                    )
                }

                createOrderUseCase(
                    accountId = 1L, // TODO: Get from user session
                    shippingInfoId = shippingInfo.shippingInfoId,
                    items = orderDetailRequests,
                    voucherId = viewModelState.value.appliedCoupon?.voucherId
                ).collect { result ->
                    when (result) {
                        is Success -> {
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    orderCreated = true,
                                    paymentRedirectUrl = result.data.paymentRedirectUrl
                                )
                            }
                        }
                        else -> {
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Failed to create order. Please try again."
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        isProcessingOrder = false,
                        errorMessage = "An error occurred: ${e.message}"
                    )
                }
            }
        }
    }
} 