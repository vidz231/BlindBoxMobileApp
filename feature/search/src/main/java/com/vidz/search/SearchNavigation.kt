package com.vidz.search

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.addSearchNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    composable(DestinationRoutes.SEARCH_SCREEN_ROUTE,
               enterTransition = { EnterTransition.None },
               exitTransition = { ExitTransition.None },
               popEnterTransition = { EnterTransition.None },
               popExitTransition = { ExitTransition.None }) {
        SearchScreen(navController, sharedTransitionScope, this@composable)
    }
}