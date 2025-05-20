package com.vidz.blindboxapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(
     navController: NavHostController,
) {


    NavHost(
        navController = navController,
        startDestination = HomeNavigation(),
        route = AppNavRoute.Root.route
    ) {

    }

}

@Serializable
data class  HomeNavigation(val id: String="");