import io.gnocchi.BuildConfiguration

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

group = "com.github.rosendojonas" // JitPack Standard

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
                artifactId = "gnocchi-core"

                from(components["release"])

                pom {
                    name.set("gnocchi-core")
                    description.set("Core utilities for Gnocchi Android")
                    url.set("https://github.com/rosendojonas/gnocchi/tree/main/gnocchi-core")
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
                        url.set("https://github.com/rosendojonas/gnocchi/tree/main/gnocchi-core")
                        connection.set("scm:git:https://github.com/rosendojonas/gnocchi/tree/main/gnocchi-core.git")
                        developerConnection.set("scm:git:ssh://git@github.com/rosendojonas/gnocchi/tree/main/gnocchi-core.git")
                    }
                }
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
