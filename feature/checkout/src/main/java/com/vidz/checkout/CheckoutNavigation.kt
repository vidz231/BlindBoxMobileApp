package com.vidz.checkout

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.ShippingInfo

fun NavGraphBuilder.addCheckoutNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    // Main Checkout Screen
    composable(
        route = "checkout?checkoutType={checkoutType}&buyNowSkuId={buyNowSkuId}&buyNowQuantity={buyNowQuantity}&buyNowName={buyNowName}&buyNowPrice={buyNowPrice}&buyNowImageUrl={buyNowImageUrl}&buyNowBlindBoxName={buyNowBlindBoxName}",
        arguments = listOf(
            navArgument("checkoutType") { 
                type = NavType.StringType
                defaultValue = "FROM_CART"
            },
            navArgument("buyNowSkuId") { 
                type = NavType.StringType
                defaultValue = "0"
            },
            navArgument("buyNowQuantity") { 
                type = NavType.StringType
                defaultValue = "1"
            },
            navArgument("buyNowName") { 
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("buyNowPrice") { 
                type = NavType.StringType
                defaultValue = "0"
            },
            navArgument("buyNowImageUrl") { 
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("buyNowBlindBoxName") { 
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { backStackEntry ->
        val checkoutType = backStackEntry.arguments?.getString("checkoutType") ?: "FROM_CART"
        val buyNowSkuId = backStackEntry.arguments?.getString("buyNowSkuId")?.toLongOrNull() ?: 0L
        val buyNowQuantity = backStackEntry.arguments?.getString("buyNowQuantity")?.toIntOrNull() ?: 1
        val buyNowName = backStackEntry.arguments?.getString("buyNowName") ?: ""
        val buyNowPrice = backStackEntry.arguments?.getString("buyNowPrice")?.toDoubleOrNull() ?: 0.0
        val buyNowImageUrl = backStackEntry.arguments?.getString("buyNowImageUrl") ?: ""
        val buyNowBlindBoxName = backStackEntry.arguments?.getString("buyNowBlindBoxName") ?: ""

        val buyNowItems = if (checkoutType == "BUY_NOW" && buyNowSkuId > 0) {
            listOf(
                CheckoutItemData(
                    skuId = buyNowSkuId,
                    quantity = buyNowQuantity,
                    slotId = null,
                    name = buyNowName,
                    price = buyNowPrice,
                    imageUrl = buyNowImageUrl,
                    blindBoxName = buyNowBlindBoxName
                )
            )
        } else null

        CheckoutScreen(
            onBackClick = { navController.navigateUp() },
            onNavigateToShippingSelection = {
                navController.navigate("shipping_selection")
            },
            onNavigateToPayment = { paymentUrl ->
                // TODO: Handle payment navigation
                onShowSnackbar("Redirecting to payment: $paymentUrl")
            },
            checkoutType = if (checkoutType == "BUY_NOW") CheckoutType.BUY_NOW else CheckoutType.FROM_CART,
            buyNowItems = buyNowItems,
            navController = navController
        )
    }

    // Shipping Selection Screen
    composable("shipping_selection") {
        ShippingSelectionScreen(
            onBackClick = { navController.navigateUp() },
            onShippingInfoSelected = { shippingInfo ->
                // Pass only the shipping info ID back to checkout screen
                navController.previousBackStackEntry?.savedStateHandle?.set("selected_shipping_info_id", shippingInfo.shippingInfoId)
                navController.navigateUp()
            },
            onCreateNewShippingInfo = {
                // TODO: Navigate to create shipping info screen
                onShowSnackbar("Create new shipping info - to be implemented")
            }
        )
    }
} 