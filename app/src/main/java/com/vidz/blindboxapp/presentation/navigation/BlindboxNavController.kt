package com.vidz.blindboxapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE


@Composable
fun rememberBlindboxNavController(
    navController: NavHostController = rememberNavController()
): BlindboxNavController = remember(navController) {
    BlindboxNavController(navController)
}


@Stable
class BlindboxNavController(val navController: NavHostController) {

    private val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToNavigationBar(
        route: String,
        numberItemOfPage: String= "10"
    ) {
        when (route) {
            HOME_SCREEN_ROUTE -> {
                navController.navigate("$route?numberItemOfPage=$numberItemOfPage") {
                    popUpTo(HOME_SCREEN_ROUTE) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            DestinationRoutes.SEARCH_SCREEN_ROUTE -> {
                navController.navigate("$route?numberItemOfPage=$numberItemOfPage") {
                    popUpTo(HOME_SCREEN_ROUTE) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            DestinationRoutes.CART_SCREEN_ROUTE -> {
                navController.navigate("$route?numberItemOfPage=$numberItemOfPage") {
                    popUpTo(HOME_SCREEN_ROUTE) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            DestinationRoutes.ORDER_SCREEN_ROUTE -> {
                navController.navigate("$route?numberItemOfPage=$numberItemOfPage") {
                    popUpTo(HOME_SCREEN_ROUTE) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            DestinationRoutes.SETTING_SCREEN_ROUTE -> {
                navController.navigate("$route?numberItemOfPage=$numberItemOfPage") {
                    popUpTo(HOME_SCREEN_ROUTE) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            else -> {
                if (route != currentRoute) {
                    navController.navigate(route) {
                        popUpTo(HOME_SCREEN_ROUTE) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }

    fun navigateToLogin(from: NavBackStackEntry) {
        if (shouldNavigate(from)) {
            navController.navigate(DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE)
        }
    }
}

private fun shouldNavigate(from: NavBackStackEntry): Boolean = from.isLifecycleResumed()

private fun NavBackStackEntry.isLifecycleResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
