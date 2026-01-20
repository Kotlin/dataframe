import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publisher)
    alias(libs.plugins.ktlint)
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

group = "org.jetbrains.kotlinx.dataframe"

dependencies {
    implementation(projects.dataframe)
    // experimental
    implementation(projects.dataframeOpenapiGenerator)

    implementation(libs.ksp.api)
    implementation(libs.kotlin.reflect)
    implementation(libs.h2db)
    testImplementation(libs.h2db)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.compile.testing.ksp)
    testImplementation(libs.ktor.server.netty)
    testImplementation(libs.kotestAssertions)
}

kotlinPublications {
    pom {
        githubRepo("Kotlin", "dataframe")
        inceptionYear = "2021"
        licenses {
            apache2()
        }
        developers {
            developer("koperagen", "Nikita Klimenko", "nikita.klimenko@jetbrains.com")
        }
    }

    publication {
        groupId = "org.jetbrains.kotlinx.dataframe"
        publicationName = "maven"
        artifactId = "symbol-processor-all"
        description = "Annotation preprocessor for DataFrame"
        packageName = artifactId
    }
}

// uses java 11 for testing
tasks.compileTestKotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}
tasks.compileTestJava {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.release.set(11)
}
