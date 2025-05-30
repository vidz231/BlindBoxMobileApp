package com.vidz.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.auth.login.LoginScreenRoot
import com.vidz.auth.register.RegisterScreenRoot
import com.vidz.auth.forgot_password.ForgotPasswordScreenRoot
import com.vidz.base.navigation.DestinationRoutes

fun NavGraphBuilder.addAuthNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE,
        startDestination = DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE + "/login"
    ) {
        composable(DestinationRoutes.ROOT_LOGIN_SCREEN_ROUTE + "/login") {
            LoginScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                onNavigateToRegister = {
                    navController.navigate(DestinationRoutes.REGISTER_SCREEN_ROUTE)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(DestinationRoutes.FORGOT_PASSWORD_SCREEN_ROUTE)
                }
            )
        }
        
        composable(DestinationRoutes.REGISTER_SCREEN_ROUTE) {
            RegisterScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                onNavigateToLogin = {
                    println("DEBUG: onNavigateToLogin called, calling popBackStack")
                    navController.popBackStack()
                    println("DEBUG: popBackStack completed")
                }
            )
        }
        
        composable(DestinationRoutes.FORGOT_PASSWORD_SCREEN_ROUTE) {
            ForgotPasswordScreenRoot(
                navController = navController,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}