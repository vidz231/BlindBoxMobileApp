package com.vidz.blindboxapp.presentation.navigation

import android.view.animation.Animation
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.vidz.auth.addAuthNavGraph
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations
import com.vidz.base.navigation.NavigationAnimations.enterTransition
import com.vidz.base.navigation.NavigationAnimations.exitTransition
import com.vidz.base.navigation.NavigationAnimations.popEnterTransition
import com.vidz.base.navigation.NavigationAnimations.popExitTransition
import com.vidz.cart.addCartNavGraph
import com.vidz.checkout.addCheckoutNavGraph
import com.vidz.detail.addDetailNavGraph
import com.vidz.home.addHomeNavGraph
import com.vidz.message.addMessageNavGraph
import com.vidz.order.addOrderNavGraph
import com.vidz.search.addSearchNavGraph
import com.vidz.setting.addSettingNavGraph

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    onShowSnackbar: (String) -> Unit
) {
        NavHost(
            navController = navController,
            startDestination = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            addHomeGraph(navController, onShowSnackbar)

            addDetailNavGraph(navController, onShowSnackbar)
            addAuthNavGraph(navController, onShowSnackbar)
        }
    }
@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.addHomeGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
) {

    navigation(
        route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
        startDestination = DestinationRoutes.HOME_SCREEN_ROUTE,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        addHomeNavGraph(navController, onShowSnackbar)
        addSearchNavGraph(navController, onShowSnackbar)
        addCartNavGraph(navController, onShowSnackbar)
        addCheckoutNavGraph(navController, onShowSnackbar)
        addSettingNavGraph(navController, onShowSnackbar)
        addOrderNavGraph(navController, onShowSnackbar)
        addMessageNavGraph(navController, onShowSnackbar)
    }


}