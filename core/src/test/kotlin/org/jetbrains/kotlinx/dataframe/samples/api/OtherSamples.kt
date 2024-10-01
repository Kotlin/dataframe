package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.explainer.WritersideFooter
import org.jetbrains.kotlinx.dataframe.explainer.WritersideStyle
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHTML
import org.junit.Test
import java.io.File

// To display code together with a table, we can use TransformDataFrameExpressions annotation together with korro
// This class provides an ability to save only a table that can be embedded anywhere in the documentation
class OtherSamples {

    @Test
    fun example() {
        val df = DataFrame.readCSV("../data/titanic.csv", delimiter = ';').take(5)
        // writeTable(df, "exampleName")
    }

    private fun writeTable(df: AnyFrame, name: String) {
        val dir = File("../docs/StardustDocs/snippets/manual").also { it.mkdirs() }
        val html = df.toStandaloneHTML(getFooter = WritersideFooter) + WritersideStyle
        html.writeHTML(File(dir, "$name.html"))
    }
}
