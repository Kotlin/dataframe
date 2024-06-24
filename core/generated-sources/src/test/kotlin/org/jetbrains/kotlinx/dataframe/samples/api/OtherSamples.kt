package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.explainer.WritersideFooter
import org.jetbrains.kotlinx.dataframe.explainer.WritersideStyle
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHTML
import org.junit.Test
import java.io.File

// To display code together with a table, we can use TransformDataFrameExpressions annotation together with korro
// This class provides an ability to save only a table that can be embedded anywhere in the documentation
class OtherSamples {
    @Test
    fun extensionPropertiesApi1() {
        val df = dataFrameOf("example")(123)
        writeTable(df, "extensionPropertiesApi1")
    }

    private fun writeTable(df: AnyFrame, name: String) {
        val dir = File("../docs/StardustDocs/snippets/manual").also { it.mkdirs() }
        val html = df.toStandaloneHTML(getFooter = WritersideFooter) + WritersideStyle
        html.writeHTML(File(dir, "$name.html"))
    }
}
