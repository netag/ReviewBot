import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
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
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
}

dependencies {
    val arrow_version: String by project
    val ktor_version: String by project
    val logback_version: String by project
    val koin_version: String by project
    val kotlinx_version: String by project
    val config4k_version: String by project
    val kmongo_version: String by project

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.config4k:config4k:$config4k_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_version")

    // ktor server
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")

    // ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    // arrow
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-syntax:$arrow_version")

    // mongo
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")

    // koin
    implementation("org.koin:koin-core:$koin_version")
    implementation("org.koin:koin-core-ext:$koin_version")
    implementation("org.koin:koin-ktor:$koin_version")
    testImplementation("org.koin:koin-test:$koin_version")
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
