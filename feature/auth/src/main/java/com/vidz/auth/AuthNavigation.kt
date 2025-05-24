package com.vidz.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addAuthNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.CART_SCREEN_ROUTE) {
        AuthScreenRoot(navController)
    }
}