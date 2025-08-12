package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.explainer.WritersideFooter
import org.jetbrains.kotlinx.dataframe.explainer.WritersideStyle
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.junit.Test
import java.io.File

// To display code together with a table, we can use TransformDataFrameExpressions annotation together with korro
// This class provides an ability to save only a table that can be embedded anywhere in the documentation

@Deprecated("This can now be reproduced with 'sample dataframes' in the 'tests' module.")
class OtherSamples : TestBase() {

    @Test
    fun example() {
        val df = DataFrame.read("../data/movies.csv").take(5)
        // writeTable(df, "exampleName")
    }

    private fun writeTable(df: AnyFrame, name: String) {
        val dir = File("../docs/StardustDocs/resources/snippets/manual").also { it.mkdirs() }
        val html = df.toStandaloneHtml(getFooter = WritersideFooter) + WritersideStyle
        html.writeHtml(File(dir, "$name.html"))
    }
}
