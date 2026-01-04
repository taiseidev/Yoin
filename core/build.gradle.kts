plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Core"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutinesExtensions)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DateTime
            api(libs.kotlinx.datetime)

            // Koin
            implementation(libs.koin.core)

            // Coil - Image Loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }

        androidMain.dependencies {
            // Ktor Client - Android Engine
            implementation(libs.ktor.client.android)

            // SQLDelight - Android Driver
            implementation(libs.sqldelight.androidDriver)
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
    namespace = "com.yoin.core"
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
            packageName.set("com.yoin.core.database")
        }
    }
}
