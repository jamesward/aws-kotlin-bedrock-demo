import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.0.0-RC2"
    kotlin("plugin.serialization") version "2.0.0-RC2"
}

kotlin {
    jvmToolchain(21)

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "MainKt"
        }
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation("aws.sdk.kotlin:bedrockruntime:1.2.8")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
            }
        }
    }
}
