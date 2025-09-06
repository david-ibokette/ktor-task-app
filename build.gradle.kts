import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "net.ibokette"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    implementation("io.ktor:ktor-server-core:3.2.3")
    implementation("io.ktor:ktor-server-host-common:3.2.3")
    implementation("io.ktor:ktor-server-status-pages:3.2.3")
    implementation("io.ktor:ktor-server-core:3.2.3")
    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-5")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-5")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-5")
    implementation("org.postgresql:postgresql:42.7.3")
    testImplementation(libs.ktor.server.test.host)

    // Use kotlin.test with JUnit 5 (Jupiter)
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

// Run tests on the JUnit Platform
tasks.test {
    useJUnitPlatform()
}
