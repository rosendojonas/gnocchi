import io.gnocchi.BuildConfiguration

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

group = providers.gradleProperty("group").getOrElse("com.github.rosendojonas.gnocchi") // JitPack Standard
version = providers.gradleProperty("version").getOrElse("0.0.0")

android {
    namespace = BuildConfiguration.gnocchiAndroidViewNamespace
    compileSdk = BuildConfiguration.compileSdk

    defaultConfig {
        minSdk = BuildConfiguration.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
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
                artifactId = "gnocchi-android-view"

                from(components["release"])

                pom {
                    name.set("gnocchi-android-view")
                    description.set("Compose utilities for Gnocchi Android")
                    url.set("https://github.com/rosendojonas/gnocchi")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("rosendojonas")
                            name.set("Jonas Rosendo")
                        }
                    }
                    scm {
                        url.set("https://github.com/rosendojonas/gnocchi")
                        connection.set("scm:git:https://github.com/rosendojonas/gnocchi.git")
                        developerConnection.set("scm:git:ssh://git@github.com/rosendojonas/gnocchi.git")
                    }
                }
            }
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    api(project(":gnocchi-core"))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}