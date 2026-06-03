plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.ktlint.gradle)

    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Add general `dataframe` dependency
    implementation(libs.dataframe)

    // multik support
    implementation(libs.multik.core)
    implementation(libs.multik.default)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
    // rules are set up through .editorconfig
}
