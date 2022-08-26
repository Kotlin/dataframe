package org.jetbrains.kotlinx.dataframe.unit

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacadeImpl
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.jetbrains.kotlinx.dataframe.plugin.explodeImpl
import org.jetbrains.kotlinx.dataframe.plugin.pluginSchema
import org.jetbrains.kotlinx.dataframe.plugin.print
import org.jetbrains.kotlinx.dataframe.plugin.toColumnPath
import org.jetbrains.kotlinx.dataframe.runners.DataFrameUnitTests
import org.junit.jupiter.api.Test

private val defaultExplodeColumns: ColumnsSelector<*, *> = { dfs { it.isList() || it.isFrameColumn() } }

fun <T> DataFrame<T>.explodeTest(
    facade: KotlinTypeFacade,
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns
): DataFrame<T> {
    val df = this
    println("Before runtime")
    schema().print()
    println("Before compile")
    facade.pluginSchema(this).print()
    val runtime = explode(dropEmpty, selector)
    println()
    println()
    println("Runtime")
    runtime.schema().print()
    println("Compile")
    val compile = facade.run { pluginSchema(df).explodeImpl(dropEmpty, selector.toColumnPath(df)) }
    compile.print()
    return runtime
}

val df = dataFrameOf("name", "age", "city", "weight")(
    "Alice", 15, "London", 54,
    "Bob", 45, "Dubai", 87,
    "Charlie", 20, "Moscow", null,
    "Charlie", 40, "Milan", null,
    "Bob", 30, "Tokyo", 68,
    "Alice", 20, null, 55,
    "Charlie", 30, "Moscow", 90
)

@Suppress("IncorrectFormatting")
class Explode1 : DataFrameUnitTests({ session, _ ->
    val grouped = df
        .filter { it["city"] != null }
        .remove("age", "weight")
        .groupBy("city")
        .toDataFrame()

    val facade = KotlinTypeFacadeImpl(session)
    grouped.explodeTest(facade) { it["group"] }
}) {

    @Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

class Explode2 : DataFrameUnitTests(::explode2) {

    // null in frame column
    //@Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

fun explode2(session: FirSession, call: FirFunctionCall) {
    val grouped = df.groupBy("city")
    val groupCol = grouped.groups.toColumnAccessor()
    val plain = grouped
        .toDataFrame()
        .update { groupCol }.at(1).withNull() // shouldn't happen
        .update { groupCol }.at(2).with { emptyDataFrame() }
        .update { groupCol }.at(3).with { it.filter { false } }

    val facade = KotlinTypeFacadeImpl(session)
    plain.explodeTest(facade = facade, dropEmpty = false) { it["group"] }
}

class Explode3 : DataFrameUnitTests(::explode3) {
    @Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

fun explode3(session: FirSession, call: FirFunctionCall) {
    val df = dataFrameOf("packageName", "files")(
        "org.jetbrains.kotlinx.dataframe.api", listOf("add.kt", "addId.kt", "all.kt"),
        "org.jetbrains.kotlinx.dataframe.io", listOf("common.kt", "csv.kt", "guess.kt")
    )

    val facade = KotlinTypeFacadeImpl(session)
    df.explodeTest(facade = facade, dropEmpty = false) { it["files"] }
}

class Explode4 : DataFrameUnitTests(::explode4) {
    @Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

fun explode4(session: FirSession, call: FirFunctionCall) {

}

class Explode5 : DataFrameUnitTests(::explode5) {
    @Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

fun explode5(session: FirSession, call: FirFunctionCall) {

}
