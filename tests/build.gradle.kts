
plugins {
    java
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(korro)
        alias(ktlint)
        alias(kover)

        alias(dataframe)
        // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
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
        include("docs/StardustDocs/topics/read.md")
        include("docs/StardustDocs/topics/write.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
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
