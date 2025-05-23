import com.vidz.convention.BlindBoxBuildType
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
                "\"http://40.87.80.54:8080/\""
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
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.moshi)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation (libs.moshi.kotlin)



}