plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.78"
    id("idea")
}

version = "1.0.0"
group = "com.teamresourceful.resourcefulbees"

base {
    archivesName.set("resourceful-bees")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

neoForge {
    version = "21.1.141"

    parchment {
        mappingsVersion = "2024.11.17"
        minecraftVersion = "1.21.1"
    }

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", "resourcefulbees")
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "resourcefulbees")
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "resourcefulbees")
        }

        register("data") {
            data()
            programArguments.addAll(
                "--mod", "resourcefulbees",
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create("resourcefulbees") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}


dependencies {
    compileOnlyApi("com.teamresourceful.resourcefullib:resourcefullib-neoforge-1.21:3.0.12")
}