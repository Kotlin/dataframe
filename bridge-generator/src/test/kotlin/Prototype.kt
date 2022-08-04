import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.GenerateConstructor
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.pluginJsonFormat
import org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender.toPluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.jupiter.api.Test
import java.io.File
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
    class Function(
        val receiverType: String,
        val function: String,
        val functionReturnType: Type,
        val parameters: List<Parameter>
    ) : DataRowSchema {
    }

    @DataSchema
    class Parameter(
        val name: String,
        val returnType: Type,
        val defaultValue: String?,
    ) : DataRowSchema

    @DataSchema
    data class Type(val name: String, val vararg: Boolean)

    val otherFunctions = dataFrameOf(
        Function("DataFrame<T>", "insert", Type("InsertClause<T>", false), listOf(Parameter("column", Type("DataColumn<C>", false), null))),
        Function("DataFrame<T>", "insert", Type("InsertClause<T>", false), listOf(
            Parameter("name", Type("String", false), null),
            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
            Parameter("expression", Type("RowExpression<T, R>", false), null)
        )),
        Function("DataFrame<T>", "insert", Type("InsertClause<T>", false), listOf(
            Parameter("column", Type("ColumnAccessor<R>", false), null),
            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
            Parameter("expression", Type("RowExpression<T, R>", false), null)
        )),
        Function("DataFrame<T>", "insert", Type("InsertClause<T>", false), listOf(
            Parameter("column", Type("KProperty<R>", false), null),
            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
            Parameter("expression", Type("RowExpression<T, R>", false), null)
        )),
    )

    val dfFunctions = dataFrameOf(
        /**
         * @see InsertClause.under
         */
        Function("InsertClause<T>", "under", Type("DataFrame<T>", false), listOf(
            Parameter("column", Type("ColumnSelector<T, *>", false), null)
        )),
        Function("InsertClause<T>", "under", Type("DataFrame<T>", false), listOf(
            Parameter("columnPath", Type("ColumnPath", false), null)
        )),
        Function("InsertClause<T>", "under", Type("DataFrame<T>", false), listOf(
            Parameter("column", Type("ColumnAccessor<*>", false), null)
        )),
        Function("InsertClause<T>", "under", Type("DataFrame<T>", false), listOf(
            Parameter("column", Type("KProperty<*>", false), null)
        )),
        Function("InsertClause<T>", "under", Type("DataFrame<T>", false), listOf(
            Parameter("column", Type("String", false), null)
        )),
//        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            Parameter("columns", Type("AnyBaseCol", vararg = true), null)
//        )),
//        Function("DataFrame<T>", "addAll", Type("DataFrame<T>", false), listOf(
//            Parameter("columns", Type("Iterable<AnyBaseCol>", false), null)
//        )),
//        Function("DataFrame<T>", "addAll", Type("DataFrame<T>", false), listOf(
//            Parameter("dataFrames", Type("AnyFrame", vararg = true), null)
//        )),
        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
            Parameter("name", Type("String", false), null),
            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
            Parameter("expression", Type("AddExpression<T, R>", false), null),
        )),
//        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            Parameter("property", Type("KProperty<R>", false), null),
//            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
//        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            Parameter("column", Type("ColumnAccessor<R>", false), null),
//            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
//        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            Parameter("path", Type("ColumnPath", false), null),
//            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
        Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
            Parameter("columns", Type("ColumnsSelector<T, C>", false), null)
        )),
        Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
            Parameter("columns", Type("KProperty<C>", true), null)
        )),
        Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
            Parameter("columns", Type("String", true), null)
        )),
    )

    val convert = dataFrameOf(
        Function("Convert<T, C>", "to", Type("DataFrame<T>", false), emptyList())
    )

    private val functions = (otherFunctions concat dfFunctions).appendReceiverAndId()

    fun DataFrame<Function>.appendReceiverAndId() = groupBy { function }
        .updateGroups { it.addId() }
        .concat()
        .update { parameters }.with {
            it.append(Parameter("receiver", Type(receiverType, false), null))
        }

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
            Parameter("df", Type("DataFrame<T>", false), null),
            Parameter("column", Type("AnyCol", false), null),
        )),
    )

    // endregion

    val uniqueReturnTypes: ColumnGroup<Type> = functions.collectUsedTypes(classes)

    @Test
    fun `class cast exception`() {
        val returnType by column<Type>()
        // when actual returnType is in fact ColumnGroup<Type>, not DataColumn<Type>
        // compiler doesn't complain, but it's an error
        val res = functions
            .explode { parameters }
            .ungroup { parameters }[returnType]
        res.map { it.name }

        // and we can do this, still no error
        //res.concat(functions.functionReturnType.toListOf<Type>().toColumn(""))
    }

    @Test
    fun `generate bridges`() {
        uniqueReturnTypes.joinBridges(bridges, verify = true)
    }

    @Test
    fun `join removes column from column group`() {
        val a by columnOf(1)
        val group by columnOf(a)

        val groupReference by columnGroup("group")
        val aa by groupReference.column<Int>("a")

        val df = dataFrameOf(a)
        val df1 = dataFrameOf(group)
        val res = df.join(df1) {
            "a".match(aa.map { it })
        }

        val res1 = df.join(df1) {
            "a".match(aa)
        }
        println(res1.schema())
    }

    @DataSchema
    class Data(val a: Int)
    class Record(val data: Data)
    @Test
    fun `cast and convert error should print schema maybe`() {
        val b by columnOf(1)
        val data by columnOf(b)
        dataFrameOf(data).cast<Record>(verify = true)
    }

    // region bridges
    val type by column<String>()

    @DataSchema
    class Bridge(val type: Type,
                 val approximation: String,
                 val converter: String,
                 val lens: String,
                 val supported: Boolean = false) : DataRowSchema

    private val bridges by lazy { DataFrame.readJson("bridges.json").cast<Bridge>(verify = true) }

    val refinedFunctions = functions.refine(bridges)

    @DataSchema
    class RefinedFunction(
        val receiverType: String,
        val function: String,
        val functionReturnType: Type,
        val parameters: List<Parameter>,
        val startingSchema: Parameter
    ) : DataRowSchema

    // endregion

    /**
     * @see ConvertInterpreter
     */
    @Test
    fun `generate interpreters`() {
        refinedFunctions.generateInterpreters()
    }

    @Test
    fun `generate approximations`() {
        val approximation by column<String>()
        val df = classes
            .leftJoin(bridges) { name.match(right.type.name) }
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
    fun `generate atoms tests`() {
        `generate atoms tests`(bridges)
    }

    fun `generate atoms tests`(bridges: DataFrame<Bridge>) {

        fun writeTestStub(name: String, s: String) {
            // val root = TODO()
            val atoms = File(root, "kotlin/org/jetbrains/kotlinx/dataframe/plugin/testing/atoms").also { it.mkdirs() }
            File(atoms, "$name.kt").writeText(s)
        }

        val name by column<String>()
        bridges
            .distinctBy { expr { type.name.substringBefore("<") } and type.vararg }
            .groupBy { converter }
            .updateGroups { df ->
                add(name) {
                    var name = type.name.substringBefore("<")
                    if (type.vararg) {
                        name = "Vararg$name"
                    }
                    name
                }
            }
            .concat()
            .filter { supported }
            .forEach {
                val testSubjectName = name().replaceFirstChar { it.lowercase() }
                println(name())
                println()
                val interpreterName = name()
                writeTestStub(testSubjectName, """
                    package org.jetbrains.kotlinx.dataframe.plugin.testing
                    
                    import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
                    import org.jetbrains.kotlinx.dataframe.annotations.Arguments
                    import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
                    import org.jetbrains.kotlinx.dataframe.plugin.*
                    
                    @Interpretable(${interpreterName}Identity::class)
                    public fun ${testSubjectName}(v: ${type.name}): ${type.name} {
                        return v
                    }
                    
                    public class ${interpreterName}Identity : AbstractInterpreter<$approximation>() {
                        internal val Arguments.v: $approximation by $converter()
                        
                        override fun Arguments.interpret(): $approximation {
                            return v
                        }
                    }
                    
                    internal fun ${testSubjectName}Test() {
                        
                    }
                """.trimIndent())
                println()
            }
    }

    //dataFrameOf\((".+",?)+\)\(.*\)

    @Test
    fun printFunctionsThatShouldWork() {
        val supportedFunctions = functions
            .leftJoin(bridges) { functionReturnType.name.match(right.type.name) }
            .convert { parameters }.with {
                it.leftJoin(bridges) {
                    it.returnType.name.match<String>(right.type.name)
                }
            }
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
    fun `schema1 test`() {
        generateSchemaTestStub("schema1", "DataFrame.readJson(\"functions.json\")")
    }

    @Test
    fun `schema2 test`() {
        generateSchemaTestStub("schema2", """run {
            |val name by columnOf("name")
            |val returnType by columnOf("")
            |val df = dataFrameOf(name, returnType)
            |val functions by columnOf(df)
            |val function by columnOf(name, returnType)
            |val nestedGroup by columnOf(name)
            |val group by columnOf(nestedGroup)
            |dataFrameOf(name, functions, function, group)
            |}""".trimMargin())
    }

    fun Map<String, ColumnSchema>.accept(
        s: String,
    ): List<String> {
        var i = 0
        val expressions = mutableListOf<String>()
        fun acceptInt(
            s: String,
            columns: Map<String, ColumnSchema>,
            expressions: MutableList<String>,
        ) {
            columns.forEach { (t, u) ->
                when (u) {
                    is ColumnSchema.Frame -> {
                        acceptInt("${s}.$t[0]", u.schema.columns, expressions)
                    }

                    is ColumnSchema.Value -> {
                        val funName = "col${i}"
                        expressions.add("fun $funName(v: ${u.type.toString()}) {}")
                        i++
                        expressions.add("${funName}(${s}.$t[0])")
                    }

                    is ColumnSchema.Group -> {
                        acceptInt("${s}.$t", u.schema.columns, expressions)
                    }
                }
            }
        }
        acceptInt(s, this, expressions)
        return expressions.toList()
    }
    val root = File("build/tmp/prototype")
    private fun generateSchemaTestStub(name: String, expression: String) {

        fun writeInterpreter(s: String) {
            // val root = File("/home/nikitak/IdeaProjects/dataframe/core/src/main")
            val schemaRender = File(root, "kotlin/org/jetbrains/kotlinx/dataframe/plugin/testing/schemaRender").also { it.mkdirs() }
            File(schemaRender, "$name.kt").writeText(s)
        }

        fun writeTestStub(s: String) {
            // val root = File("/home/nikitak/Downloads/kotlin/plugins/kotlin-dataframe")
            val schemaRender = File(root, "testData/diagnostics/schemaRender").also { it.mkdirs() }
            File(schemaRender, "$name.kt")
        }

        val capitalizedName = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        val repl = object : JupyterReplTestCase() {
        }
        val df = repl.execRaw("val df = $expression; df") as DataFrame<*>
        val declaration = df.generateSchemaDeclaration(capitalizedName)
        val schemaTestCode = df.generateTestCode()
        repl.exec(schemaTestCode)
        val pluginSchema = df.pluginSchema()
        val jsonString = pluginSchema.toJson()


        bridges.first { it.type.name == "DataFrame<T>" }.run {
            writeInterpreter("""
                        package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender
                        
                        import kotlinx.serialization.decodeFromString
                        import org.jetbrains.kotlinx.dataframe.DataFrame
                        import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
                        import org.jetbrains.kotlinx.dataframe.annotations.Arguments
                        import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
                        import org.jetbrains.kotlinx.dataframe.plugin.*
                        
                        @Interpretable(${capitalizedName}::class)
                        public fun $name(): DataFrame<*> {
                            return TODO("won't run")
                        }
                        
                        public class ${capitalizedName} : AbstractInterpreter<$approximation>() {
                            override fun Arguments.interpret(): $approximation {
                                return pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(
                                    ${"\"\"\""}$jsonString${"\"\"\""}
                                )
                            }
                        }
                """.trimIndent())
        }
        val schemaDeclaration = declaration.lineSequence().joinToString("\n|")
        writeTestStub("""
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
                |    val df = $name()
                |    ${schemaTestCode}
                |}
            """.trimMargin())
    }

    private fun DataFrame<*>.generateTestCode(): String {
        val expressions = schema().columns.accept("df")
        return expressions.joinToString("\n")
    }

    @Test
    fun generateDfFunctionTestStub() {
        val schemaName = "Add0"
        val expression = "dataFrameOf(\"a\")(1)"
        val modify = "add(\"\") { 42 }"
        val id = "add0"
        generateDfFunctionTestStub(expression, schemaName, modify, id, "add.kt")
    }

    private fun generateDfFunctionTestStub(expression: String, schemaName: String, modify: String, id: String, file: String) {
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

        printCompilerTest(file, schemaName, declarationBefore, beforePluginSchema, schemaTestCode, modify, afterPluginSchema.toJson(), id)
    }

    private fun printCompilerTest(file: String, schemaName: String, schemaDeclaration: String, before: String, schemaTestCode: String, modify: String, after: String, id: String) {
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
        println()
        println("""
            "${id}_schema" to pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(${"\"\"\""}$before${"\"\"\""}),
            "$id" to pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(${"\"\"\""}$after${"\"\"\""}),
        """.trimIndent())
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

    @Test
    fun `convert APIs`() {
        val convert = convert.appendReceiverAndId()
        val types = convert.collectUsedTypes(classes.take(1))
        val rawBridges = types.joinBridges(bridges, verify = false)
        val path = "convert_bridges.json"
        File(path).let {
            if (!it.exists()) {
                rawBridges.writeJson(it, prettyPrint = true)
            }
        }

        val editedBridges = DataFrame.readJson(path).cast<Bridge>(verify = true)
        val allBridges = bridges.concat(editedBridges).distinct().cast<Bridge>(verify = true)
        allBridges.writeJson("bridges.json", prettyPrint = true)

        val refine = convert
            .refine(allBridges)
            //.also { println(it.schema()) }
            .convertTo<RefinedFunction>()
            //.also { println(it.schema()) }
            //.cast<RefinedFunction>(verify = true)

        refine.generateInterpreters()
    }
}

private val returnType by columnGroup<Prototype.Type>()
fun DataFrame<Prototype.Function>.collectUsedTypes(
    classes: DataFrame<Prototype.ClassDeclaration> = emptyDataFrame()
) = explode { parameters }
    .ungroup { parameters }[returnType]
    .concat(functionReturnType)
    .concat(
        classes
            .select { parameters }
            .explode { parameters }
            .ungroup { parameters }[returnType]
    )
    .concat(classes.name.distinct().map { Prototype.Type(it, false) }.asIterable().toDataFrame())
    .distinct()
    .asColumnGroup("type")

fun ColumnGroup<Prototype.Type>.joinBridges(bridges: DataFrame<Prototype.Bridge>, verify: Boolean): DataFrame<Prototype.Bridge> {
    val df = dataFrameOf(this)
        .leftJoin(bridges) {
            // join keeps only left column!!
            "type" match right.type
        }
        //.rename { "type"["type"] }.into("name")
        .fillNulls("supported").with { false }
        .remove("name", "vararg")
        .cast<Prototype.Bridge>(verify = verify)

    return df
}

fun DataFrame<Prototype.Function>.refine(bridges: DataFrame<Prototype.Bridge>): DataFrame<Prototype.RefinedFunction> {
    val functions = this
    return functions.leftJoin(bridges) {
        //functions.functionReturnType.match(type) TODO: Shouldn't compile
        functions.functionReturnType.name.match(right.type.name)
    }
        .convert { parameters }.with { it.leftJoin(bridges) { it[returnType].name.match(right.type.name) } }
        .add("startingSchema") {
            parameters.first { it.name == "receiver" }
        }
        .cast<Prototype.RefinedFunction>()
}

fun DataFrame<Prototype.RefinedFunction>.generateInterpreters() {
    val approximation by column<String>()
    val converter by column<String>()
    val id by column<Int>()
    println(this)

    val interpreters = convert { parameters }.with {
        it.add("arguments") {
            "val Arguments.$name: ${approximation()} by ${converter()}()"
        }
    }
        .add("argumentsStr") {
            // generate deprecated property with name argumentStr?
            buildString {
                append(it.parameters["arguments"].values().joinToString("\n") { "|  $it" })
                // how to handle nullable property? make all columns nullable? forbid it?
                it.startingSchema.name?.let {
                    appendLine()
                    append("  override val Arguments.startingSchema get() = ${it}")
                }
            }
        }
        .add("interpreterName") {
            val name = it.function.replaceFirstChar { it.uppercaseChar() }
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

fun PluginDataFrameSchema.toJson(): String {
    val afterJson = pluginJsonFormat.encodeToString(this)
    val decodedSchema = pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(afterJson)
    decodedSchema shouldBe this
    return afterJson
}

fun DataFrame<*>.pluginSchema() = schema().toPluginDataFrameSchema()

fun DataFrame<*>.generateSchemaDeclaration(
    capitalizedName: String,
    generator: CodeGenerator = CodeGenerator.create(useFqNames = false)
) = generator.generate(schema(), name = capitalizedName, fields = true, extensionProperties = true, isOpen = true)
        .code.declarations
        .replace(Regex("@JvmName\\(.*\"\\)"), "")

