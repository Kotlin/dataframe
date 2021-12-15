package org.jetbrains.dataframe.gradle

import org.junit.Before
import java.util.*

abstract class AbstractDataFramePluginIntegrationTest {
    protected val kotlinVersion = TestData.kotlinVersion
    protected lateinit var dataframeJarPath: String

    @Before
    fun before() {
        val properties = Properties().also {
            it.load(javaClass.getResourceAsStream("df.properties"))
        }
        dataframeJarPath = properties.getProperty("DATAFRAME_JAR")
    }
}
