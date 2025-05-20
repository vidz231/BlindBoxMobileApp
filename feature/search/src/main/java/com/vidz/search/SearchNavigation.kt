package com.vidz.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addSearchNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.SEARCH_SCREEN_ROUTE) {
        SearchScreen(navController)
    }

    
}