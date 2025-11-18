import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.publisher)
    alias(libs.plugins.ktlint)
}

group = "org.jetbrains.kotlinx.dataframe"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core")) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-datetime-jvm")
        exclude(group = "commons-io", module = "commons-io")
        exclude(group = "commons-io", module = "commons-csv")
        exclude(group = "org.apache.commons", module = "commons-csv")
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "io.github.microutils", module = "kotlin-logging-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json-jvm")
        exclude(group = "commons-codec", module = "commons-codec")
        exclude(group = "com.squareup", module = "kotlinpoet-jvm")
        exclude(group = "ch.randelshofer", module = "fastdoubleparser")
        exclude(group = "io.github.oshai", module = "kotlin-logging-jvm")
        exclude(group = "org.jetbrains", module = "annotations")
    }

    // we assume Kotlin plugin has reflect dependency - we're not bringing our own version
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

tasks.withType<ShadowJar> {
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-datetime-jvm:.*"))
        exclude(dependency("commons-io:commons-io:.*"))
        exclude(dependency("commons-io:commons-csv:.*"))
        exclude(dependency("org.apache.commons:commons-csv:.*"))
        exclude(dependency("org.slf4j:slf4j-api:.*"))
        exclude(dependency("io.github.microutils:kotlin-logging-jvm:.*"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:.*"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:.*"))
        exclude(dependency("commons-codec:commons-codec:.*"))
        exclude(dependency("com.squareup:kotlinpoet-jvm:.*"))
        exclude(dependency("ch.randelshofer:fastdoubleparser:.*"))
        exclude(dependency("io.github.oshai:kotlinlogging:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }
    exclude("org/jetbrains/kotlinx/dataframe/jupyter/**")
    exclude("org/jetbrains/kotlinx/dataframe/io/**")
    exclude("org/jetbrains/kotlinx/dataframe/documentation/**")
    exclude("org/jetbrains/kotlinx/dataframe/impl/io/**")
    exclude("io/github/oshai/kotlinlogging/**")
    exclude("apache/**")
    exclude("**.html")
    exclude("**.js")
    exclude("**.css")
    exclude("META-INF/kotlin-jupyter-libraries/**")
}

kotlinPublications {
    publication {
        publicationName = "shadowed"
        artifactId = "dataframe-compiler-plugin-core"
        packageName = artifactId
    }
}
