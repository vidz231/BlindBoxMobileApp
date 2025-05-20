package com.vidz.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addHomeNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.HOME_SCREEN_ROUTE) {
        HomeScreen(navController)
    }


}