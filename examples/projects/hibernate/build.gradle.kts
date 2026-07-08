plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.ktlint.gradle)

    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Add general `dataframe` dependency
    implementation(libs.dataframe)

    // Hibernate + H2 + HikariCP (for Hibernate example)
    implementation(libs.hibernate.core)
    implementation(libs.hibernate.hikaricp)
    implementation(libs.hikaricp)

    implementation(libs.h2db)
    implementation(libs.sl4jsimple)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
    // rules are set up through .editorconfig
}
