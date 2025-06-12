package com.vidz.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.CheckoutItemComponent
import com.vidz.base.components.CheckoutSummaryComponent
import com.vidz.base.components.PrimaryButton
import com.vidz.base.components.TopAppBarWithBack
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

    LaunchedEffect(uiState.orderCreated) {
        if (uiState.orderCreated && uiState.paymentRedirectUrl.isNotEmpty()) {
            onNavigateToPayment(uiState.paymentRedirectUrl)
        }
    }

    // Handle shipping info selection from navigation
    LaunchedEffect(navController) {
        navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<ShippingInfo>("selected_shipping_info")?.observeForever { shippingInfo ->
            shippingInfo?.let {
                viewModel.updateSelectedShippingInfo(it)
                // Clear the saved state to prevent re-triggering
                navController.currentBackStackEntry?.savedStateHandle?.remove<ShippingInfo>("selected_shipping_info")
            }
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
    //endregion

    //region ui
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBarWithBack(
            title = "Checkout",
            onBackClick = onBackClick
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shipping Information Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = handleShippingInfoClick
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Delivery Address",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val shippingInfo = uiState.selectedShippingInfo
                            if (shippingInfo != null) {
                                Text(
                                    text = shippingInfo.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = shippingInfo.phoneNumber,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${shippingInfo.address}, ${shippingInfo.ward}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${shippingInfo.district}, ${shippingInfo.city}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "Select shipping address",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Mapbox Area Placeholder
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Map",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Mapbox Integration Area",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Map will be rendered here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Checkout Items
                items(uiState.checkoutItems) { item ->
                    CheckoutItemComponent(
                        name = item.name,
                        price = item.price,
                        quantity = item.quantity,
                        imageUrl = item.imageUrl,
                        blindBoxName = item.blindBoxName,
                        slotNumber = item.slotNumber,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Coupon Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = handleCouponClick
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = "Coupon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    val appliedCoupon = uiState.appliedCoupon
                                    Text(
                                        text = if (appliedCoupon != null) {
                                            "Coupon Applied: ${appliedCoupon.code}"
                                        } else {
                                            "Apply Coupon"
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (appliedCoupon != null) {
                                        Text(
                                            text = "Discount: ${(appliedCoupon.discountRate * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            val appliedCouponForButton = uiState.appliedCoupon
                            if (appliedCouponForButton != null) {
                                TextButton(
                                    onClick = handleCouponRemove
                                ) {
                                    Text("Remove")
                                }
                            }
                        }
                    }
                }

                // Order Summary
                item {
                    CheckoutSummaryComponent(
                        subtotal = uiState.subtotal,
                        shippingFee = uiState.shippingFee,
                        discountAmount = uiState.discountAmount,
                        finalTotal = uiState.finalTotal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Place Order Button
                item {
                    PrimaryButton(
                        text = if (uiState.isProcessingOrder) "Processing..." else "Place Order",
                        onClick = handlePlaceOrder,
                        enabled = !uiState.isProcessingOrder && 
                                 uiState.selectedShippingInfo != null &&
                                 uiState.checkoutItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.errorMessage.isNotEmpty()) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Bottom padding for safe area
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    //region Dialog and Sheet
    if (showCouponBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCouponBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Apply Coupon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                var couponCode by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it },
                    label = { Text("Coupon Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showCouponBottomSheet = false },
                        modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
                
                val appliedCouponInSheet = uiState.appliedCoupon
                if (appliedCouponInSheet != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Applied Coupon: ${appliedCouponInSheet.code}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Discount: ${(appliedCouponInSheet.discountRate * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    handleCouponRemove()
                                    showCouponBottomSheet = false
                                }
                            ) {
                                Text("Remove Coupon")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    //end region
    //end region
} 