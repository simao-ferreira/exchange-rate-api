plugins {
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.10"
    groovy
}

group = "io.template"
version = "1.0-SNAPSHOT"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("org.springdoc:springdoc-openapi:2.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.0.2")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.apache.groovy:groovy-all:4.0.9")
    testImplementation("org.spockframework:spock-core:2.4-M1-groovy-4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
