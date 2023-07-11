plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
}

group = "com.github.wireless4024"
version = "0.1.0"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    implementation(kotlin("reflect"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    testImplementation(kotlin("test"))
}

tasks.jar {
    exclude("testing/**")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.wireless4024"
            artifactId = "kfuzzy"

            from(components["java"])
        }
    }
}
