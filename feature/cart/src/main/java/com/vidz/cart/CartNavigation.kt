package com.vidz.cart

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations

fun NavGraphBuilder.addCartNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    composable(DestinationRoutes.CART_SCREEN_ROUTE,
               enterTransition =  NavigationAnimations.enterTransition ,
               exitTransition =  NavigationAnimations.exitTransition ,
               popEnterTransition =  NavigationAnimations.popEnterTransition ,
               popExitTransition =  NavigationAnimations.popExitTransition ) {
        CartScreen(
            navController = navController,
            onBackClick = { navController.navigateUp() }
        )
    }
}