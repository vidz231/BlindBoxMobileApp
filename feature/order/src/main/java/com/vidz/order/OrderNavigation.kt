package com.vidz.order

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addOrderNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    composable(
        DestinationRoutes.ORDER_SCREEN_ROUTE,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }) {
        OrderScreen(navController)
    }
}