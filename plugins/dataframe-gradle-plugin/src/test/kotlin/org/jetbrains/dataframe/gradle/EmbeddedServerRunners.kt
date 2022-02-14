package org.jetbrains.dataframe.gradle

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun useHostedJson(json: String, f: (url: String) -> Unit) {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/test.json") {
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start()
    f("http://0.0.0.0:8080/test.json")
    server.stop(500, 1000)
}
