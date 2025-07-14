package com.vidz.checkout

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.CheckoutItemComponent
import com.vidz.base.components.CheckoutSummaryComponent
import com.vidz.base.components.PrimaryButton
import com.vidz.base.components.TopAppBarWithBack
import com.vidz.domain.model.PaymentMethod
import com.vidz.domain.model.ShippingInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onNavigateToShippingSelection: () -> Unit,
    onNavigateToPayment: (String) -> Unit,
    checkoutType: CheckoutType = CheckoutType.FROM_CART,
    buyNowItems: List<CheckoutItemData>? = null,
    navController: NavController? = null,
    onShowSnackbar: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCouponBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    //endregion

    //region Event Handler
    LaunchedEffect(checkoutType, buyNowItems) {
        when (checkoutType) {
            CheckoutType.FROM_CART -> {
                viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.LoadCartItems)
            }
            CheckoutType.BUY_NOW -> {
                buyNowItems?.let { items ->
                    viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.LoadBuyNowItems(items))
                }
            }
        }
    }

    LaunchedEffect(uiState.orderCreated, uiState.paymentRedirectUrl) {
        Log.d("CheckoutScreen", "LaunchedEffect triggered - orderCreated: ${uiState.orderCreated}, paymentRedirectUrl: ${uiState.paymentRedirectUrl}")
        if (uiState.orderCreated && uiState.paymentRedirectUrl.isNotEmpty()) {
            Log.d("CheckoutScreen", "Navigating to payment with URL: ${uiState.paymentRedirectUrl}")
            onNavigateToPayment(uiState.paymentRedirectUrl)
            // Reset order state after navigation to prevent re-triggering
            viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.ResetOrderState)
        }
    }

    // Handle shipping info selection from navigation
    LaunchedEffect(navController) {
        navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("selected_shipping_info_id")?.observeForever { shippingInfoId ->
            if (shippingInfoId != null && shippingInfoId > 0) {
                viewModel.loadSelectedShippingInfo(shippingInfoId)
                // Clear the saved state to prevent re-triggering
                navController.currentBackStackEntry?.savedStateHandle?.remove<Long>("selected_shipping_info_id")
            }
        }
    }

    // Handle snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        if (uiState.snackbarMessage.isNotEmpty()) {
            onShowSnackbar?.invoke(uiState.snackbarMessage)
            viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.ClearSnackbarMessage)
        }
    }

    val handleShippingInfoClick = {
        onNavigateToShippingSelection()
    }

    val handleCouponClick = {
        showCouponBottomSheet = true
    }

    val handlePlaceOrder = {
        viewModel.onTriggerEvent(
            if (checkoutType == CheckoutType.FROM_CART) {
                CheckoutViewModel.CheckoutViewEvent.CreateOrderFromCart
            } else {
                CheckoutViewModel.CheckoutViewEvent.CreateOrderBuyNow
            }
        )
    }

    val handleCouponApply = { code: String ->
        viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.ApplyCoupon(code))
    }

    val handleCouponRemove = {
        viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.RemoveCoupon)
    }

    val handlePaymentMethodSelect = { paymentMethod: PaymentMethod ->
        viewModel.onTriggerEvent(CheckoutViewModel.CheckoutViewEvent.SelectPaymentMethod(paymentMethod))
    }
    //endregion

    //region ui
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                TopAppBarWithBack(
                    title = "Checkout",
                    onBackClick = onBackClick
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Modern Shipping Information Card
                    item {
                        ModernSectionCard(
                            title = "Delivery Address",
                            icon = Icons.Default.LocationOn,
                            onClick = handleShippingInfoClick
                        ) {
                            val shippingInfo = uiState.selectedShippingInfo
                            if (shippingInfo != null) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = shippingInfo.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = shippingInfo.phoneNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${shippingInfo.address}, ${shippingInfo.ward}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${shippingInfo.district}, ${shippingInfo.city}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Select delivery address",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Modern Payment Method Section
                    item {
                        ModernSectionCard(
                            title = "Payment Method",
                            icon = Icons.Default.Payment
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val paymentMethods = listOf(
                                    PaymentMethod.Vnpay to ("VNPay" to Icons.Default.CreditCard),
                                    PaymentMethod.Paypal to ("PayPal" to Icons.Default.Payment),
                                    PaymentMethod.InternalWallet to ("Internal Wallet" to Icons.Default.AccountBalanceWallet)
                                )

                                paymentMethods.forEach { (method, nameIcon) ->
                                    val (name, icon) = nameIcon
                                    ModernPaymentOption(
                                        name = name,
                                        icon = icon,
                                        isSelected = uiState.selectedPaymentMethod == method,
                                        onClick = { handlePaymentMethodSelect(method) }
                                    )
                                }
                            }
                        }
                    }

                    // Modern Checkout Items
                    items(uiState.checkoutItems) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp
                        ) {
                            CheckoutItemComponent(
                                name = item.name,
                                price = item.price,
                                quantity = item.quantity,
                                imageUrl = item.imageUrl,
                                blindBoxName = item.blindBoxName,
                                slotNumber = item.slotNumber,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Modern Coupon Section
                    item {
                        ModernCouponCard(
                            appliedCoupon = uiState.appliedCoupon,
                            onCouponClick = handleCouponClick,
                            onCouponRemove = handleCouponRemove
                        )
                    }

                    // Modern Order Summary
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp
                        ) {
                            CheckoutSummaryComponent(
                                subtotal = uiState.subtotal,
                                shippingFee = uiState.shippingFee,
                                discountAmount = uiState.discountAmount,
                                finalTotal = uiState.finalTotal,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }

                    // Modern Place Order Button
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModernPlaceOrderButton(
                                text = if (uiState.isProcessingOrder) "Processing..." else "Place Order",
                                onClick = handlePlaceOrder,
                                enabled = !uiState.isProcessingOrder &&
                                        uiState.selectedShippingInfo != null &&
                                        uiState.checkoutItems.isNotEmpty(),
                                isProcessing = uiState.isProcessingOrder
                            )

                            if (uiState.errorMessage.isNotEmpty()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = uiState.errorMessage,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom padding for safe area
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    //region Modern Bottom Sheet
    if (showCouponBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCouponBottomSheet = false },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Modern header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Apply Coupon",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                var couponCode by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it },
                    label = { Text("Enter coupon code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showCouponBottomSheet = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (couponCode.isNotBlank()) {
                                handleCouponApply(couponCode)
                                showCouponBottomSheet = false
                            }
                        },
                        enabled = couponCode.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Apply")
                    }
                }

                val appliedCouponInSheet = uiState.appliedCoupon
                if (appliedCouponInSheet != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Applied: ${appliedCouponInSheet.code}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Discount: ${(appliedCouponInSheet.discountRate * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    handleCouponRemove()
                                    showCouponBottomSheet = false
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Remove Coupon")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    //endregion
}

// Modern Section Card Component
@Composable
private fun ModernSectionCard(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (onClick != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// Modern Payment Option Component
@Composable
private fun ModernPaymentOption(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Modern Coupon Card Component
@Composable
private fun ModernCouponCard(
    appliedCoupon: Any?, // Replace with actual coupon type
    onCouponClick: () -> Unit,
    onCouponRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCouponClick() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = "Coupon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = if (appliedCoupon != null) {
                            "Coupon Applied: ${(appliedCoupon as? CouponData)?.code ?: "CODE"}"
                        } else {
                            "Apply Coupon"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (appliedCoupon != null) {
                        Text(
                            text = "Discount: ${((appliedCoupon as? CouponData)?.discountRate?.times(100)?.toInt() ?: 0)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (appliedCoupon != null) {
                TextButton(
                    onClick = onCouponRemove,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Remove")
                }
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Modern Place Order Button Component
@Composable
private fun ModernPlaceOrderButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isProcessing: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// Placeholder for coupon data class
data class CouponData(
    val code: String,
    val discountRate: Double
)