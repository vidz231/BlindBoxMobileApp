
plugins {
    alias(libs.plugins.blindbox.android.library)
    alias(libs.plugins.blindbox.android.library.jacoco)
    alias(libs.plugins.blindbox.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.vidz.blindbox.core.domain"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

}