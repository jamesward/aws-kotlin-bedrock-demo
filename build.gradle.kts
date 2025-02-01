import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    kotlin("plugin.power-assert") version "2.1.10"
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
                implementation("aws.sdk.kotlin:s3:1.4.10")
                implementation("aws.sdk.kotlin:bedrock:1.4.10")
                implementation("aws.sdk.kotlin:bedrockruntime:1.4.10")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
                runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
            }
        }
    }
}
