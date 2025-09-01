import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.korro
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.main
import org.gradle.kotlin.dsl.projects
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.runKtlintCheckOverMainSourceSet
import org.gradle.kotlin.dsl.runKtlintCheckOverTestSourceSet
import org.gradle.kotlin.dsl.runKtlintFormatOverMainSourceSet
import org.gradle.kotlin.dsl.runKtlintFormatOverTestSourceSet
import org.gradle.kotlin.dsl.sourceSets
import org.gradle.kotlin.dsl.test
import org.gradle.kotlin.dsl.testImplementation

plugins {
    java
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(korro)
        alias(ktlint)
        alias(dataframePlugin)
//        alias(kover)
        alias(ksp)
    }
}

repositories {
    mavenCentral()
    mavenLocal() // for local development
}

dependencies {
    implementation(projects.dataframe)
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kandy)
    testImplementation(libs.kandy.samples.utils)
    testImplementation(libs.kotlin.datetimeJvm)
    testImplementation(libs.poi)
    testImplementation(libs.arrow.vector)
}

kotlin.sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin/")
    }
}

korro {
    docs = fileTree(rootProject.rootDir) {
        // todo topics/*.md as a part of #898
        include("docs/StardustDocs/topics/DataSchema-Data-Classes-Generation.md")
        include("docs/StardustDocs/topics/read.md")
        include("docs/StardustDocs/topics/write.md")
        include("docs/StardustDocs/topics/rename.md")
        include("docs/StardustDocs/topics/format.md")
        include("docs/StardustDocs/topics/guides/*.md")
        include("docs/StardustDocs/topics/operations/utils/*.md")
        include("docs/StardustDocs/topics/collectionsInterop/*.md")
        include("docs/StardustDocs/topics/dataSources/sql/*.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/utils/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/collectionsInterop/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/guides/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/io/*.kt")
    }

    groupSamples {

        beforeSample = "<tab title=\"NAME\">\n"
        afterSample = "\n</tab>"

        funSuffix("_properties") {
            replaceText("NAME", "Properties")
        }
        funSuffix("_accessors") {
            replaceText("NAME", "Accessors")
        }
        funSuffix("_strings") {
            replaceText("NAME", "Strings")
        }
        beforeGroup = "<tabs>\n"
        afterGroup = "</tabs>"
    }
}

tasks.runKtlintFormatOverMainSourceSet {
    dependsOn("kspKotlin")
}

tasks.runKtlintFormatOverTestSourceSet {
    dependsOn("kspTestKotlin")
}

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn("kspKotlin")
}

tasks.runKtlintCheckOverTestSourceSet {
    dependsOn("kspTestKotlin")
}

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}
