plugins {
    alias(libs.plugins.blindbox.android.feature)
    alias(libs.plugins.blindbox.android.library.compose)
    alias(libs.plugins.blindbox.android.library.jacoco)
}

android {
    namespace = "com.vidz.blindbox.feature.message"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.theme)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.kotlinx.serialization.json)
} 