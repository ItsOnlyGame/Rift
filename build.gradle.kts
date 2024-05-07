import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.iog"
version = "6.0.1"

application {
    mainClass = "com.iog.Main"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven(url = "https://maven.lavalink.dev/releases")
}

dependencies {
    implementation("dev.lavalink.youtube:v2:1.1.0")
    implementation("dev.arbjerg:lavaplayer:2.1.2")
    implementation("net.dv8tion:JDA:5.0.0-beta.21")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("se.michaelthelin.spotify:spotify-web-api-java:6.5.2")

    implementation("org.slf4j:slf4j-simple:2.0.5")
    implementation("org.tinylog:tinylog-api:2.6.1")
    implementation("org.tinylog:tinylog-impl:2.6.1")
    implementation("org.reflections:reflections:0.10.2")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")
}

tasks.register("generateVersionProperties") {
    doLast {
        val versionFile = file("$projectDir/src/main/resources/com/iog/version.txt")
        versionFile.parentFile.mkdirs()
        File("$projectDir/src/main/resources/com/iog/version.txt").bufferedWriter().use { out ->
            out.write("$version")
        }
    }
}

tasks.named("processResources") {
    dependsOn("generateVersionProperties")
}