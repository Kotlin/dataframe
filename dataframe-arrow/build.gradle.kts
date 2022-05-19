plugins {
    kotlin("jvm") apply true
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":"))

    implementation(libs.arrow.vector)
    implementation(libs.arrow.format)
    implementation(libs.arrow.memory)
    implementation(libs.commonsCompress)

    testApi(project(":"))
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}
