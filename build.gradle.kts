import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("com.diffplug.spotless") version "5.11.1"
    application
}

group = "no.group"
version = "0.1"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.4.0")
    implementation("io.ktor:ktor-html-builder:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "MainKt"
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.useIR = true
        kotlinOptions.freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
}
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint("0.39.0").userData(mapOf("disabled_rules" to "no-wildcard-imports,filename"))
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.39.0")
    }
}

application {
    mainClass.set("MainKt")
}
