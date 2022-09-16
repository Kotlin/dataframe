package org.jetbrains.dataframe.ksp

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun useHostedFile(file: File, f: (url: String) -> Unit) {
    // duplicated in gradle/EmbeddedServerRunners.kt
    val port = 14771
    val server = embeddedServer(Netty, port = port) {
        routing {
            get("/") {
                call.respondFile(file)
            }
        }
    }.start()
    try {
        f("http://0.0.0.0:$port/")
    } finally {
        server.stop(500, 1000)
    }
}
