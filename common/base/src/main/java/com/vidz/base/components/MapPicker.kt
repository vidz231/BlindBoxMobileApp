package com.vidz.base.components

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch
import java.util.Locale
//import com.vidz.blindbox.core.data.BuildConfig
//
//private val GOONG_API_KEY = BuildConfig.GOONG_API_KEY
//private val GOONG_STYLE_URL = "https://tiles.goong.io/assets/goong_map_web.json?api_key=${BuildConfig.GOONG_API_KEY}"

/**
 * A reusable composable that shows a Goong map and lets user pick a single point.
 *
 * @param modifier Compose modifier
 * @param onPointSelected callback invoked when user taps on the map with selected [Point]
 * @param onUserLocationUpdated callback invoked when user location is updated
 */
@Composable
fun MapComposePicker(
    modifier: Modifier = Modifier,
    onPointSelected: (Point) -> Unit,
    onUserLocationUpdated: (Point) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    PermissionManager(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { permissionState ->
            hasLocationPermission = permissionState.status == PermissionStatus.GRANTED
        },
        onRequestPermission = { requestPermission ->
            if (!permissionRequested) {
                permissionRequested = true
                requestPermission()
            }
        }
    )

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context)
        },
        update = { mapView ->
            val mapboxMap = mapView.mapboxMap
            lifecycleOwner.lifecycleScope.launch {
                try {
//                    val style = mapboxMap.loadStyle(GOONG_STYLE_URL)

                    val start = Point.fromLngLat(105.85242472181584, 21.029579719995272)
                    val end = Point.fromLngLat(106.2832288, 20.9409074)

                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(start)
                            .zoom(15.0)
                            .build()
                    )

                    val annotationApi = mapView.annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                    pointAnnotationManager.create(PointAnnotationOptions().withPoint(start))
                    pointAnnotationManager.create(PointAnnotationOptions().withPoint(end))

                    // ✅ Enable location after style is ready
                    setupMap(mapView, hasLocationPermission, onPointSelected, onUserLocationUpdated)

                } catch (e: Exception) {
                    Log.e("MapLoadError", "Error loading style: ${e.message}", e)
                }
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        onDispose {
            // MapView will handle its own lifecycle
        }
    }
}

private fun setupMap(
    mapView: MapView,
    hasLocationPermission: Boolean,
    onPointSelected: (Point) -> Unit,
    onUserLocationUpdated: (Point) -> Unit
) {
    // Handle location permission
    if (hasLocationPermission) {
        mapView.location.updateSettings {
            locationPuck = createDefault2DPuck(withBearing = true)
            enabled = true
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
        }
        
        // Add location listener
        lateinit var indicatorListener: OnIndicatorPositionChangedListener
        indicatorListener = OnIndicatorPositionChangedListener { point ->
            onUserLocationUpdated(point)
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(15.0)
                    .build()
            )
            mapView.location.removeOnIndicatorPositionChangedListener(indicatorListener) // ✅ Correct
        }
        mapView.location.addOnIndicatorPositionChangedListener(indicatorListener)

    } else {
        mapView.location.updateSettings { enabled = false }
    }

    // Add map click listener
    mapView.mapboxMap.addOnMapClickListener { point ->
        onPointSelected(point)
        true
    }
}

fun reverseGeocode(context: Context, point: Point): Address? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = geocoder.getFromLocation(point.latitude(), point.longitude(), 1)
        results?.firstOrNull()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
