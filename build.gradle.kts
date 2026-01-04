plugins {
    id("org.springframework.boot") version "3.0.13"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.24" apply false // remove if not using Kotlin code
    id("java")
    id("com.diffplug.spotless") version "8.1.0"
    id("org.openrewrite.rewrite") version "6.11.0"
}

group = "com.razdeep"
version = "0.0.1-SNAPSHOT"
description = "konsign-api"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target("src/**/*.java")

        palantirJavaFormat()
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()

        lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jjwt)
    implementation(libs.jaxb.api)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgres.connector)
    implementation(libs.gson)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.springdoc.openapi.ui)

    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.jedis)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.jackson.jsr310)

    runtimeOnly(libs.micrometer.prometheus)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.mysql)

    rewrite("org.openrewrite.recipe:rewrite-spring:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

rewrite {
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0")
}
