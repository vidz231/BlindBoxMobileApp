package com.vidz.base.components

import android.Manifest
import androidx.compose.runtime.Composable
import com.vidz.base.components.PermissionManager
import com.vidz.base.components.PermissionStatus

/**
 * A simple reusable permission handler dedicated for accessing the device location.
 *
 * @param onGranted Called once the location permission is granted.
 * @param onDenied  Called if the user denies or permanently denies the permission.
 */
@Composable
fun LocationPermissionHandler(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
) {
    PermissionManager(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { permissionState ->
            when (permissionState.status) {
                PermissionStatus.GRANTED -> onGranted()
                PermissionStatus.DENIED, PermissionStatus.PERMANENTLY_DENIED -> onDenied()
                PermissionStatus.NOT_REQUESTED -> {}
            }
        }
    )
} 