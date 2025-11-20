plugins {
    id("java")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.gson)
}
java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}