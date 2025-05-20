package com.vidz.blindboxapp.presentation.app

import android.annotation.SuppressLint
import android.app.Activity
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.multidex.BuildConfig
import androidx.navigation.NavController

import androidx.navigation.compose.currentBackStackEntryAsState
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.blindboxapp.presentation.navigation.AppNavHost
import com.vidz.blindboxapp.presentation.navigation.rememberBlindboxNavController
import com.vidz.theme.BlindBoxAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Logger

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter",
              "RestrictedApi"
)
fun BlindBoxApp(
    blindboxAppViewModel: BlindboxAppViewModel = hiltViewModel()
    ) {
    val navController = rememberBlindboxNavController()
    val uiState = blindboxAppViewModel.uiState.collectAsStateWithLifecycle().value
    val currentBackStackEntry = navController.navController.currentBackStackEntryAsState()

    // Track both the route and the navController itself to ensure proper recomposition
    LaunchedEffect(currentBackStackEntry.value?.destination?.route) {
        val currentRoute = navController.navController.currentDestination?.route
        Log.d(
            "BlindBoxApp",
            "Current destination: $currentRoute"
        )
        blindboxAppViewModel.onTriggerEvent(
            BlindboxAppViewModel.BlindBoxViewEvent.ObserveNavDestination(navController.navController)
        )
    }

    // Track if back was already pressed once
    val backPressedState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    // Update backStackList whenever the back stack changes
    if (BuildConfig.DEBUG) {
        val backStack by navController.navController.currentBackStackEntryAsState()

        val backStackList = remember { mutableStateListOf<String>() }

        LaunchedEffect(backStack) {
            backStackList.clear()
            navController.navController.currentBackStack.value.forEach { entry ->
                backStackList.add(entry.destination.route ?: "Unknown")
            }
        }

        Log.d("BlindBoxApp", "AppTheme:  | CurrentDestination: ${
            backStack?.destination?.route
        } | BackStack: ${backStackList.joinToString(" -> ")
        }")
        Log.d("BlindBoxApp", "BackStackTree:")
        backStackList.forEachIndexed { index, route ->
            val indentation = "  ".repeat(index) // Create indentation for hierarchy
            Log.d("BlindBoxApp", "$indentation├─ $route")
        }
    }


    BackHandler {
        if (navController.navController.currentDestination?.route == DestinationRoutes.HOME_SCREEN_ROUTE) {
            if (backPressedState.value) {
                // User pressed back twice within timeout, exit app
                (context as? Activity)?.finish()
        } else {
                // First back press - show snackbar and start timer
                backPressedState.value = true
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Press back again to exit",
                        duration = SnackbarDuration.Short
                    )
                    // Reset after 2 seconds
                    delay(2000)
                    backPressedState.value = false
                }
            }
        } else {
            // Not on home screen, just go back
            navController.navController.popBackStack()
        }
    }
    BlindBoxAppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (uiState.shouldShowBottomBar) {
                    BlindboxBottomAppBar(
                        navController = navController.navController,
                        currentRoute = navController.navController.currentDestination?.route
                    )
                }
            }
        ) { paddingvalue ->
            AppNavHost(navController.navController)
        }
    }
}


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        "Home",
        Icons.Filled.Home,
        DestinationRoutes.HOME_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Cart",
        Icons.Filled.ShoppingCart,
        DestinationRoutes.CART_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Search",
        Icons.Filled.Search,
        DestinationRoutes.SEARCH_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Setting",
        Icons.Filled.Settings,
        DestinationRoutes.SETTING_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Order",
        Icons.Filled.List,
        DestinationRoutes.ORDER_SCREEN_ROUTE
    )
)

@Composable
fun BlindboxBottomAppBar(
    navController: NavController,
    currentRoute: String?,

    ) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label
                    )
                },
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