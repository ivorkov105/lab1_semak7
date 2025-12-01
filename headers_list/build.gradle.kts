plugins {
    id("org.jetbrains.kotlin.jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.gson)
    testImplementation(libs.junit)
}

kotlin {
    jvmToolchain(17)
}
