import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.dataframe.api.JsonPath

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlinx.dataframe")
}

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))
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
        name = "org.jetbrains.kotlinx.dataframe.examples.openapi.ApiGuruOpenApiGradle"
    }
}
