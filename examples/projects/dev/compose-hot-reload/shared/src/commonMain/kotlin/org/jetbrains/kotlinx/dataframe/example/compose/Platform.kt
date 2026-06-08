package org.jetbrains.kotlinx.dataframe.example.compose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform