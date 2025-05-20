pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Blind Box App"
include(":app")
include(":feature")
include(":feature:home")
include(":feature:search")
include(":feature:item_detail")
include(":feature:cart")
include(":feature:order")
include(":feature:order_detail")
include(":core:domain")
include(":common:base")
include(":common:theme")
include(":core:data")
include(":feature:setting")
include(":feature:checkout")
