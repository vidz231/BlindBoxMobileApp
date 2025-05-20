package com.vidz.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addSettingNavGraph(
    navController: NavController,
) {
    composable(DestinationRoutes.SETTING_SCREEN_ROUTE) {
        SettingScreen(navController)
    }

    
}