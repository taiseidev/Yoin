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

// Core module (unified)
include(":core")

// Domain module (unified)
include(":domain")

// Data module (unified)
include(":data")

// Feature modules (kept separate for independence)
include(":feature:onboarding")
include(":feature:auth")
include(":feature:home")
include(":feature:room")
include(":feature:camera")
include(":feature:timeline")
include(":feature:map")
include(":feature:profile")
include(":feature:settings")
include(":feature:shop")
include(":feature:notifications")

