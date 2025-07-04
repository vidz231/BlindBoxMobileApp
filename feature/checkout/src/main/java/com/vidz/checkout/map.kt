//package com.vidz.checkout
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.Button
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import com.google.android.gms.maps.model.LatLng
//import com.mapbox.maps.MapView
//
//@Composable
//fun MapScreen(
//    mapView: MapView,
//    onSearchResult: (LatLng) -> Unit,
//    onDirectionRequest: () -> Unit
//) {
//    var searchText by remember { mutableStateOf("") }
//    var suggestions by remember { mutableStateOf(listOf<AutoComplete>()) }
//    val context = LocalContext.current
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        OutlinedTextField(
//            value = searchText,
//            onValueChange = {
//                searchText = it
//                if (it.isNotEmpty()) {
//                    searchAutoComplete(it, context) { result ->
//                        suggestions = result
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            label = { Text("Search location") }
//        )
//
//        LazyColumn(modifier = Modifier.height(150.dp)) {
//            items(suggestions) { item ->
//                Text(
//                    text = item.description ?: "",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            fetchPlaceDetail(item.place_id!!, context) { latLng ->
//                                onSearchResult(latLng)
//                            }
//                        }
//                        .padding(8.dp)
//                )
//            }
//        }
//
//        AndroidView(factory = { mapView }, modifier = Modifier.weight(1f))
//
//        Button(
//            onClick = onDirectionRequest,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text("Get Directions")
//        }
//    }
//}
//
//
//fun searchAutoComplete(
//    query: String,
//    context: Context,
//    callback: (List<AutoComplete>) -> Unit
//) {
//    val service = RetrofitInstance.getRetrofitInstance(context.getString(R.string.goong_api_url))
//        .create(IApiService::class.java)
//
//    service.getAutoComplete(query, context.getString(R.string.goong_api_key))
//        .enqueue(object : Callback<AutoCompleteResponse> {
//            override fun onResponse(call: Call<AutoCompleteResponse>, response: Response<AutoCompleteResponse>) {
//                callback(response.body()?.predictions ?: emptyList())
//            }
//
//            override fun onFailure(call: Call<AutoCompleteResponse>, t: Throwable) {
//                Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//}
//
//fun fetchPlaceDetail(
//    placeId: String,
//    context: Context,
//    callback: (LatLng) -> Unit
//) {
//    val service = RetrofitInstance.getRetrofitInstance(context.getString(R.string.goong_api_url))
//        .create(IApiService::class.java)
//
//    service.getPlaceDetail(placeId, context.getString(R.string.goong_api_key))
//        .enqueue(object : Callback<PlaceDetailResponse> {
//            override fun onResponse(call: Call<PlaceDetailResponse>, response: Response<PlaceDetailResponse>) {
//                val loc = response.body()?.result?.geometry?.location
//                loc?.let {
//                    val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())
//                    callback(latLng)
//                }
//            }
//
//            override fun onFailure(call: Call<PlaceDetailResponse>, t: Throwable) {
//                Toast.makeText(context, "Detail fetch failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//}
//
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity(), OnMapReadyCallback {
//
//    private lateinit var mapView: MapView
//    private lateinit var mapboxMap: MapboxMap
//    private var selectedLatLng by mutableStateOf<LatLng?>(null)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
//        mapView = MapView(this)
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)
//
//        setContent {
//            MaterialTheme {
//                MapScreen(
//                    mapView = mapView,
//                    onSearchResult = { latLng ->
//                        selectedLatLng = latLng
//                        mapboxMap.clear()
//                        mapboxMap.addMarker(MarkerOptions().position(latLng))
//                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))
//                    },
//                    onDirectionRequest = {
//                        selectedLatLng?.let { destination ->
//                            val origin = LatLng(21.029579719995272, 105.85242472181584)
//                            fetchDirections(origin, destination)
//                        }
//                    }
//                )
//            }
//        }
//    }
//
//    override fun onMapReady(mapboxMap: MapboxMap) {
//        val uri = "${getString(R.string.goong_map_url)}/assets/goong_map_web.json?api_key=${getString(R.string.goong_map_key)}"
//        mapboxMap.setStyle(Style.Builder().fromUri(uri)) {
//            this.mapboxMap = mapboxMap
//            val start = LatLng(21.029579719995272, 105.85242472181584)
//            mapboxMap.addMarker(MarkerOptions().position(start))
//            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 14.0))
//        }
//    }
//
//    private fun fetchDirections(start: LatLng, end: LatLng) {
//        val service = RetrofitInstance.getRetrofitInstance(getString(R.string.goong_api_url))
//            .create(IApiService::class.java)
//
//        val origin = "${start.latitude},${start.longitude}"
//        val destination = "${end.latitude},${end.longitude}"
//
//        service.getDirection(origin, destination, "car", getString(R.string.goong_api_key))
//            .enqueue(object : Callback<DirectionResponse> {
//                override fun onResponse(call: Call<DirectionResponse>, response: Response<DirectionResponse>) {
//                    if (response.isSuccessful) {
//                        response.body()?.routes?.firstOrNull()?.overviewPolyline?.points?.let { encoded ->
//                            val points = LineString.fromPolyline(encoded, 5).coordinates()
//                            drawRoute(points)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<DirectionResponse>, t: Throwable) {
//                    Toast.makeText(this@MainActivity, "Direction fetch failed", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun drawRoute(points: List<Point>) {
//        val lineString = LineString.fromLngLats(points)
//        val feature = Feature.fromGeometry(lineString)
//        val style = mapboxMap.style ?: return
//        val source = GeoJsonSource("line-source", feature)
//        style.addSource(source)
//        val layer = LineLayer("line-layer", "line-source").withProperties(
//            PropertyFactory.lineColor(Color.RED),
//            PropertyFactory.lineWidth(5f)
//        )
//        style.addLayer(layer)
//    }
//
//    override fun onStart() { super.onStart(); mapView.onStart() }
//    override fun onResume() { super.onResume(); mapView.onResume() }
//    override fun onPause() { super.onPause(); mapView.onPause() }
//    override fun onStop() { super.onStop(); mapView.onStop() }
//    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
//    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
//}
