package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.explainer.WritersideFooter
import org.jetbrains.kotlinx.dataframe.explainer.WritersideStyle
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.junit.Test
import java.io.File

// To display code together with a table, we can use TransformDataFrameExpressions annotation together with korro
// This class provides an ability to save only a table that can be embedded anywhere in the documentation
class OtherSamples : TestBase() {

    @Test
    fun example() {
        val df = DataFrame.read("../data/movies.csv").take(5)
        // writeTable(df, "exampleName")
    }

    @Test
    fun formatExample() {
        val formattedDf = df
            .format().with { bold and textColor(black) }
            .format { isHappy }.with { background(if (it) green else red) }
            .format { weight }.notNull().linearBg(50 to FormattingDsl.blue, 90 to FormattingDsl.red)
            .format { age }.perRowCol { row, col ->
                textColor(
                    linear(value = col[row], from = col.min() to blue, to = col.max() to green),
                )
            }

        writeTable(formattedDf, "formatExample")
    }

    private fun writeTable(df: AnyFrame, name: String) {
        val dir = File("../docs/StardustDocs/resources/snippets/manual").also { it.mkdirs() }
        val html = df.toStandaloneHtml(getFooter = WritersideFooter) + WritersideStyle
        html.writeHtml(File(dir, "$name.html"))
    }

    private fun writeTable(formattedDf: FormattedFrame<*>, name: String) {
        val dir = File("../docs/StardustDocs/resources/snippets/manual").also { it.mkdirs() }
        val html = formattedDf.df.toStandaloneHtml(
            configuration = formattedDf.getDisplayConfiguration(DisplayConfiguration.DEFAULT),
            getFooter = WritersideFooter,
        ) + WritersideStyle
        html.writeHtml(File(dir, "$name.html"))
    }
}
