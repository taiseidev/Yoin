rootProject.name = "YoinApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// App module
include(":app")

// Core modules
include(":core:common")
include(":core:design")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:analytics")
include(":core:notification")
include(":core:camera")
include(":core:image")

// Domain modules
include(":domain:common")
include(":domain:auth")
include(":domain:room")
include(":domain:photo")
include(":domain:user")

// Data modules
include(":data:local")
include(":data:auth")
include(":data:room")
include(":data:photo")
include(":data:user")

// Feature modules
include(":feature:onboarding")
include(":feature:auth")
include(":feature:home")
include(":feature:room")
include(":feature:camera")
include(":feature:timeline")
include(":feature:map")
include(":feature:profile")
include(":feature:settings")

// Legacy modules (to be removed after migration)
include(":composeApp")
include(":shared")