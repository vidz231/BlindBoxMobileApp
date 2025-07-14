package com.vidz.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.domain.Success
import com.vidz.domain.ServerError
import com.vidz.domain.Init
import com.vidz.domain.model.PaymentMethod
import com.vidz.domain.model.ShippingInfo
import com.vidz.domain.model.Voucher
import com.vidz.domain.usecase.CreateOrderFromCartUseCase
import com.vidz.domain.usecase.CreateOrderUseCase
import com.vidz.domain.usecase.GetCurrentUserUseCase
import com.vidz.domain.usecase.GetShippingInfoByIdUseCase
import com.vidz.domain.usecase.ObserveCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrderFromCartUseCase: CreateOrderFromCartUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
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
        data class SelectPaymentMethod(val paymentMethod: PaymentMethod) : CheckoutViewEvent()
        data object CreateOrderFromCart : CheckoutViewEvent()
        data object CreateOrderBuyNow : CheckoutViewEvent()
        data object ClearSnackbarMessage : CheckoutViewEvent()
        data object ResetOrderState : CheckoutViewEvent()
    }

    data class CheckoutViewState(
        val isLoading: Boolean = true,
        val checkoutItems: List<CheckoutItemData> = emptyList(),
        val selectedShippingInfo: ShippingInfo? = null,
        val appliedCoupon: Voucher? = null,
        val selectedPaymentMethod: PaymentMethod = PaymentMethod.Vnpay,
        val subtotal: Double = 0.0,
        val shippingFee: Double = 0.0,
        val discountAmount: Double = 0.0,
        val finalTotal: Double = 0.0,
        val isProcessingOrder: Boolean = false,
        val orderCreated: Boolean = false,
        val paymentRedirectUrl: String = "",
        val errorMessage: String = "",
        val snackbarMessage: String = ""
    ) : ViewState()

    data class CheckoutViewModelState(
        val isLoading: Boolean = true,
        val checkoutItems: List<CheckoutItemData> = emptyList(),
        val selectedShippingInfo: ShippingInfo? = null,
        val appliedCoupon: Voucher? = null,
        val selectedPaymentMethod: PaymentMethod = PaymentMethod.Vnpay,
        val subtotal: Double = 0.0,
        val shippingFee: Double = 15000.0, // Default shipping fee
        val discountAmount: Double = 0.0,
        val finalTotal: Double = 0.0,
        val isProcessingOrder: Boolean = false,
        val orderCreated: Boolean = false,
        val paymentRedirectUrl: String = "",
        val errorMessage: String = "",
        val buyNowItems: List<CheckoutItemData> = emptyList(),
        val checkoutType: CheckoutType = CheckoutType.FROM_CART,
        val snackbarMessage: String = ""
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            val uiState = CheckoutViewState(
                isLoading = isLoading,
                checkoutItems = checkoutItems,
                selectedShippingInfo = selectedShippingInfo,
                appliedCoupon = appliedCoupon,
                selectedPaymentMethod = selectedPaymentMethod,
                subtotal = subtotal,
                shippingFee = shippingFee,
                discountAmount = discountAmount,
                finalTotal = calculateFinalTotal(),
                isProcessingOrder = isProcessingOrder,
                orderCreated = orderCreated,
                paymentRedirectUrl = paymentRedirectUrl,
                errorMessage = errorMessage,
                snackbarMessage = snackbarMessage
            )
            if (orderCreated && paymentRedirectUrl.isNotEmpty()) {
                Log.d("CheckoutViewModel", "toUiState() - orderCreated: $orderCreated, paymentUrl: $paymentRedirectUrl")
            }
            return uiState
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
            is CheckoutViewEvent.SelectPaymentMethod -> selectPaymentMethod(event.paymentMethod)
            is CheckoutViewEvent.CreateOrderFromCart -> createOrderFromCart()
            is CheckoutViewEvent.CreateOrderBuyNow -> createOrderBuyNow()
            is CheckoutViewEvent.ClearSnackbarMessage -> clearSnackbarMessage()
            is CheckoutViewEvent.ResetOrderState -> resetOrderState()
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            try {
                Log.d("CheckoutViewModel", "Loading cart items")
                viewModelState.update { it.copy(isLoading = true) }
                
                val cartItems = observeCartItemsUseCase().first()
                Log.d("CheckoutViewModel", "Found ${cartItems.size} cart items")
                
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
                Log.d("CheckoutViewModel", "Cart subtotal: $subtotal")

                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        checkoutItems = checkoutItems,
                        subtotal = subtotal,
                        checkoutType = CheckoutType.FROM_CART,
                        errorMessage = ""
                    )
                }
                Log.d("CheckoutViewModel", "Cart items loaded successfully")
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Failed to load cart items", e)
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

    private fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        viewModelState.update {
            it.copy(selectedPaymentMethod = paymentMethod)
        }
    }

    fun updateSelectedShippingInfo(shippingInfo: ShippingInfo) {
        selectShippingInfo(shippingInfo)
    }

    fun loadSelectedShippingInfo(shippingInfoId: Long? = null) {
        viewModelScope.launch {
            try {
                val idToLoad = shippingInfoId ?: savedStateHandle.get<Long>("selected_shipping_info_id")
                if (idToLoad != null && idToLoad > 0) {
                    getShippingInfoByIdUseCase(idToLoad).collect { result ->
                        when (result) {
                            is Success -> {
                                selectShippingInfo(result.data)
                                // Clear any previous error messages
                                viewModelState.update {
                                    it.copy(errorMessage = "")
                                }
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

    private fun clearSnackbarMessage() {
        viewModelState.update {
            it.copy(snackbarMessage = "")
        }
    }

    private fun resetOrderState() {
        viewModelState.update {
            it.copy(
                orderCreated = false,
                paymentRedirectUrl = "",
                errorMessage = ""
            )
        }
    }

    private suspend fun getCurrentUserId(): Long? {
        return try {
            var userId: Long? = null
            getCurrentUserUseCase().collect { result ->
                when (result) {
                is Success -> {
                        Log.d("CheckoutViewModel", "Current user ID: ${result.data.accountId}")
                        userId = result.data.accountId
                    }
                    is ServerError -> {
                        Log.e("CheckoutViewModel", "Server error getting current user: ${result.message}")
                        userId = null
                    }
                    is Init -> {
                        Log.d("CheckoutViewModel", "Initializing current user request")
                }
                else -> {
                        Log.e("CheckoutViewModel", "Failed to get current user: $result")
                        userId = null
                }
            }
            }
            userId
        } catch (e: Exception) {
            Log.e("CheckoutViewModel", "Exception getting current user", e)
            null
        }
    }

    private fun createOrderFromCart() {
        viewModelScope.launch {
            try {
                Log.d("CheckoutViewModel", "Starting createOrderFromCart")
                
                // Check shipping address first
                val shippingInfo = viewModelState.value.selectedShippingInfo
                if (shippingInfo == null) {
                    Log.w("CheckoutViewModel", "No shipping address selected")
                    viewModelState.update {
                        it.copy(
                            snackbarMessage = "Please select a shipping address to continue"
                        )
                    }
                    return@launch
                }

                Log.d("CheckoutViewModel", "Shipping info: ${shippingInfo.shippingInfoId}, ${shippingInfo.name}")
                Log.d("CheckoutViewModel", "Applied coupon: ${viewModelState.value.appliedCoupon?.voucherId}")
                Log.d("CheckoutViewModel", "Cart checkout items: ${viewModelState.value.checkoutItems.size}")
                
                // Check if cart is empty
                if (viewModelState.value.checkoutItems.isEmpty()) {
                    Log.w("CheckoutViewModel", "Cart is empty, cannot create order")
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "Your cart is empty. Please add items before checkout."
                        )
                    }
                    return@launch
                }
                
                // Get current user ID
                val currentUserId = getCurrentUserId()
                if (currentUserId == null) {
                    Log.e("CheckoutViewModel", "Unable to get current user ID")
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "Unable to get user information. Please try logging in again."
                        )
                    }
                    return@launch
                }
                
                // Log summary of order being created
                Log.d("CheckoutViewModel", "Creating order with:")
                Log.d("CheckoutViewModel", "  - Account ID: $currentUserId")
                Log.d("CheckoutViewModel", "  - Shipping Info ID: ${shippingInfo.shippingInfoId}")
                Log.d("CheckoutViewModel", "  - Voucher ID: ${viewModelState.value.appliedCoupon?.voucherId}")
                Log.d("CheckoutViewModel", "  - Number of items: ${viewModelState.value.checkoutItems.size}")

                viewModelState.update { 
                    it.copy(
                        isProcessingOrder = true,
                        errorMessage = "",
                        snackbarMessage = ""
                    ) 
                }

                createOrderFromCartUseCase(
                    accountId = currentUserId,
                    shippingInfoId = shippingInfo.shippingInfoId,
                    voucherId = viewModelState.value.appliedCoupon?.voucherId,
                    paymentMethod = viewModelState.value.selectedPaymentMethod
                ).collect { result ->
                    Log.d("CheckoutViewModel", "Order creation result: $result")
                    when (result) {
                        is Success -> {
                            Log.d("CheckoutViewModel", "Order created successfully: ${result.data.order.orderId}")
                            Log.d("CheckoutViewModel", "Payment URL: ${result.data.paymentRedirectUrl}")
                            viewModelState.update {
                                val newState = it.copy(
                                    isProcessingOrder = false,
                                    orderCreated = true,
                                    paymentRedirectUrl = result.data.paymentRedirectUrl
                                )
                                Log.d("CheckoutViewModel", "Updated cart state - orderCreated: ${newState.orderCreated}, paymentUrl: ${newState.paymentRedirectUrl}")
                                newState
                            }
                        }
                        is ServerError -> {
                            Log.e("CheckoutViewModel", "Server error: ${result.message}")
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Server error: ${result.message}"
                                )
                            }
                        }
                        is Init -> {
                            Log.d("CheckoutViewModel", "Order creation initialized")
                        }
                        else -> {
                            Log.e("CheckoutViewModel", "Unknown error result: $result")
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Failed to create order. Unknown error: ${result.javaClass.simpleName}"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Exception creating order", e)
                viewModelState.update {
                    it.copy(
                        isProcessingOrder = false,
                        errorMessage = "Error creating order: ${e.message}"
                    )
                }
            }
        }
    }

    private fun createOrderBuyNow() {
        viewModelScope.launch {
            try {
                Log.d("CheckoutViewModel", "Starting createOrderBuyNow")
                
                // Check shipping address first
                val shippingInfo = viewModelState.value.selectedShippingInfo
                if (shippingInfo == null) {
                    Log.w("CheckoutViewModel", "No shipping address selected for buy now")
                    viewModelState.update {
                        it.copy(
                            snackbarMessage = "Please select a shipping address to continue"
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

                Log.d("CheckoutViewModel", "Buy now order details: ${orderDetailRequests.size} items")
                
                // Check if buy now items are empty
                if (orderDetailRequests.isEmpty()) {
                    Log.w("CheckoutViewModel", "Buy now items are empty, cannot create order")
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "No items to purchase. Please select an item first."
                        )
                    }
                    return@launch
                }
                
                orderDetailRequests.forEach { item ->
                    Log.d("CheckoutViewModel", "Order item: skuId=${item.skuId}, quantity=${item.quantity}, slotId=${item.slotId}")
                }
                
                // Get current user ID
                val currentUserId = getCurrentUserId()
                if (currentUserId == null) {
                    Log.e("CheckoutViewModel", "Unable to get current user ID for buy now")
                    viewModelState.update {
                        it.copy(
                            isProcessingOrder = false,
                            errorMessage = "Unable to get user information. Please try logging in again."
                        )
                    }
                    return@launch
                }
                
                // Log summary of buy now order being created
                Log.d("CheckoutViewModel", "Creating buy now order with:")
                Log.d("CheckoutViewModel", "  - Account ID: $currentUserId")
                Log.d("CheckoutViewModel", "  - Shipping Info ID: ${shippingInfo.shippingInfoId}")
                Log.d("CheckoutViewModel", "  - Voucher ID: ${viewModelState.value.appliedCoupon?.voucherId}")
                Log.d("CheckoutViewModel", "  - Number of items: ${orderDetailRequests.size}")

                viewModelState.update { 
                    it.copy(
                        isProcessingOrder = true,
                        errorMessage = "",
                        snackbarMessage = ""
                    ) 
                }

                createOrderUseCase(
                    accountId = currentUserId,
                    shippingInfoId = shippingInfo.shippingInfoId,
                    items = orderDetailRequests,
                    voucherId = viewModelState.value.appliedCoupon?.voucherId,
                    paymentMethod = viewModelState.value.selectedPaymentMethod
                ).collect { result ->
                    Log.d("CheckoutViewModel", "Buy now order creation result: $result")
                    when (result) {
                        is Success -> {
                            Log.d("CheckoutViewModel", "Buy now order created successfully: ${result.data.order.orderId}")
                            Log.d("CheckoutViewModel", "Payment URL: ${result.data.paymentRedirectUrl}")
                            viewModelState.update {
                                val newState = it.copy(
                                    isProcessingOrder = false,
                                    orderCreated = true,
                                    paymentRedirectUrl = result.data.paymentRedirectUrl
                                )
                                Log.d("CheckoutViewModel", "Updated state - orderCreated: ${newState.orderCreated}, paymentUrl: ${newState.paymentRedirectUrl}")
                                newState
                            }
                        }
                        is ServerError -> {
                            Log.e("CheckoutViewModel", "Server error for buy now: ${result.message}", Exception(result.message))
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Server error: ${result.message}"
                                )
                            }
                        }
                        is Init -> {
                            Log.d("CheckoutViewModel", "Buy now order creation initialized")
                        }
                        else -> {
                            val exception = Exception("Unknown error result: ${result.javaClass.simpleName}")
                            Log.e("CheckoutViewModel", "Unknown error result for buy now: $result", exception)
                            viewModelState.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    errorMessage = "Failed to create order. Unknown error: ${result.javaClass.simpleName}"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Exception creating buy now order", e)
                    e.printStackTrace() // Print full stack trace to console
                viewModelState.update {
                    it.copy(
                        isProcessingOrder = false,
                        errorMessage = "Error creating order: ${e.message}"
                    )
                }
            }
        }
    }
} 