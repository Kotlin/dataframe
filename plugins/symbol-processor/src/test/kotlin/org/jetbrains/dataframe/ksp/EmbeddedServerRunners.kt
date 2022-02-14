package org.jetbrains.dataframe.ksp

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun useHostedFile(file: File, f: (url: String) -> Unit) {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondFile(file)
            }
        }
    }.start()
    f("http://0.0.0.0:8080/")
    server.stop(500, 1000)
}
