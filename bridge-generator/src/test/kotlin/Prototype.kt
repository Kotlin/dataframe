import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.GenerateConstructor
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.pluginJsonFormat
import org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender.toPluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.jupiter.api.Test
import java.util.*

class Prototype {

    /**
     * also this:
     * @see DataFrame.distinctBy
     */
    @Test
    fun `print link to the sources`() {
        println(".(mappings.kt:5)")
        println(".(move.kt:5)")
        println("kotlin/org/jetbrains/kotlinx/dataframe/plugin/ColumnAccessorApproximation.kt :10")
        println("/home/nikitak/IdeaProjects/dataframe/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/plugin/ColumnAccessorApproximation.kt :10")

    }

    @DataSchema
    interface Function : DataRowSchema {
        val receiverType: String
        val function: String
        val functionReturnType: String
        val parameters: List<Parameter>

        @GenerateConstructor
        companion object
    }

    @DataSchema
    interface Parameter : DataRowSchema {
        val name: String
        val returnType: String
        val defaultValue: String?

        @GenerateConstructor
        companion object
    }

    val id by column<Int>()

    val otherFunctions = dataFrameOf(
        Function("DataFrame<T>", "insert", "InsertClause<T>", listOf(Parameter("column", "DataColumn<C>", null))),
        Function("DataFrame<T>", "insert", "InsertClause<T>", listOf(
            Parameter("name", "String", null),
            Parameter("infer", "Infer", "Infer.Nulls"),
            Parameter("expression", "RowExpression<T, R>", null)
        )),
        Function("DataFrame<T>", "insert", "InsertClause<T>", listOf(
            Parameter("column", "ColumnAccessor<R>", null),
            Parameter("infer", "Infer", "Infer.Nulls"),
            Parameter("expression", "RowExpression<T, R>", null)
        )),
        Function("DataFrame<T>", "insert", "InsertClause<T>", listOf(
            Parameter("column", "KProperty<R>", null),
            Parameter("infer", "Infer", "Infer.Nulls"),
            Parameter("expression", "RowExpression<T, R>", null)
        )),
    )

    val dfFunctions = dataFrameOf(
    /**
     * @see InsertClause.under
     */
        Function("InsertClause<T>", "under", "DataFrame<T>", listOf(
            Parameter("column", "ColumnSelector<T, *>", null)
        )),
        Function("InsertClause<T>", "under", "DataFrame<T>", listOf(
            Parameter("columnPath", "ColumnPath", null)
        )),
        Function("InsertClause<T>", "under", "DataFrame<T>", listOf(
            Parameter("column", "ColumnAccessor<*>", null)
        )),
        Function("InsertClause<T>", "under", "DataFrame<T>", listOf(
            Parameter("column", "KProperty<*>", null)
        )),
        Function("InsertClause<T>", "under", "DataFrame<T>", listOf(
            Parameter("column", "String", null)
        ))
    )

    val functions = (otherFunctions concat dfFunctions)
        .groupBy { function }
        .updateGroups { it.addId() }
        .concat()
        .update { parameters }.with { it.append(Parameter("<this>", receiverType, null)) }

    // region classes

    @DataSchema
    interface ClassDeclaration : DataRowSchema {
        val name: String
        val parameters: List<Parameter>

        @GenerateConstructor
        companion object
    }

    // "val df: DataFrame<T>, val column: AnyCol"
    val classes = dataFrameOf(
        ClassDeclaration("InsertClause<T>", listOf(
            Parameter("df", "DataFrame<T>", null),
            Parameter("column", "AnyCol", null),
        )),
    )

    // endregion

    val returnType by column<String>()

    val uniqueReturnTypes = functions
        .explode { parameters }
        .ungroup { parameters }[returnType]
        .concat(functions.functionReturnType)
        .concat(
            classes
                .select { parameters }
                .explode { parameters }
                .ungroup { parameters }[returnType]
        )
        .concat(classes.name.distinct())
        .distinct()

    @Test
    fun `generate bridges`() {
        val mapper = uniqueReturnTypes.toList()
            .joinToString(
                ",\n\t",
                prefix = "private val bridges = dataFrameOf(\n\t",
                "\n)"
            ) {
                val reference = mapping[it]
                    ?.get(approximation)?.let { simpleName -> "\"$simpleName\"" }
                    ?: "null"

                val converter = mapping[it]
                    ?.get(converter)?.let { simpleName -> "\"$simpleName\"" }
                    ?: "null"
                "Bridge(\"$it\", $reference, $converter)"
            }
        println(mapper)
    }

    // region bridges
    val type by column<String>()
    val approximation by column<String>()
    val converter by column<String>()

//    @DataSchema
//    interface Bridge : DataRowSchema {
//        val type: String
//        val approximation: String
//        val converter: String
//        val lens: String
//        val supported: Boolean
//
//        @GenerateConstructor
//        companion object
//    }

    @DataSchema
    class Bridge(val type: String,
                 val approximation: String,
                 val converter: String,
                 val lens: String,
                 val supported: Boolean = false) : DataRowSchema

//    @DataSchema
//    interface ValueExample : DataRowSchema {
//        val constructor: String?
//        val usage: String
//
//        @GenerateConstructor
//        companion object
//    }

    private val bridges = dataFrameOf(
        Bridge("DataColumn<C>", "SimpleCol", "dataColumn", "Value"),
        Bridge("DataFrame<T>", "PluginDataFrameSchema", "dataFrame", "Schema", true),
        Bridge("String", "String", "string", "Value", true),
        Bridge("Infer", "Infer", "enum", "Value", true),
        Bridge("RowExpression<T, R>", "TypeApproximation", "type", "ReturnType", true),
        Bridge("ColumnAccessor<R>", "ColumnAccessorApproximation", "columnAccessor", "Value"),
        Bridge("KProperty<R>", "KPropertyApproximation", "kproperty", "Value", true),
        Bridge("ColumnSelector<T, *>", "ColumnWithPathApproximation", "columnWithPath", "Value"),
        Bridge("InsertClause<T>", "InsertClauseApproximation", "insertClause", "Value", true),
        Bridge("ColumnPath", "ColumnPathApproximation", "columnPath", "Value"),
        Bridge("ColumnAccessor<*>", "ColumnAccessorApproximation", "columnAccessor", "Value"),
        Bridge("KProperty<*>", "KPropertyApproximation", "kproperty", "Value", true),
        Bridge("AnyCol", "SimpleCol", "dataColumn", "Value")
    )


    private val mapping = bridges.associateBy { it["type"] }
    private fun findBridge(type: String) = mapping[type]

    // endregion

    /**
     * @see ConvertInterpreter
     */
    @Test
    fun `generate interpreters`() {
        `generate interpreters`(functions, bridges)
    }

    private fun `generate interpreters`(functions: DataFrame<Function>, bridges: DataFrame<Bridge>) {
        println(functions)

        val function by column<String>()
        val functionReturnType by column<String>()

        functions
            .leftJoin(bridges) { it[functionReturnType].match(type) }
            .convert { parameters }.with { it.leftJoin(bridges) { it[returnType].match(type) } }
            .schema()
            .print()

        val interpreters = functions
            .leftJoin(bridges) { it[functionReturnType].match(type) }
            .convert { parameters }.with { it.leftJoin(bridges) { it[returnType].match(type) } }
            .convert { parameters }.with {
                it.add("arguments") {
                    val (name, runtimeName) = if (name == "<this>") {
                        "receiver" to "THIS"
                    } else {
                        name to ""
                    }

                    "val Arguments.${name}: ${approximation()} by ${converter()}($runtimeName)"
                }
            }
            .add("argumentsStr") {
                // generate deprecated property with name argumentStr
                it.parameters["arguments"].values().joinToString("\n") { "|   $it" }
            }
            .add("interpreterName") {
                val name = it[function].replaceFirstChar { it.uppercaseChar() }
                "$name${it[id]}"
            }
            .mapToColumn("interpreters") {
                """
                    |internal class ${it["interpreterName"]} : AbstractInterpreter<${approximation()}>() {
                        ${it["argumentsStr"]}
                    |    
                    |    override fun Arguments.interpret(): ${approximation()} {
                    |        TODO()
                    |    }
                    |}
                    """.trimMargin()
            }
        println(interpreters.values().joinToString("\n\n"))
    }

    @Test
    fun `generate approximations`() {
        val df = classes
            .leftJoin(bridges) { name.match(right.type) }
            .convert { parameters }.with {
                it.leftJoin(bridges) { returnType.match(right.type) }
            }
            .add("code") {
                val parameters = parameters.rows().joinToString(", ") { "val ${it.name}: ${it["approximation"]}" }
                "internal class ${approximation()}($parameters)"
            }

        println(df["code"].toList())

    }

    @Test
    fun `generate tests stubs`() {
        bridges
            .distinctBy { expr { type.substringBefore("<") } }
            .filter { supported }
            .forEach {
                println(it.converter)
                println()
                val titleCaseConverter =
                    converter.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                println("""
                    package org.jetbrains.kotlinx.dataframe.plugin.testing
                    
                    import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
                    import org.jetbrains.kotlinx.dataframe.annotations.Arguments
                    import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
                    import org.jetbrains.kotlinx.dataframe.plugin.*
                    
                    @Interpretable(${titleCaseConverter}Identity::class)
                    public fun $converter(v: $type): $type {
                        return v
                    }
                    
                    public class ${titleCaseConverter}Identity : AbstractInterpreter<$approximation>() {
                        internal val Arguments.v: $approximation by $converter()
                        
                        override fun Arguments.interpret(): $approximation {
                            return v
                        }
                    }
                    
                    internal fun ${converter}Test() {
                        
                    }
                """.trimIndent())
                println()
            }
    }

    //dataFrameOf\((".+",?)+\)\(.*\)

    @Test
    fun printFunctionsThatShouldWork() {
        val supportedFunctions = functions
            .leftJoin(bridges) { it[functionReturnType].match(type) }
            .convert { parameters }.with { it.leftJoin(bridges) { it[returnType].match(type) } }
            .also {
                it.forEach {
                    parameters.print()
                }
            }
            .filter {
                it.parameters.all { it[Bridge::supported] }
            }

        supportedFunctions
            .remove(Bridge::supported, Bridge::lens, Bridge::converter, Bridge::approximation)
            .convert { parameters }.with { df ->
                df.map {
                    val default = it.defaultValue?.let { " = ${it}" } ?: ""
                    "${it.name}: ${it.returnType}$default"
                }
            }
            .print(valueLimit = 500)
    }

    @Test
    fun test() {
        val dfExpression = """dataFrameOf("test")(123)"""
        val repl = object : JupyterReplTestCase() {

        }
        val functionCall = """df.insert("age") { 42 }.under("test")"""
        //val df = dataFrameOf("test")(123)
        val df = repl.exec<DataFrame<*>>("val df = $dfExpression; df")
        val df1 = repl.exec("""
                try {
                    $functionCall
                } catch (e: Exception) {
                    e
                }
            """.trimIndent())
//        when (df1) {
//            is DataFrame<*> ->
//
//        }
    }

    @Test
    fun `schema tests`() {
        generateSchemaTestStub("DataFrame.readJson(\"functions.json\")")
    }

    private fun generateSchemaTestStub(s: String) {
        val repl = object : JupyterReplTestCase() {

        }
        val df = repl.execRaw("val df = $s; df") as DataFrame<*>
        val expressions = mutableListOf<String>()
        var i = 0
        fun accept(s: String, columns: Map<String, ColumnSchema>) {
            columns.forEach { (t, u) ->
                when (u) {
                    is ColumnSchema.Frame -> {
                        accept("${s}.$t[0]", u.schema.columns)
                    }

                    is ColumnSchema.Value -> {
                        val funName = "col${i}"
                        expressions.add("fun $funName(v: ${u.type.toString()}) {}")
                        i++
                        expressions.add("${funName}(${s}.$t[0])")
                    }

                    is ColumnSchema.Group -> {
                        accept("${s}.$t", u.schema.columns)
                    }
                }
            }
        }

        val generator = CodeGenerator.create(useFqNames = false)
        val declaration =
                generator.generate(df.schema(), name = "Schema1", fields = true, extensionProperties = true, isOpen = true)
                    .code.declarations
                    .replace(Regex("@JvmName\\(.*\"\\)"), "")
        accept("df", df.schema().columns)
        val schemaTestCode = expressions.joinToString("\n")
        println(schemaTestCode)
        repl.exec(schemaTestCode)

        val pluginSchema = df.schema().toPluginDataFrameSchema()

        val jsonString = pluginJsonFormat.encodeToString(pluginSchema)
        val decodedSchema = pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(jsonString)

        assert(decodedSchema == pluginSchema)
        println("")
        println(jsonString)
        println()
        bridges.first { it.type == "DataFrame<T>" }.run {
            println("""
                        package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender
                        
                        import kotlinx.serialization.decodeFromString
                        import org.jetbrains.kotlinx.dataframe.DataFrame
                        import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
                        import org.jetbrains.kotlinx.dataframe.annotations.Arguments
                        import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
                        import org.jetbrains.kotlinx.dataframe.plugin.*
                        
                        @Interpretable(Schema1::class)
                        public fun schema1(): DataFrame<*> {
                            return TODO("won't run")
                        }
                        
                        public class Schema1 : AbstractInterpreter<$approximation>() {
                            override fun Arguments.interpret(): $approximation {
                                return pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(
                                    ${"\"\"\""}$jsonString${"\"\"\""}
                                )
                            }
                        }
                """.trimIndent())
        }
        val schemaDeclaration = declaration.lineSequence().joinToString("\n|")
        println("schema1")
        println()
        println("""
                |import org.jetbrains.kotlinx.dataframe.*
                |import org.jetbrains.kotlinx.dataframe.api.*
                |import org.jetbrains.kotlinx.dataframe.annotations.*
                |import org.jetbrains.kotlinx.dataframe.plugin.testing.*
                |import org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender.*
                |
                |/*
                |$schemaDeclaration
                |*/
                |
                |internal fun schemaTest() {
                |    val df = schema1()
                |    ${expressions.joinToString("\n|    ")}
                |}
            """.trimMargin())
    }

    val expressions = listOf(
        """dataFrameOf("age")(17)""",
        """dataFrameOf("name", "age")("Name", 17).group("name", "age").into("person")""",
        """dataFrameOf("persons")(dataFrameOf("name", "age")("Name", 17).group("name", "age").into("person"))"""
    )

    @DataSchema
    interface TestCase : DataRowSchema {
        val dfExpression: String
        val functionCalls: List<String>

        @GenerateConstructor
        companion object
    }

    val expressions1 = dataFrameOf(
        TestCase("""dataFrameOf("age", "name")(17, "Name")""", listOf(
            """df.insert("col1") { 42 }.after("age")""",
            """df.insert("col1") { 42 }.after("name")"""
        )),
    )

    fun wfetest() {
        val repl = object : JupyterReplTestCase() {

        }
        val df = expressions.toDataFrame {
            "expression" from { it }
            "df" from { repl.exec<DataFrame<*>>(it) }
        }.add {
            "schema" from {
                "df"<DataFrame<*>>().schema()
            }
        }
    }
}

