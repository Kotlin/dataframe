package org.jetbrains.dataframe.ksp

import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
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
