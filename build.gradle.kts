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
        testRuns.configureEach {
            executionTask.configure {
                useJUnitPlatform()
            }
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
        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
                implementation("org.testcontainers:junit-jupiter:1.20.4")
                implementation("org.testcontainers:testcontainers:1.20.4")
                implementation("org.testcontainers:localstack:1.20.4")
            }
        }
    }
}

tasks.withType<AbstractTestTask> {
    testLogging {
        showStandardStreams = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
        )
    }
}
