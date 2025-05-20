package com.vidz.blindboxapp.presentation.component.bottom_bar

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.vidz.base.navigation.DestinationRoutes

/**
 * Created by FPL on 17/02/2025.
 */

@SuppressLint("SupportAnnotationUsage")
data class BottomBarInfo(
    val id: String,
    val route: String? = null,
    val name: String? = null,
    val lightIconUrl: String = "",
    val lightSelectIconUrl: String = "",
    val darkIconUrl: String = "",
    val darkSelectIconUrl: String = "",
    val numberItemOfPage: String = "",
    @DrawableRes val defaultLightIcon: ImageVector? = null,
    @DrawableRes val defaultLightSelectIcon: ImageVector? = null,
    @DrawableRes val defaultDarkIcon: ImageVector? = null,
    @DrawableRes val defaultDarkSelectIcon: ImageVector? = null
) {

    companion object {
        val defaults = listOf(
            BottomBarInfo(
                id = "1",
                route = DestinationRoutes.HOME_SCREEN_ROUTE,
                name = "Trang chủ",
                defaultLightIcon = Icons.Filled.Home,
                defaultLightSelectIcon = Icons.Filled.Home,
                defaultDarkIcon = Icons.Filled.Home,
                defaultDarkSelectIcon = Icons.Filled.Home,
            ),
            BottomBarInfo(
                id = "2",
                route = DestinationRoutes.SEARCH_SCREEN_ROUTE,
                name = "Khám phá",
                defaultLightIcon = Icons.Filled.Search,
                defaultLightSelectIcon = Icons.Filled.Search,
                defaultDarkIcon = Icons.Filled.Search,
                defaultDarkSelectIcon = Icons.Filled.Search,
            ),
            BottomBarInfo(
                id = "3",
                route = DestinationRoutes.CART_SCREEN_ROUTE,
                name = "",
                defaultLightIcon = Icons.Filled.Add,
                defaultLightSelectIcon = Icons.Filled.Add,
                defaultDarkIcon = Icons.Filled.Add,
                defaultDarkSelectIcon = Icons.Filled.Add,
            ),
            BottomBarInfo(
                id = "4",
                route = DestinationRoutes.ORDER_SCREEN_ROUTE,
                name = "Thông báo",
                defaultLightIcon = Icons.Filled.Notifications,
                defaultLightSelectIcon = Icons.Filled.Notifications,
                defaultDarkIcon = Icons.Filled.Notifications,
                defaultDarkSelectIcon = Icons.Filled.Notifications,
            ),
            BottomBarInfo(
                id = "5",
                route = DestinationRoutes.SETTING_SCREEN_ROUTE,
                name = "Tài khoản",
                defaultLightIcon = Icons.Filled.Person,
                defaultLightSelectIcon = Icons.Filled.Person,
                defaultDarkIcon = Icons.Filled.Person,
                defaultDarkSelectIcon = Icons.Filled.Person,
            )
        )
    }
}