@file:ImportDataSchema(
    path = "src/main/resources/ApiGuruOpenApi.yaml",
    name = "ApiGuruOpenApiKsp",
    enableExperimentalOpenApi = true,
)
@file:ImportDataSchema(
    path = "https://raw.githubusercontent.com/1Password/connect/aac5e44b27570036e6b56e9f5b2a398a824ae5fc/docs/openapi/spec.yaml",
    name = "OnePassword",
    enableExperimentalOpenApi = true,
)

package org.jetbrains.kotlinx.dataframe.examples.openapi

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.value

/**
 * In this file we'll demonstrate how to use OpenApi schemas
 * to generate DataSchemas and how to use them.
 */
fun main() {
    gradle()
    ksp()
}

/**
 * Gradle example of reading JSON files with OpenApi schemas.
 * Ctrl+Click on [GradleAPIs] or [GradleMetrics] to see the generated code.
 *
 * (We use import aliases to avoid clashes with the KSP example)
 */
private fun gradle() {
    val apis = ApiGuruOpenApiGradle.APIs.readJson("examples/idea-examples/json/src/main/resources/ApiGuruSample.json")
    apis.print(columnTypes = true, title = true, borders = true)

    apis.filter {
        value.versions.value.any {
            (it.updated ?: it.added).year >= 2021
        }
    }

    val metrics =
        ApiGuruOpenApiGradle.Metrics.readJson("examples/idea-examples/json/src/main/resources/apiGuruMetrics.json")
    metrics.print(columnTypes = true, title = true, borders = true)
}

/**
 * KSP example of reading JSON files with OpenApi schemas.
 * Ctrl+Click on [APIs] or [Metrics] to see the generated code.
 */
private fun ksp() {
    val apis = ApiGuruOpenApiKsp.APIs.readJson("examples/idea-examples/json/src/main/resources/ApiGuruSample.json")
    apis.print(columnTypes = true, title = true, borders = true)

    val metrics =
        ApiGuruOpenApiKsp.Metrics.readJson("examples/idea-examples/json/src/main/resources/apiGuruMetrics.json")
    metrics.print(columnTypes = true, title = true, borders = true)
}
