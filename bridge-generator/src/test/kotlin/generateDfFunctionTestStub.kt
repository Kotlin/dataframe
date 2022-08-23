import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.accept
import org.jetbrains.kotlinx.dataframe.plugin.generateSchemaDeclaration
import org.jetbrains.kotlinx.dataframe.plugin.pluginSchema
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase

internal fun generateDfFunctionTestStub(
    expression: String,
    schemaName: String,
    modify: String,
    id: String,
    file: String
): Pair<PluginDataFrameSchema, PluginDataFrameSchema> {
    val repl = object : JupyterReplTestCase() {
    }
    // region schema before
    val df = repl.execRaw("val df = $expression; df") as DataFrame<*>
    val declarationBefore = df.generateSchemaDeclaration(schemaName)
    val pluginSchema = df.pluginSchema()
    val beforePluginSchema = pluginSchema.toJson()
    // endregion


    val dfRes = repl.execRaw("val df1 = df.$modify; df1") as DataFrame<*>
    val schemaTestCode = dfRes.schema().columns.accept("df1").joinToString("\n")
    repl.exec(schemaTestCode)
    val afterPluginSchema = dfRes.pluginSchema()

    printCompilerTest(file, schemaName, declarationBefore, schemaTestCode, modify, id)

    return pluginSchema to afterPluginSchema
}

private fun printCompilerTest(file: String, schemaName: String, schemaDeclaration: String, schemaTestCode: String, modify: String, id: String) {
    println(file)
    val test = buildString {
        appendLine("""
            import org.jetbrains.kotlinx.dataframe.*
            import org.jetbrains.kotlinx.dataframe.api.*
            import org.jetbrains.kotlinx.dataframe.annotations.*
            import org.jetbrains.kotlinx.dataframe.plugin.testing.*
        """.trimIndent())
        appendLine()
        appendLine(schemaDeclaration)
        appendLine()
        appendLine("""
            fun $id(df: DataFrame<$schemaName>) {
                test(id = "${id}_schema", call = df)
                val df1 = test(id = "$id", call = df.$modify)
        """.trimIndent())
        appendLine(schemaTestCode.prependIndent())
        append("}")
    }
    println(test)
    //println()
    //println("""
    //    "${id}_schema" to pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(${"\"\"\""}$before${"\"\"\""}),
    //    "$id" to pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(${"\"\"\""}$after${"\"\"\""}),
    //""".trimIndent())
}
