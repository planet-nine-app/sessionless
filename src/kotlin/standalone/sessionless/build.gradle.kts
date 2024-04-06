plugins {
    id("java-library")
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.json:json:20240303")
}