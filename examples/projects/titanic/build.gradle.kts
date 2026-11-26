plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.ktlint.gradle)

    application
}

repositories {
    mavenCentral()
}

application.mainClass = "org.jetbrains.kotlinx.dataframe.examples.titanic.ml.TitanicKt"

dependencies {
    // Add general `dataframe` dependency
    implementation(libs.dataframe)

    // note: needs to target java 11 for these dependencies
    implementation(libs.kotlin.dl.api)
    implementation(libs.kotlin.dl.impl)
    implementation(libs.kotlin.dl.tensorflow)
    implementation(libs.kotlin.dl.dataset)
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
