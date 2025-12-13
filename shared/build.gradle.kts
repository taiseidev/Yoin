import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Koin - Dependency Injection
            implementation(libs.koin.core)

            // Ktor Client - HTTP Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx Serialization - JSON Serialization
            implementation(libs.kotlinx.serialization.json)

            // SQLDelight - Local Database
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutinesExtensions)

            // Kotlinx DateTime - Date/Time handling
            implementation(libs.kotlinx.datetime)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // Ktor Client - Android Engine
            implementation(libs.ktor.client.android)

            // SQLDelight - Android Driver
            implementation(libs.sqldelight.androidDriver)

            // Koin Android
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            // Ktor Client - iOS Engine
            implementation(libs.ktor.client.darwin)

            // SQLDelight - Native Driver
            implementation(libs.sqldelight.nativeDriver)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.yoin.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("YoinDatabase") {
            packageName.set("com.yoin.database")
        }
    }
}
