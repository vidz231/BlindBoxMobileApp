package com.vidz.order

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations
import com.vidz.order_detail.OrderDetailScreen

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

    composable(
        route = "${DestinationRoutes.ORDER_DETAIL_SCREEN_ROUTE}/{orderId}",
        arguments = listOf(
            navArgument("orderId") {
                type = NavType.StringType
            }
        ),
        enterTransition =  NavigationAnimations.enterTransition ,
        exitTransition =  NavigationAnimations.exitTransition ,
        popEnterTransition =  NavigationAnimations.popEnterTransition ,
        popExitTransition =  NavigationAnimations.popExitTransition
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        OrderDetailScreen(
            navController = navController,
            orderId = orderId
        )
    }
}