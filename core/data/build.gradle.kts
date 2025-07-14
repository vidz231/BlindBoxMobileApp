import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.blindbox.android.library)
    alias(libs.plugins.blindbox.android.library.jacoco)
    alias(libs.plugins.blindbox.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.vidz.blindbox.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }


    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://blindbox-w3cil4hv.southeastasia.cloudapp.azure.com/api/v1/\"",
                )
            buildConfigField(
                "String",
                "MAPBOX_ACCESS_TOKEN",
                "\"${project.findProperty("MAPBOX_ACCESS_TOKEN") ?: ""}\""
            )
            buildConfigField(
                "String",
                "GOONG_API_KEY",
                "\"${project.findProperty("GOONG_API_KEY") ?: ""}\""
            )
            buildConfigField(
                "String",
                "GOONG_API_URL",
                "\"${project.findProperty("GOONG_API_URL") ?: ""}\""
            )
            buildConfigField(
                "String",
                "GOONG_MAP_URL",
                "\"${project.findProperty("GOONG_MAP_URL") ?: ""}\""
            )
            buildConfigField(
                "String",
                "GOONG_MAP_KEY",
                "\"${project.findProperty("GOONG_MAP_KEY") ?: ""}\""
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.named("debug").get()
        }
    }
    buildFeatures {
        buildConfig = true
    }
}


dependencies {
//    implementation(projects.app)
    implementation(projects.core.domain)
    implementation(projects.core.datastore)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation (libs.moshi.kotlin)
    ksp (libs.moshi.kotlin.codegen)

    implementation(libs.play.services.maps)

// Annotation plugin
    implementation(libs.maps.annotation)

    implementation(libs.android)
// Gestures plugin
    implementation(libs.maps.compose)
//    implementation(libs.mapbox.android.geojson)
    implementation(libs.mapbox.sdk.geojson)
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:7.4.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:7.4.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-core:7.4.0")


}
