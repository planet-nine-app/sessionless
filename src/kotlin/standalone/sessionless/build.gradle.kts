plugins {
    kotlin("jvm") version "2.0.0"
    `java-library`
    `maven-publish`
}

group = "com.planetnineapp.sessionless-kt"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("org.json:json:20240303")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        ))
    }
}
