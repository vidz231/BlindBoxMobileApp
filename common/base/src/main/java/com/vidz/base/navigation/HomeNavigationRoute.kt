package com.vidz.base.navigation

object DestinationRoutes {
    const val ROOT_HOME_SCREEN_ROUTE = "root_home_screen_route"
    const val ROOT_LOGIN_SCREEN_ROUTE = "root_login_screen_route"
    const val HOME_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/home"
    const val CART_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/cart"
    const val SEARCH_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/search"
    const val SETTING_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/setting"
    const val ORDER_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/order"
    const val MESSAGE_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/message"
    const val FORGOT_PASSWORD_SCREEN_ROUTE = "${ROOT_LOGIN_SCREEN_ROUTE}/forgot_password_screen"
    const val REGISTER_SCREEN_ROUTE = "${ROOT_LOGIN_SCREEN_ROUTE}/register_screen"
    const val ORDER_DETAIL_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/order_detail"
    const val CHECKOUT_SCREEN_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/checkout"
    const val ITEM_DETAIL_SCREEN_BASE_ROUTE = "${ROOT_HOME_SCREEN_ROUTE}/item_detail"
    const val ITEM_DETAIL_SCREEN_ROUTE = "$ITEM_DETAIL_SCREEN_BASE_ROUTE/{blindBoxId}/{imageUrl}/{title}"

    const val ROOT_EDIT_PROFILE_SCREEN_ROUTE = "${SETTING_SCREEN_ROUTE}/edit_profile"

    const val CHECKOUT_SUCCESS_SCREEN_ROUTE = "${CHECKOUT_SCREEN_ROUTE}/checkout_success"
    const val CHECKOUT_FAILURE_SCREEN_ROUTE = "${CHECKOUT_SCREEN_ROUTE}/checkout_failure"
}