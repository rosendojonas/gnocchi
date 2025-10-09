import io.gnocchi.BuildConfiguration

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = BuildConfiguration.namespace
    compileSdk = BuildConfiguration.compileSdk

    defaultConfig {
        applicationId = BuildConfiguration.applicationId
        minSdk = BuildConfiguration.minSdk
        targetSdk = BuildConfiguration.targetSdk
        versionCode = BuildConfiguration.versionCode
        versionName = BuildConfiguration.versionName

        testInstrumentationRunner = BuildConfiguration.testInstrumentationRunner
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = BuildConfiguration.javaVersion
        targetCompatibility = BuildConfiguration.javaVersion
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.gnocchi.core)
    implementation(libs.gnocchi.compose)
    implementation(libs.gnocchi.android.view)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
}