package com.vidz.blindboxapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.home.addHomeGraph
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(
     navController: NavHostController = rememberBlindboxNavController().navController,
) {

    val navController = rememberBlindboxNavController().navController

    NavHost(
        navController = navController,
        startDestination = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
    ) {
        navigation (
            route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
                    startDestination = DestinationRoutes.HOME_SCREEN_ROUTE
        ){
            addHomeGraph(navController)
        }
    }


}

@Serializable
data class  HomeNavigation(val id: String="");