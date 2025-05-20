import org.gradle.kotlin.dsl.implementation
import com.vidz.convention.NiaBuildType

plugins {
    alias(libs.plugins.blindbox.android.application)
    alias(libs.plugins.blindbox.android.application.compose)
    alias(libs.plugins.blindbox.android.application.flavors)
    alias(libs.plugins.blindbox.android.application.jacoco)
    alias(libs.plugins.blindbox.hilt)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
}

android {

    defaultConfig {
        applicationId = "com.google.samples.apps.blindbox"
        versionCode = 8
        versionName = "0.1.2" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "com.google.samples.apps.blindbox.core.testing.NiaTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = NiaBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = NiaBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            // who clones the code to sign and run the release variant, use the debug signing key.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.vidz.blindboxapp"
}

dependencies {
    implementation(projects.feature.home)
    implementation( projects.feature.search)
    implementation(projects.feature.setting)
    implementation(projects.feature.cart)
    implementation(projects.feature.order)
    implementation(projects.common.base)
    implementation(projects.common.theme)
    implementation(projects.core.data)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
//
    ksp(libs.hilt.compiler)
//
    debugImplementation(libs.androidx.compose.ui.testManifest)
//    debugImplementation(projects.uiTestHiltManifest)
//
    kspTest(libs.hilt.compiler)
//
//    testImplementation(projects.core.dataTest)
//    testImplementation(projects.core.datastoreTest)
//    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.sync.syncTest)
//    testImplementation(libs.kotlin.test)
//
//    testDemoImplementation(libs.androidx.navigation.testing)
//    testDemoImplementation(libs.robolectric)
//    testDemoImplementation(libs.roborazzi)
//    testDemoImplementation(projects.core.screenshotTesting)
//    testDemoImplementation(projects.core.testing)
//
//    androidTestImplementation(projects.core.testing)
//    androidTestImplementation(projects.core.dataTest)
//    androidTestImplementation(projects.core.datastoreTest)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test)
//    androidTestImplementation(libs.hilt.android.testing)
//    androidTestImplementation(libs.kotlin.test)

//    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}