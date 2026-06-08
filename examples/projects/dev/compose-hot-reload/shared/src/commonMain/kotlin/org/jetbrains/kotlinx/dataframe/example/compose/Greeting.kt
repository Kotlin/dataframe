package org.jetbrains.kotlinx.dataframe.example.compose

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = sayHello(platform.name)
}
