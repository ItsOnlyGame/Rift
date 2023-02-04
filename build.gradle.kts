import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.FileOutputStream
import java.util.Properties

val generatedVersionDir = "$buildDir/generated-version"

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.iog"
version = "5.0.3"

application {
    mainClass.set("com.iog.Main")
}

repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://m2.dv8tion.net/releases")
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.iog:lavaplayer:1.4.0")
    implementation("com.discord4j:discord4j-core:3.2.3")

    implementation("org.reflections:reflections:0.10.2")

    implementation("org.slf4j:slf4j-simple:2.0.4")
    implementation("org.tinylog:tinylog-api:2.5.0")
    implementation("org.tinylog:tinylog-impl:2.5.0")
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