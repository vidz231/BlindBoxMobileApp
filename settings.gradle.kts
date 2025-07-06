pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google() // Essential for AndroidX

        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoiZHV5ZW45OTIyMyIsImEiOiJjbWNscnFvYWYwYng4Mm5zYmVvMDJ0YXFtIn0.KsVJAQvZd4HQ6OKcLVy80g"
            }
        }
    }
}

rootProject.name = "BlindBoxApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":feature")
include(":feature:home")
include(":feature:search")
include(":feature:item_detail")
include(":feature:cart")
include(":feature:order")
include(":feature:order_detail")
include(":feature:message")
include(":core:domain")
include(":common:base")
include(":common:theme")
include(":core:data")
include(":feature:setting")
include(":feature:checkout")
include(":core:datastore")
include(":feature:auth")
