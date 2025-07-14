package com.vidz.setting

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations

fun NavGraphBuilder.addSettingNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    composable(
        DestinationRoutes.SETTING_SCREEN_ROUTE,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }) {
        SettingScreen(navController)
    }

    composable(
        DestinationRoutes.ROOT_EDIT_PROFILE_SCREEN_ROUTE,
        enterTransition =  NavigationAnimations.enterTransition ,
        exitTransition =  NavigationAnimations.exitTransition ,
        popEnterTransition =  NavigationAnimations.popEnterTransition ,
        popExitTransition =  NavigationAnimations.popExitTransition
    ) {
        EditProfileScreen(navController = navController)
    }

    composable(
        DestinationRoutes.APP_SETTINGS_SCREEN_ROUTE,
        enterTransition =  NavigationAnimations.enterTransition ,
        exitTransition =  NavigationAnimations.exitTransition ,
        popEnterTransition =  NavigationAnimations.popEnterTransition ,
        popExitTransition =  NavigationAnimations.popExitTransition
    ) {
        AppSettingsScreen(navController = navController)
    }
}