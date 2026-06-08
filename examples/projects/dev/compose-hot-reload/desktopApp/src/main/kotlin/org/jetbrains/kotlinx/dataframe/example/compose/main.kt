package org.jetbrains.kotlinx.dataframe.example.compose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "demo",
        ) {
            App()
        }
    }
