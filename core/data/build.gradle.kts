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
                "\"http://52.163.66.235:8080/api/v1/\""
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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation (libs.moshi.kotlin)
    ksp (libs.moshi.kotlin.codegen)



}
