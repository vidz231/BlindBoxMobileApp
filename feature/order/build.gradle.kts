
plugins {
    alias(libs.plugins.blindbox.android.feature)
    alias(libs.plugins.blindbox.android.library.compose)
    alias(libs.plugins.blindbox.android.library.jacoco)
}

android {
    namespace = "com.vidz.blindbox.feature.order"
}

dependencies {
       implementation(projects.common.base)
    implementation(projects.common.theme)
}