plugins {
    alias(libs.plugins.blindbox.android.feature)
    alias(libs.plugins.blindbox.android.library.compose)
    alias(libs.plugins.blindbox.android.library.jacoco)
}

android {
    namespace = "com.vidz.blindbox.common.base"

    defaultConfig{
        buildConfigField("String", "GOONG_MAP_URL", "\"${property("GOONG_MAP_URL")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
}
dependencies {
    implementation(libs.play.services.maps)

// Annotation plugin
    implementation(libs.maps.annotation)

    implementation("com.mapbox.maps:android:11.13.1")
// Gestures plugin
    implementation(libs.maps.gestures)
    implementation("com.mapbox.extension:maps-compose:11.13.1")
//    implementation(libs.mapbox.android.geojson)
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:7.4.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:7.4.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:7.4.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-core:7.4.0")

//    implementation("androidx.annotation:annotation:1.0.0")
}

