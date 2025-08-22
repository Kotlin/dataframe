package org.jetbrains.kotlinx.dataframe.examples.springboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringbootDataframeApplication

fun main(args: Array<String>) {
    runApplication<SpringbootDataframeApplication>(*args)
}
