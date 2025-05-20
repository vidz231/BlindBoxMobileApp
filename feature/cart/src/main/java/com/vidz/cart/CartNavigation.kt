package com.vidz.cart

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addCartNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.CART_SCREEN_ROUTE) {
        CartScreen(navController)
    }

    
}