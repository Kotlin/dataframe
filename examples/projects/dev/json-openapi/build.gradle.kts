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

    // explicitly depend on dataframe-openapi, since its support is experimental
    // and only supports OpenAPI 3.0
    // See https://kotlin.github.io/dataframe/openapi.html for more info
    implementation(libs.dataframe.openapi)
    implementation(libs.dataframe.openapi.generator)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
    // rules are set up through .editorconfig
}
