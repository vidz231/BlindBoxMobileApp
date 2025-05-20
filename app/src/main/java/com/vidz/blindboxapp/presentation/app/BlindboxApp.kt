package com.vidz.blindboxapp.presentation.app

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.blindboxapp.presentation.navigation.AppNavHost
import com.vidz.blindboxapp.presentation.navigation.rememberBlindboxNavController
import com.vidz.theme.BlindBoxAppTheme

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun BlindBoxApp() {
    val navController = rememberBlindboxNavController().navController

    BlindBoxAppTheme {
        Scaffold(
            bottomBar = {
                BlindboxBottomAppBar(
                    navController = navController,
                    currentRoute =  navController.currentDestination?.route
                )

            }
        ) {  paddingvalue->
            AppNavHost(navController)
        }
    }
}


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, DestinationRoutes.HOME_SCREEN_ROUTE),
    BottomNavItem("Cart", Icons.Filled.ShoppingCart, DestinationRoutes.CART_SCREEN_ROUTE),
    BottomNavItem("Search", Icons.Filled.Search, DestinationRoutes.SEARCH_SCREEN_ROUTE),
    BottomNavItem("Setting", Icons.Filled.Settings, DestinationRoutes.SETTING_SCREEN_ROUTE),
    BottomNavItem("Order", Icons.Filled.List, DestinationRoutes.ORDER_SCREEN_ROUTE)
)

@Composable
fun BlindboxBottomAppBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}