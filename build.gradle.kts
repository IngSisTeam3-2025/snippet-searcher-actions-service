plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.spring") version "2.0.10"
    kotlin("plugin.jpa") version "2.0.10"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("com.diffplug.spotless") version "6.22.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
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
    //Spring y Kotlin
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// LINTER
detekt {
    buildUponDefaultConfig = true
    config.setFrom(
        resources.text.fromString(
            """
            style:
              SpacingBetweenPackageAndImports:
                active: true
              MaxLineLength:
                active: true
                maxLineLength: 120
              LoopWithTooManyJumpStatements:
                active: false
              ReturnCount:
                active: false
              UseCheckOrError:
                active: false
            complexity:
              TooManyFunctions:
                active: false
              CyclomaticComplexMethod:
                active: false
              LongMethod:
                active: false
            exceptions:
              TooGenericExceptionThrown:
                active: false
            """.trimIndent()
        )
    )
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "21"
}

//FORMATTER
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

//COVERAGE
kover {
    verify {
        rule {
            bound {
                minValue = 0
            }
        }
    }
}

tasks.check {
    dependsOn("detekt", "spotlessCheck", "koverVerify")
}

tasks.named("build") {
    dependsOn("spotlessApply", "check")
}