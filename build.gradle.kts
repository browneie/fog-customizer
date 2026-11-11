plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
    id("ploceus") version "1.17.4"
}

// OneClient runs 1.8.9 on Ornithe (calamus gen2 intermediary + Feather mappings), not Legacy Fabric.
ploceus {
    setIntermediaryGeneration(2)
}

// OneConfig brings LWJGL 3 through Compose; 1.8.9 ships its own LWJGL 2.
configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
}

version = property("mod_version") as String
group = property("maven_group") as String
base.archivesName = property("mod_id") as String

repositories {
    mavenCentral()
    google()
    maven("https://maven.ornithemc.net/releases")
    maven("https://repo.polyfrost.org/releases")
    maven("https://central.sonatype.com/repository/maven-snapshots") {
        content { includeGroup("net.kyori") }
    }
    maven("https://redirector.kotlinlang.org/maven/compose-dev") {
        content { includeGroup("org.jetbrains.skiko") }
    }
    maven("https://maven.cloverclient.com/releases") {
        content { includeGroup("pl.tomgirl") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.fabricmc.net/") }
        filter { includeGroup("net.fabricmc") }
    }
}

dependencies {
    val mc = property("minecraft_version") as String
    minecraft("com.mojang:minecraft:$mc")
    mappings(ploceus.layeredMappings {
        mappings("net.ornithemc:feather-gen2:$mc+build.${property("feather_build")}:v2") {
            containsUnpick()
        }
    })
    ploceus.dependOsl(property("osl_version") as String)
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_language_kotlin_version")}")

    val oneconfig = property("oneconfig_version") as String
    modImplementation("org.polyfrost.oneconfig:$mc-ornithe:$oneconfig") {
        exclude(group = "net.fabricmc", module = "fabric-language-kotlin")
    }
    for (module in listOf("config", "config-impl", "events", "hud", "internal", "ui", "utils", "commands")) {
        implementation("org.polyfrost.oneconfig:$module:$oneconfig")
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.processResources {
    val props = mapOf(
        "id" to project.property("mod_id"),
        "name" to project.property("mod_name"),
        "version" to project.version,
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
}
