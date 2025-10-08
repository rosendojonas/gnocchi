import io.gnocchi.BuildConfiguration

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

android {
    namespace = BuildConfiguration.gnocchiCoreNamespace
    compileSdk = BuildConfiguration.compileSdk

    defaultConfig {
        minSdk = BuildConfiguration.minSdk
        lint.targetSdk = BuildConfiguration.targetSdk

        testInstrumentationRunner = BuildConfiguration.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
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
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.rosendojonas" // JitPack Standard
                artifactId = "gnocchi-core" // artifact name
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    // ViewModel + Coroutines
    api(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
