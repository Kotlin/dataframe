package org.jetbrains.dataframe.gradle

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun useHostedJson(json: String, f: (url: String) -> Unit) {
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
