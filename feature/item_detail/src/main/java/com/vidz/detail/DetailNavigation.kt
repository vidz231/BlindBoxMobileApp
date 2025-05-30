package com.vidz.detail

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vidz.base.navigation.DestinationRoutes
import java.net.URLDecoder

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.addDetailNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope
) {
    composable(
        DestinationRoutes.ITEM_DETAIL_SCREEN_ROUTE,
        arguments = listOf(
            navArgument("blindBoxId") { type = NavType.LongType },
            navArgument("imageUrl") { type = NavType.StringType },
            navArgument("title") { type = NavType.StringType }
        ),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) { backStackEntry ->
        val blindBoxId = backStackEntry.arguments?.getLong("blindBoxId") ?: 0L
        val encodedImageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
        val encodedTitle = backStackEntry.arguments?.getString("title") ?: ""
        
        val imageUrl = java.net.URLDecoder.decode(encodedImageUrl, "UTF-8")
        val title = java.net.URLDecoder.decode(encodedTitle, "UTF-8")
        
        DetailScreen(
            navController = navController,
            blindBoxId = blindBoxId,
            initialImageUrl = imageUrl,
            initialTitle = title,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this,
            onShowSnackbar = onShowSnackbar
        )
    }
}