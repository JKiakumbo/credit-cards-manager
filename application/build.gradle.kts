plugins {
    kotlin("jvm")
}

group = "dev.jkiakumbo.ccm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
    testImplementation("org.assertj:assertj-core:3.24.2")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}