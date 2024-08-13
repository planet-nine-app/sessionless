plugins {
    kotlin("jvm") version "2.0.0"
    id("java-library")
    id("maven-publish")
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


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["kotlin"]) // Use 'kotlin' if it's a Kotlin project

            artifactId = "sessionless-kt" // Set the artifact ID

            pom {
                name.set("SLAP (Sessionless Authentication Protocol)") // Set a descriptive name
                description.set("Native Kotlin library for Sessionless Authentication Protocol (SLAP) which works for client-server protocols, as well as for auth between local applications") // Add a description
                url.set("https://github.com/planetnineapps/sessionless") // Provide a project URL (optional)

                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://github.com/planet-nine-app/sessionless/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("galacticai")
                        name.set("Galacticai")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/planet-nine-app/sessionless.git")
                    developerConnection.set("scm:git:ssh://github.com/planet-nine-app/sessionless.git")
                    url.set("https://github.com/planet-nine-app/sessionless")
                }
            }
        }
    }

    repositories {
        maven {
            name = "mavencentral"
            url = uri("https://my-maven-repo.com/releases") // Replace with your repository URL
            credentials {
                username = env.MAVEN_USERNAME
                password = env.MAVEN_PASSWORD
            }
        }
    }
}