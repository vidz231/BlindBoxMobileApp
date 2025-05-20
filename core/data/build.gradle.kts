
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
}

dependencies {

}