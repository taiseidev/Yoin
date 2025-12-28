plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

// Note: Supabase credentials are initialized at runtime from SupabaseConfig
// See: app/src/commonMain/kotlin/com/yoin/app/App.kt

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Data"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Domain
            implementation(project(":domain"))

            // Core
            implementation(project(":core"))

            // Koin
            implementation(libs.koin.core)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DateTime
            implementation(libs.kotlinx.datetime)

            // Supabase
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.realtime)

            // Ktor Client (required by Supabase)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Firebase - TODO: Add in Phase 4
            // implementation(libs.firebase.common)
            // implementation(libs.firebase.auth)
            // implementation(libs.firebase.messaging)
            // implementation(libs.firebase.analytics)
            // implementation(libs.firebase.crashlytics)

            // RevenueCat - TODO: Add in Phase 8
            // implementation(libs.revenuecat.purchases)
        }

        androidMain.dependencies {
            // Ktor Android Engine
            implementation(libs.ktor.client.android)
        }

        iosMain.dependencies {
            // Ktor Darwin Engine (for iOS)
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.yoin.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
