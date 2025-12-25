import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "YoinApp"
            isStatic = true

            // Export feature modules for iOS framework
            export(project(":feature:onboarding"))
            export(project(":feature:auth"))
            export(project(":feature:home"))
            export(project(":feature:room"))
            export(project(":feature:camera"))
            export(project(":feature:timeline"))
            export(project(":feature:map"))
            export(project(":feature:profile"))
            export(project(":feature:settings"))
            export(project(":feature:shop"))
            export(project(":feature:notifications"))

            // Export core modules that features depend on
            export(project(":core"))
            export(project(":core"))
            export(project(":core"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Feature Modules
            implementation(project(":feature:onboarding"))
            implementation(project(":feature:auth"))
            implementation(project(":feature:home"))
            implementation(project(":feature:room"))
            implementation(project(":feature:camera"))
            implementation(project(":feature:timeline"))
            implementation(project(":feature:map"))
            implementation(project(":feature:profile"))
            implementation(project(":feature:settings"))
            implementation(project(":feature:shop"))
            implementation(project(":feature:notifications"))

            // Data Modules
            implementation(project(":data"))
            implementation(project(":data"))
            implementation(project(":data"))
            implementation(project(":data"))
            implementation(project(":data"))

            // Core Modules
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))
            implementation(project(":core"))

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Voyager Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeViewModel)
        }

        androidMain.dependencies {
            // Android specific dependencies
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            // UI Tooling for Android Studio Preview
            implementation(compose.uiTooling)
        }

        iosMain.dependencies {
            // iOS specific dependencies
            // Export all feature modules for iOS framework
            api(project(":feature:onboarding"))
            api(project(":feature:auth"))
            api(project(":feature:home"))
            api(project(":feature:room"))
            api(project(":feature:camera"))
            api(project(":feature:timeline"))
            api(project(":feature:map"))
            api(project(":feature:profile"))
            api(project(":feature:settings"))
            api(project(":feature:shop"))
            api(project(":feature:notifications"))

            // Export core modules for iOS framework
            api(project(":core"))
            api(project(":core"))
            api(project(":core"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.yoin.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.yoin.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
