plugins {
    kotlin("jvm") version "1.6.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}
