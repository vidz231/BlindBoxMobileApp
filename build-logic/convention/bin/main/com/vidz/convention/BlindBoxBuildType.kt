package com.vidz.convention
/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class BlindBoxBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
