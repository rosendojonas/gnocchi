package io.gnocchi

import org.gradle.api.JavaVersion

object BuildConfiguration {
    const val compileSdk = 36
    const val targetSdk = 36
    const val minSdk = 21
    const val versionCode = 5
    const val versionName = "0.1.22"
    const val gnocchiCoreNamespace = "io.gnocchi.core"
    const val gnocchiComposeNamespace = "io.gnocchi_compose"
    const val gnocchiAndroidViewNamespace = "io.gnocchi_android_view"
    const val groupId = "io.gnocchi"
    const val coreArtifactId = "gnocchi-core"
    const val composeArtifactId = "gnocchi-compose"
    const val androidViewArtifactId = "gnocchi-android-view"

    const val namespace = "io.gnocchi"
    const val applicationId = "io.gnocchi"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    val javaVersion = JavaVersion.VERSION_17
}