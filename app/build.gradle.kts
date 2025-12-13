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

            // Export all feature modules
            export(project(":feature:onboarding"))
            export(project(":feature:auth"))
            export(project(":feature:home"))
            export(project(":feature:room"))
            export(project(":feature:camera"))
            export(project(":feature:timeline"))
            export(project(":feature:map"))
            export(project(":feature:profile"))
            export(project(":feature:settings"))
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

            // Data Modules
            implementation(project(":data:auth"))
            implementation(project(":data:room"))
            implementation(project(":data:photo"))
            implementation(project(":data:user"))
            implementation(project(":data:local"))

            // Core Modules
            implementation(project(":core:ui"))
            implementation(project(":core:design"))
            implementation(project(":core:common"))
            implementation(project(":core:network"))
            implementation(project(":core:database"))
            implementation(project(":core:analytics"))
            implementation(project(":core:notification"))
            implementation(project(":core:camera"))
            implementation(project(":core:image"))

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Navigation
            implementation(libs.androidx.navigation.compose)

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
