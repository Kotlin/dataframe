package org.jetbrains.dataframe.gradle

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun useHostedJson(
    json: String,
    f: (url: String) -> Unit,
) {
    // duplicated in ksp/EmbeddedServerRunners.kt
    val port = 14771
    val server = embeddedServer(Netty, port = port) {
        routing {
            get("/test.json") {
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start()
    f("http://0.0.0.0:$port/test.json")
    server.stop(500, 1000)
}
