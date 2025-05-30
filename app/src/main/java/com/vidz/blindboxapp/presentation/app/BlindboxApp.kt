package com.vidz.blindboxapp.presentation.app

import android.R.attr.onClick
import android.R.id.message
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE
import com.vidz.blindboxapp.BuildConfig
import com.vidz.blindboxapp.R
import com.vidz.blindboxapp.presentation.navigation.AppNavHost
import com.vidz.blindboxapp.presentation.navigation.BlindboxNavController
import com.vidz.blindboxapp.presentation.navigation.rememberBlindboxNavController
import com.vidz.theme.BlindBoxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
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

    if (navController.navController.currentDestination?.route == HOME_SCREEN_ROUTE) {
        DoubleBackToExitApp(
            streamUiState = uiState
        )
    }

    if (BuildConfig.DEBUG) {
        val backStack by navController.navController.currentBackStackEntryAsState()
        val backStackList = remember { mutableStateListOf<String>() }
        LaunchedEffect(backStack) {
            backStackList.clear()
            navController.navController.currentBackStack.value.forEach { entry ->
                backStackList.add(entry.destination.route ?: "Unknown")
            }
        }
        Log.d(
            "BlindBoxApp",
            "AppTheme:  | CurrentDestination: ${
                backStack?.destination?.route
            } | BackStack: ${
                backStackList.joinToString(" -> ")
            }"
        )
        Log.d(
            "BlindBoxApp",
            "BackStackTree:"
        )
        backStackList.forEachIndexed { index, route ->
            val indentation = "  ".repeat(index) // Create indentation for hierarchy
            Log.d(
                "BlindBoxApp",
                "$indentation├─ $route"
            )
        }
    }



    BlindBoxTheme(darkTheme = false) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            bottomBar = {
                if (uiState.shouldShowBottomBar) {
                    BlindboxBottomAppBar(
                        navController = navController,
                        currentRoute = navController.navController.currentDestination?.route
                    )
                }
            }
        ) { paddingvalue ->
            AppNavHost(
                navController = navController.navController,
                onShowSnackbar = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
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
        HOME_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Search",
        Icons.Filled.Search,
        DestinationRoutes.SEARCH_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Cart",
        Icons.Filled.ShoppingCart,
        DestinationRoutes.CART_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Order",
        Icons.AutoMirrored.Filled.List,
        DestinationRoutes.ORDER_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Setting",
        Icons.Filled.Settings,
        DestinationRoutes.SETTING_SCREEN_ROUTE
    ),
)

@Composable
fun BlindboxBottomAppBar(
    navController: BlindboxNavController,
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
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigateToNavigationBar(
                        item.route,
                        numberItemOfPage = "10"
                    )

                }
            )
        }
    }
}

@Composable
fun DoubleBackToExitApp(streamUiState: BlindboxAppViewModel.BlindBoxViewState) {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val message = stringResource(R.string.press_back_2_times_to_exit_app)

    val isEnable = streamUiState.currentNavIndex == 0

    BackHandler(isEnable) {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
}

