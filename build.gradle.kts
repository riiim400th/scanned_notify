import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20-Beta2"
    id("com.github.johnrengelman.shadow") version "8.1.1"

}

group = "audit-notify"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("net.portswigger.burp.extensions:montoya-api:2024.7")
}

tasks.test {
    useJUnitPlatform()
}


tasks.withType<KotlinCompile>().configureEach{
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}