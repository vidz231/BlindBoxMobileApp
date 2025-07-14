package com.vidz.message

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations

fun NavGraphBuilder.addMessageNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    composable(
        DestinationRoutes.MESSAGE_SCREEN_ROUTE,
        enterTransition =  NavigationAnimations.enterTransition ,
        exitTransition =  NavigationAnimations.exitTransition ,
        popEnterTransition =  NavigationAnimations.popEnterTransition ,
        popExitTransition =  NavigationAnimations.popExitTransition
    ) {
        MessageScreenRoot(navController)
    }
} 