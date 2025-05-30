package com.vidz.home

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addHomeNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    composable(DestinationRoutes.HOME_SCREEN_ROUTE,
               enterTransition = { EnterTransition.None },
               exitTransition = { ExitTransition.None },
               popEnterTransition = { EnterTransition.None },
               popExitTransition = { ExitTransition.None }
               ) {
        HomeScreen(navController)
    }
}