plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
}

group = "com.github.wireless4024"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
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
