package com.vidz.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.CartItemComponent
import com.vidz.base.components.PrimaryButton
import com.vidz.base.components.TopAppBarWithBack

@Composable
fun CartScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearCartDialog by remember { mutableStateOf(false) }
    //endregion

    //region Event Handler
    val onEvent = { event: CartViewModel.CartViewEvent ->
        viewModel.onTriggerEvent(event)
    }

    val onQuantityChange = { cartItemId: String, newQuantity: Int ->
        onEvent(CartViewModel.CartViewEvent.UpdateQuantity(cartItemId, newQuantity))
    }

    val onRemoveItem = { cartItemId: String ->
        onEvent(CartViewModel.CartViewEvent.RemoveItem(cartItemId))
    }

    val onClearCart = {
        showClearCartDialog = true
    }

    val onCheckout = {
        navController.navigate("checkout?checkoutType=FROM_CART")
    }
    //endregion

    //region ui
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBarWithBack(
            title = "Cart",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Clear All Button (if items exist)
            if (uiState.cartItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onClearCart,
                        enabled = !uiState.isClearingCart
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.cartItems.isEmpty() -> {
                // Empty Cart State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Add some items to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            else -> {
                // Cart Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.cartItems,
                        key = { it.id }
                    ) { cartItem ->
                        CartItemComponent(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                onQuantityChange(cartItem.id, newQuantity)
                            },
                            onRemove = {
                                onRemoveItem(cartItem.id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cart Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Items (${uiState.cartSummary.totalQuantity})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${uiState.cartSummary.itemsCount} products",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Divider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", uiState.cartSummary.totalPrice)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout Button
                PrimaryButton(
                    text = "Proceed to Checkout",
                    onClick = onCheckout,
                    enabled = uiState.cartItems.isNotEmpty() && !uiState.isClearingCart
                )
            }
        }
    }
    }

    //region Dialog and Sheet
    // Clear Cart Confirmation Dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = {
                Text("Clear Cart")
            },
            text = {
                Text("Are you sure you want to remove all items from your cart?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(CartViewModel.CartViewEvent.ClearCart)
                        showClearCartDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Error Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error display
            onEvent(CartViewModel.CartViewEvent.DismissError)
        }
    }
    //endregion
    //endregion
}