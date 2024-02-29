buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.23.2")
    }
}

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines:0.19.2")
    testImplementation("org.jetbrains.kotlinx:lincheck:2.26")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}