plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("base")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.snippetsearcher"
version = "0.0.1-SNAPSHOT"
description = "Actions service for Snippet Searcher platform"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring y Kotlin
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Base de datos
    runtimeOnly("org.postgresql:postgresql")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
    jvmToolchain(17)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/detekt/detekt.yml"))
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("build/**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "insert_final_newline" to "true"
            )
        )
        trimTrailingWhitespace()
    }
}

kover {
    htmlReport {
        onCheck.set(true)
    }
    verify {
        rule {
            bound {
                minValue = 0
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.spotlessCheck {
    mustRunAfter(tasks.clean)
}

tasks.detekt {
    mustRunAfter(tasks.spotlessCheck)
}

tasks.test {
    mustRunAfter(tasks.detekt)
}

tasks.named("koverVerify") {
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(tasks.spotlessCheck)
    dependsOn(tasks.detekt)
    dependsOn(tasks.test)
    dependsOn(tasks.named("koverVerify"))
}

tasks.named("build") {
    dependsOn(tasks.check)
}
