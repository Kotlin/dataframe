package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.types.UtilTests
import java.net.URL

fun testResource(resourcePath: String): URL = UtilTests::class.java.classLoader.getResource(resourcePath)!!
fun testCsv(csvName: String) = testResource("$csvName.csv")
fun testJson(jsonName: String) = testResource("$jsonName.json")
fun testArrowFeather(name: String) = testResource("$name.feather")
