package com.vidz.order

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addOrderNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.ORDER_SCREEN_ROUTE) {
        OrderScreen(navController)
    }

    
}