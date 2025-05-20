package com.vidz.blindboxapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.cart.addCartNavGraph
import com.vidz.home.addHomeNavGraph
import com.vidz.order.addOrderNavGraph
import com.vidz.search.addSearchNavGraph
import com.vidz.setting.addSettingNavGraph

@Composable
fun AppNavHost(
    navController: NavHostController ,
) {
    NavHost(
        navController = navController,
        startDestination = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
    ) {
        addHomeGraph(navController)
    }
}

fun NavGraphBuilder.addHomeGraph(
    navController: NavController,
) {
    navigation(
        route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
        startDestination = DestinationRoutes.HOME_SCREEN_ROUTE
    ) {
        addHomeNavGraph(navController)
        addSearchNavGraph(navController)
        addCartNavGraph(navController)
        addSettingNavGraph(navController)
        addOrderNavGraph(navController)
    }
}