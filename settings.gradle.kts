pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.parchmentmc.org")
        maven("https://maven.neoforged.net/releases")
    }
}

rootProject.name = "ResourcefulBees"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}