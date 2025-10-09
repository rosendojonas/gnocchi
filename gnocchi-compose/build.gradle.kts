import io.gnocchi.BuildConfiguration

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

group = providers.gradleProperty("group").getOrElse(BuildConfiguration.groupId)
version = providers.gradleProperty("version").getOrElse("0.0.0")

android {
    namespace = BuildConfiguration.gnocchiComposeNamespace
    compileSdk = BuildConfiguration.compileSdk

    defaultConfig {
        minSdk = BuildConfiguration.minSdk

        testInstrumentationRunner = BuildConfiguration.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant(BuildConfiguration.releaseVariant) {
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
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(BuildConfiguration.releaseVariant) {
                artifactId = BuildConfiguration.composeArtifactId

                from(components[BuildConfiguration.releaseVariant])

                pom {
                    name.set(BuildConfiguration.composeArtifactId)
                    description.set("Compose utilities for Gnocchi Android")
                    url.set("https://${BuildConfiguration.repo}")
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
                        url.set("https://${BuildConfiguration.repo}")
                        connection.set("scm:git:https://${BuildConfiguration.repo}.git")
                        developerConnection.set("scm:git:ssh://git@${BuildConfiguration.repo}.git")
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
}