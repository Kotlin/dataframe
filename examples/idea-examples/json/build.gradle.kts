import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.dataframe.api.JsonPath

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlinx.dataframe")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

dependencies {
    implementation(project(":core"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dataframes {
    // Metrics, no key-value paths
    schema {
        data = "src/main/resources/apiGuruMetrics.json"
        name = "org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.noOpenApi.MetricsNoKeyValue"
    }

    // Metrics, with key-value paths
    schema {
        data = "src/main/resources/apiGuruMetrics.json"
        name = "org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.noOpenApi.MetricsKeyValue"
        jsonOptions {
            keyValuePaths = listOf(
                JsonPath()
                    .append("datasets")
                    .appendArrayWithWildcard()
                    .append("data"),
            )
        }
    }

    // ApiGuru, OpenApi
    schema {
        data = "src/main/resources/ApiGuruOpenApi.yaml"
        // name is still needed to get the full path
        name = "org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.ApiGuruOpenApi"
    }
}
