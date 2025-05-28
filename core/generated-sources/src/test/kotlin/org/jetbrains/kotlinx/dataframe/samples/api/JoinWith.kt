@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.FormattingDSL
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.JoinedDataRow
import org.jetbrains.kotlinx.dataframe.api.RGBColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.excludeJoinWith
import org.jetbrains.kotlinx.dataframe.api.filterJoinWith
import org.jetbrains.kotlinx.dataframe.api.fullJoinWith
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.innerJoinWith
import org.jetbrains.kotlinx.dataframe.api.joinWith
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.leftJoinWith
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.rightJoinWith
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.explainer.PluginCallbackProxy
import org.jetbrains.kotlinx.dataframe.explainer.SamplesDisplayConfiguration
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.renderValueForHtml
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.jupyter.ChainedCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.junit.Test
import java.time.format.DateTimeFormatter

@Suppress("ktlint:standard:argument-list-wrapping")
class JoinWith : TestBase() {

    @DataSchema
    interface Campaigns {
        val name: String
        val startDate: LocalDate
        val endDate: LocalDate
    }

    @DataSchema
    interface Visits {
        val date: LocalDate
        val userId: Int
    }

    private val campaigns = dataFrameOf("name", "startDate", "endDate")(
        "Winter Sale", LocalDate(2023, 1, 1), LocalDate(2023, 1, 31),
        "Spring Sale", LocalDate(2023, 4, 1), LocalDate(2023, 4, 30),
        "Summer Sale", LocalDate(2023, 7, 1), LocalDate(2023, 7, 31),
        "Autumn Sale", LocalDate(2023, 10, 1), LocalDate(2023, 10, 31),
    ).cast<Campaigns>()

    private val visits = dataFrameOf("date", "usedId")(
        LocalDate(2023, 1, 10), 1,
        LocalDate(2023, 1, 20), 2,
        LocalDate(2023, 4, 15), 1,
        LocalDate(2023, 5, 1), 3,
        LocalDate(2023, 7, 10), 2,
    ).cast<Visits>()

    class ColoredValue<T>(val value: T, val backgroundColor: RGBColor, val textColor: RGBColor) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ColoredValue<*>

            return value == other.value
        }

        override fun hashCode(): Int = value?.hashCode() ?: 0
    }

    private val renderer = object : ChainedCellRenderer(DefaultCellRenderer) {
        override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? =
            if (value is ColoredValue<*>) {
                if (value.value is LocalDate) {
                    RenderedContent.text(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy").format(value.value.toJavaLocalDate()),
                    )
                } else {
                    renderValueForHtml(value.value, configuration.cellContentLimit, configuration.decimalFormat)
                }
            } else {
                null
            }

        override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? = null
    }

    private fun AnyFrame.unwrapColoredValues(): AnyFrame =
        convert {
            colsAtAnyDepth().colsOf<ColoredValue<*>?>()
        }.with(Infer.Type) { it?.value }

    private fun <T> T.colored(background: RGBColor, text: RGBColor) = ColoredValue(this, background, text)

    private fun <T> T.winter(background: RGBColor = RGBColor(179, 205, 224), text: RGBColor = RGBColor(0, 0, 51)) =
        ColoredValue(this, background, text)

    private fun <T> T.spring(background: RGBColor = RGBColor(204, 235, 197), text: RGBColor = RGBColor(0, 51, 0)) =
        ColoredValue(this, background, text)

    private fun <T> T.summer(background: RGBColor = RGBColor(176, 224, 230), text: RGBColor = RGBColor(25, 25, 112)) =
        ColoredValue(this, background, text)

    private fun <T> T.autumn(background: RGBColor = RGBColor(221, 160, 221), text: RGBColor = RGBColor(85, 26, 139)) =
        ColoredValue(this, background, text)

    private val coloredCampaigns = dataFrameOf("name", "startDate", "endDate")(
        "Winter Sale".winter(), LocalDate(2023, 1, 1).winter(), LocalDate(2023, 1, 31).winter(),
        "Spring Sale".spring(), LocalDate(2023, 4, 1).spring(), LocalDate(2023, 4, 30).spring(),
        "Summer Sale".summer(), LocalDate(2023, 7, 1).summer(), LocalDate(2023, 7, 31).summer(),
        "Autumn Sale".autumn(), LocalDate(2023, 10, 1).autumn(), LocalDate(2023, 10, 31).autumn(),
    )

    @Suppress("ktlint:standard:chain-method-continuation", "ktlint:standard:max-line-length")
    private val coloredVisits = dataFrameOf("date", "usedId")(
        LocalDate(2023, 1, 10).winter(), 1.winter(),
        LocalDate(2023, 1, 20).winter(), 2.winter(),
        LocalDate(2023, 4, 15).spring(), 1.spring(),
        LocalDate(2023, 5, 1).colored(FormattingDSL.white, FormattingDSL.black), 3.colored(FormattingDSL.white, FormattingDSL.black),
        LocalDate(2023, 7, 10).summer(), 2.summer(),
    )

    private fun AnyFrame.toColoredHTML() =
        toHtml(
            getFooter = { null },
            cellRenderer = renderer,
            configuration = SamplesDisplayConfiguration.copy(
                cellFormatter = { row, col ->
                    val value = row[col]
                    if (value is ColoredValue<*>) {
                        background(value.backgroundColor) and textColor(value.textColor)
                    } else {
                        background(white)
                    }
                },
            ),
        )

    private val joinExpression: JoinedDataRow<Any?, Any?>.(it: JoinedDataRow<Any?, Any?>) -> Boolean = {
        right[{ "date"<ColoredValue<LocalDate>>() }].value in
            "startDate"<ColoredValue<LocalDate>>().value.."endDate"<ColoredValue<LocalDate>>().value
    }

    private fun DataFrameHtmlData.wrap(title: String): DataFrameHtmlData =
        copy(
            body =
            """
                <div class="table-container">
                    <b>$title</b>
                    $body
                </div>
                """.trimIndent(),
        )

    private fun DataFrameHtmlData.wrap(): DataFrameHtmlData =
        copy(
            body =
            """
                <div class="table-container">
                    $body
                </div>
                """.trimIndent(),
        )

    private fun snippetOutput(coloredResult: DataFrame<Any?>, result: DataFrame<Any?>) {
        coloredCampaigns.unwrapColoredValues() shouldBe campaigns
        coloredVisits.unwrapColoredValues() shouldBe visits
        coloredResult.unwrapColoredValues() shouldBe result

        PluginCallbackProxy.overrideHtmlOutput(
            manualOutput = DataFrameHtmlData
                .tableDefinitions()
                .plus(coloredCampaigns.toColoredHTML().wrap("campaigns"))
                .plus(coloredVisits.toColoredHTML().wrap("visits"))
                .plus(coloredResult.toColoredHTML().wrap("result"))
                .plus(
                    DataFrameHtmlData(
                        style =
                        """
                            body {
                                display: flex;
                                align-items: flex-start;
                                overflow-x: auto;
                                font-family: "JetBrains Mono", SFMono-Regular, Consolas, "Liberation Mono", Menlo, Courier, monospace;
                                font-size: 14px;
                            }

                            :root {
                                color: #19191C;
                                background-color: #fff;
                            }
                            
                            :root[theme="dark"] {
                                background-color: #19191C;
                                color: #FFFFFFCC
                            }
                            
                            .table-container {
                                margin-right: 20px; 
                            }
                            
                            .table-container:not(:last-child) {
                                margin-right: 20px; 
                            }
                            
                            td {
                                white-space: nowrap;
                            }
                            """.trimIndent(),
                    ),
                ),
        )
    }

    @TransformDataFrameExpressions
    @Test
    fun joinWith_strings() {
        val result =
            // SampleStart
            campaigns.innerJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.innerJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun filterJoinWith_strings() {
        val result =
            // SampleStart
            campaigns.filterJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.filterJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun leftJoinWith_strings() {
        val result =
            // SampleStart
            campaigns.leftJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.leftJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun rightJoinWith_strings() {
        val result =
            // SampleStart
            campaigns.rightJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.rightJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun fullJoinWith_strings() {
        val result =
            // SampleStart
            campaigns.fullJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.fullJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun excludeJoinWith_strings() {
        val result =
            // SampleStart
            campaigns.excludeJoinWith(visits) {
                right.getValue<LocalDate>("date") in "startDate"<LocalDate>().."endDate"<LocalDate>()
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.excludeJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun joinWith_properties() {
        val result =
            // SampleStart
            campaigns.innerJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.innerJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun filterJoinWith_properties() {
        val result =
            // SampleStart
            campaigns.filterJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.filterJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun leftJoinWith_properties() {
        val result =
            // SampleStart
            campaigns.leftJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.leftJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun rightJoinWith_properties() {
        val result =
            // SampleStart
            campaigns.rightJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.rightJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun fullJoinWith_properties() {
        val result =
            // SampleStart
            campaigns.fullJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.fullJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun excludeJoinWith_properties() {
        val result =
            // SampleStart
            campaigns.excludeJoinWith(visits) {
                right.date in startDate..endDate
            }
        // SampleEnd
        val coloredResult = coloredCampaigns.excludeJoinWith(coloredVisits, joinExpression = joinExpression)
        snippetOutput(coloredResult, result)
    }

    @TransformDataFrameExpressions
    @Test
    fun crossProduct() {
        val result =
            // SampleStart
            campaigns.joinWith(visits) { true }
        // SampleEnd
        val coloredResult = coloredCampaigns.joinWith(coloredVisits) { true }
        snippetOutput(coloredResult, result)
    }

    val df1 = dataFrameOf("index", "age", "name")(
        1.spring(), 15.spring(), "BOB".spring(),
        2.summer(), 19.summer(), "ALICE".summer(),
        3.autumn(), 20.autumn(), "CHARLIE".autumn(),
    )

    val df2 = dataFrameOf("index", "age", "name")(
        1.spring(), 15.spring(), "Bob".spring(),
        2.summer(), 19.summer(), "Alice".summer(),
        4.winter(), 21.winter(), "John".winter(),
    )

    @TransformDataFrameExpressions
    @Test
    fun compareInnerColumns() {
        // SampleStart
        df1.innerJoin(df2, "index", "age")
        // SampleEnd

        PluginCallbackProxy.overrideHtmlOutput(
            manualOutput = DataFrameHtmlData.tableDefinitions()
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap("df1"))
                        .plus(df2.toColoredHTML().wrap("df2"))
                        .plus(df1.innerJoin(df2, "index", "age").toColoredHTML().wrap("result"))
                        .wrapRow(),
                )
                .plus(other),
        )
    }

    @TransformDataFrameExpressions
    @Test
    fun compareInnerValues() {
        // SampleStart
        df1.innerJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
        // SampleEnd

        PluginCallbackProxy.overrideHtmlOutput(
            manualOutput = DataFrameHtmlData.tableDefinitions()
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap("df1"))
                        .plus(df2.toColoredHTML().wrap("df2"))
                        .plus(
                            df1.innerJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
                                .toColoredHTML().wrap("result")
                        )
                        .wrapRow()
                )
                .plus(other)
        )
    }

    @TransformDataFrameExpressions
    @Test
    fun compareLeft() {
        // SampleStart
        df1.leftJoin(df2, "index", "age")
        df1.leftJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
        // SampleEnd

        PluginCallbackProxy.overrideHtmlOutput(
            manualOutput = DataFrameHtmlData.tableDefinitions()
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap("df1"))
                        .plus(df2.toColoredHTML().wrap("df2"))
                        .plus(df1.leftJoin(df2, "index", "age").toColoredHTML().wrap("result"))
                        .wrapRow()
                )
                .plus(DataFrameHtmlData(body = "<br><br>"))
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap())
                        .plus(df2.toColoredHTML().wrap())
                        .plus(
                            df1.leftJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
                                .toColoredHTML().wrap()
                        )
                        .wrapRow()
                )
                .plus(other)
        )
    }

    @TransformDataFrameExpressions
    @Test
    fun compareRight() {
        // SampleStart
        df1.rightJoin(df2, "index", "age")
        df1.rightJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
        // SampleEnd

        PluginCallbackProxy.overrideHtmlOutput(
            manualOutput = DataFrameHtmlData.tableDefinitions()
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap("df1"))
                        .plus(df2.toColoredHTML().wrap("df2"))
                        .plus(df1.rightJoin(df2, "index", "age").toColoredHTML().wrap("result"))
                        .wrapRow()
                )
                .plus(DataFrameHtmlData(body = "<br><br>"))
                .plus(
                    DataFrameHtmlData()
                        .plus(df1.toColoredHTML().wrap())
                        .plus(df2.toColoredHTML().wrap())
                        .plus(
                            df1.rightJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
                                .toColoredHTML().wrap()
                        )
                        .wrapRow()
                )
                .plus(other)
        )
    }

    private fun DataFrameHtmlData.wrapRow(): DataFrameHtmlData =
        copy(
            body =
            """
                <div class="table-row">
                    $body
                </div>
                """.trimIndent(),
        )

    private val other = DataFrameHtmlData(
        style =
        """
            body {
                font-family: "JetBrains Mono", SFMono-Regular, Consolas, "Liberation Mono", Menlo, Courier, monospace;
                font-size: 14px;
            }

            :root {
                color: #19191C;
                background-color: #fff;
            }
            
            :root[theme="dark"] {
                background-color: #19191C;
                color: #FFFFFFCC
            }
            
            .table-row {
                display: flex;
                align-items: flex-start;
                overflow-x: auto;
            }
            
            .table-container:not(:last-child) {
                margin-right: 20px; 
            }
            
            td {
                white-space: nowrap;
            }
            """.trimIndent(),
    )
}
