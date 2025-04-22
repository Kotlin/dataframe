import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(kover)
        alias(ktlint)
        alias(kodex)
        alias(buildconfig)
        alias(binary.compatibility.validator)

        // generates keywords using the :generator module
//        alias(keywordGenerator)

        // dependence on our own plugin
//        alias(dataframe)

        // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
//        alias(ksp)
    }
}

group = "org.jetbrains.kotlinx"
version = "1.0.0-dev"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(projects.core)
    implementation(libs.kotlin.stdlib)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.sl4j)

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)
    testImplementation(libs.sl4jsimple)
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    friendPaths.from(project(projects.core.path).projectDir)
}

tasks.test {
    useJUnitPlatform()
}

kotlinPublications {
    publication {
        publicationName = "dataframeJson"
        artifactId = project.name
        description = "Kotlin DataFrame JSON integration."
        packageName = artifactId
    }
}
