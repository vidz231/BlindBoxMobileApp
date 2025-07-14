package com.vidz.checkout

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations
import com.vidz.domain.model.ShippingInfo

fun NavGraphBuilder.addCheckoutNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    //region Main Checkout Screen
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
        ),
        enterTransition =  NavigationAnimations.enterTransition ,
        exitTransition =  NavigationAnimations.exitTransition ,
        popEnterTransition =  NavigationAnimations.popEnterTransition ,
        popExitTransition =  NavigationAnimations.popExitTransition
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
                navController.navigate("payment_webview?paymentUrl=${java.net.URLEncoder.encode(paymentUrl, "UTF-8")}")
            },
            checkoutType = if (checkoutType == "BUY_NOW") CheckoutType.BUY_NOW else CheckoutType.FROM_CART,
            buyNowItems = buyNowItems,
            navController = navController,
            onShowSnackbar = onShowSnackbar
        )
    }
    //endregion

    //region Shipping Selection Screen
    composable("shipping_selection") {
        ShippingSelectionScreen(
            onBackClick = { navController.navigateUp() },
            onShippingInfoSelected = { shippingInfo ->
                // Pass only the shipping info ID back to checkout screen
                navController.previousBackStackEntry?.savedStateHandle?.set("selected_shipping_info_id", shippingInfo.shippingInfoId)
                navController.navigateUp()
            },
            onCreateNewShippingInfo = {
                navController.navigate("add_shipping_address")
            }
        )
    }
    //endregion

    //region Add Shipping Address Screen
    composable("add_shipping_address") {
        AddShippingAddressScreen(
            onBackClick = { navController.navigateUp() },
            onShippingAddressAdded = {
                // After adding, pop back to selection and refresh
                navController.popBackStack("shipping_selection", inclusive = false)
            }
        )
    }
    //endregion

    //region Payment WebView Screen
    composable(
        route = "payment_webview?paymentUrl={paymentUrl}",
        arguments = listOf(
            navArgument("paymentUrl") {
                type = NavType.StringType
            }
        ),
        enterTransition = {
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    ) { backStackEntry ->
        val paymentUrl = backStackEntry.arguments?.getString("paymentUrl") ?: ""
        val decodedUrl = java.net.URLDecoder.decode(paymentUrl, "UTF-8")
        
        PaymentWebViewScreen(
            paymentUrl = decodedUrl,
            onBackClick = { navController.navigateUp() },
            onPaymentResult = { result ->
                when (result) {
                    is PaymentResult.Success -> {
                        navController.navigate("payment_result/success")
                    }
                    is PaymentResult.Failed -> {
                        navController.navigate("payment_result/failed?errorMessage=${java.net.URLEncoder.encode(result.errorMessage, "UTF-8")}")
                    }
                    is PaymentResult.Cancelled -> {
                        navController.navigate("payment_result/cancelled")
                    }
                }
            }
        )
    }
    //endregion

    //region Payment Success Screen
    composable("payment_result/success") {
        PaymentResultScreen(
            paymentResult = PaymentResult.Success,
            onNavigateToHome = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            },
            onNavigateToOrders = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.ORDER_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            }
        )
    }
    //endregion

    //region Payment Failed Screen
    composable(
        route = "payment_result/failed?errorMessage={errorMessage}",
        arguments = listOf(
            navArgument("errorMessage") { 
                type = NavType.StringType
                defaultValue = "Payment failed"
            }
        ),

    ) { backStackEntry ->
        val errorMessage = backStackEntry.arguments?.getString("errorMessage") ?: "Payment failed"
        val decodedMessage = java.net.URLDecoder.decode(errorMessage, "UTF-8")
        
        PaymentResultScreen(
            paymentResult = PaymentResult.Failed(decodedMessage),
            onNavigateToHome = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            },
            onNavigateToOrders = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.ORDER_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            },
            onRetryPayment = {
                // Navigate back to checkout by popping the payment result screen
                navController.navigateUp()
            }
        )
    }
    //endregion

    //region Payment Cancelled Screen
    composable("payment_result/cancelled") {
        PaymentResultScreen(
            paymentResult = PaymentResult.Cancelled,
            onNavigateToHome = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            },
            onNavigateToOrders = {
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.ORDER_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { inclusive = false }
                }
            },
            onRetryPayment = {
                // Navigate back to checkout by popping the payment result screen
                navController.navigateUp()
            }
        )
    }
    //endregion
} 