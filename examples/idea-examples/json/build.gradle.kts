import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.dataframe.api.JsonPath

plugins {
    application
    kotlin("jvm")

    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))

    // explicitly depend on openApi
    implementation(projects.dataframeOpenapi)
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
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

    enableExperimentalOpenApi = true
}
