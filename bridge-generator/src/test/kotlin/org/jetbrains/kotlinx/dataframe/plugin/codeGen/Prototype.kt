package org.jetbrains.kotlinx.dataframe.plugin.codeGen

import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.addId
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.model.Bridge
import org.jetbrains.kotlinx.dataframe.plugin.model.Function
import org.jetbrains.kotlinx.dataframe.plugin.model.Parameter
import org.jetbrains.kotlinx.dataframe.plugin.model.RefinedFunction
import org.jetbrains.kotlinx.dataframe.plugin.model.*
import org.jetbrains.kotlinx.dataframe.plugin.pluginJsonFormat
import org.jetbrains.kotlinx.dataframe.plugin.resource
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

val bridges by lazy { DataFrame.readJson("/bridges.json".resource()).cast<Bridge>(verify = true) }

val classes = dataFrameOf(
    ClassDeclaration("InsertClause<T>", listOf(
        Parameter("df", Type("DataFrame<T>", false), null),
        Parameter("column", Type("AnyCol", false), null),
    )),
)

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
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("columns", Type("AnyBaseCol", vararg = true), null)
//        )),
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "addAll", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("columns", Type("Iterable<AnyBaseCol>", false), null)
//        )),
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "addAll", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("dataFrames", Type("AnyFrame", vararg = true), null)
//        )),
        Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
            Parameter("name", Type("String", false), null),
            Parameter("infer", Type("Infer", false), "Infer.Nulls"),
            Parameter("expression", Type("AddExpression<T, R>", false), null),
        )),
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("property", Type("KProperty<R>", false), null),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("column", Type("ColumnAccessor<R>", false), null),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
//        org.jetbrains.kotlinx.dataframe.plugin.model.Function("DataFrame<T>", "add", Type("DataFrame<T>", false), listOf(
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("path", Type("ColumnPath", false), null),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("infer", Type("Infer", false), "Infer.Nulls"),
//            org.jetbrains.kotlinx.dataframe.plugin.model.Parameter("expression", Type("AddExpression<T, R>", false), null),
//        )),
    )


    private val functions = (otherFunctions concat dfFunctions).appendReceiverAndId()

    // region classes

    // "val df: DataFrame<T>, val column: AnyCol"

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

    val refinedFunctions = functions.refine(bridges)

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
        bridges.generateAtomsTests()
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
//        generateSchemaTestStub("schema1", "DataFrame.readJson(\"functions.json\")")
    }

    @Test
    fun `schema2 test`() {
//        generateSchemaTestStub("schema2", """run {
//            |val name by columnOf("name")
//            |val returnType by columnOf("")
//            |val df = dataFrameOf(name, returnType)
//            |val functions by columnOf(df)
//            |val function by columnOf(name, returnType)
//            |val nestedGroup by columnOf(name)
//            |val group by columnOf(nestedGroup)
//            |dataFrameOf(name, functions, function, group)
//            |}""".trimMargin())

    }

    val root = File("build/tmp/prototype")



    val expressions = listOf(
        """dataFrameOf("age")(17)""",
        """dataFrameOf("name", "age")("Name", 17).group("name", "age").into("person")""",
        """dataFrameOf("persons")(dataFrameOf("name", "age")("Name", 17).group("name", "age").into("person"))"""
    )

    //    val expressions1 = dataFrameOf(
//        Prototype.TestCase("""dataFrameOf("age", "name")(17, "Name")""", listOf(
//            """df.insert("col1") { 42 }.after("age")""",
//            """df.insert("col1") { 42 }.after("name")"""
//        )),
//    )
}

private val returnType by columnGroup<Type>()
fun DataFrame<Function>.collectUsedTypes(
    classes: DataFrame<ClassDeclaration> = emptyDataFrame()
) = explode { parameters }
    .ungroup { parameters }[returnType]
    .concat(functionReturnType)
    .concat(
        classes
            .select { parameters }
            .explode { parameters }
            .ungroup { parameters }[returnType]
    )
    .concat(classes.name.distinct().map { Type(it, false) }.asIterable().toDataFrame())
    .distinct()
    .asColumnGroup("type")

fun ColumnGroup<Type>.joinBridges(bridges: DataFrame<Bridge>, verify: Boolean): DataFrame<Bridge> {
    val df = dataFrameOf(this)
        .leftJoin(bridges) {
            // join keeps only left column!!
            "type" match right.type
        }
        //.rename { "type"["type"] }.into("name")
        .fillNulls("supported").with { false }
        .remove("name", "vararg")
        .cast<Bridge>(verify = verify)

    return df
}

fun DataFrame<Function>.refine(bridges: DataFrame<Bridge>): DataFrame<RefinedFunction> {
    val functions = this
    return functions.leftJoin(bridges) {
        //functions.functionReturnType.match(type) TODO: Shouldn't compile
        functions.functionReturnType.name.match(right.type.name)
    }
        .convert { parameters }.with { it.leftJoin(bridges) { it.returnType.match(right.type) } }
        .add("startingSchema") {
            parameters.first { it.name == "receiver" }
        }
        .cast<RefinedFunction>()
}

fun DataFrame<RefinedFunction>.generateInterpreters() {
    val approximation by column<String>()
    val converter by column<String>()
    val id by column<Int>()
    println(this)

    val interpreters = convert { parameters }.with {
        it.add("arguments") {
            var defaultValue = it.defaultValue?.let { "defaultValue = Present($it)" }
            if (defaultValue == null && it.returnType.vararg) {
                defaultValue = "defaultValue = Present(emptyList())"
            }
            if (defaultValue == null) {
                defaultValue = ""
            }
            "val Arguments.$name: ${approximation()} by ${converter()}($defaultValue)"
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

fun DataFrame<Function>.appendReceiverAndId() = groupBy { function }
    .updateGroups { it.addId() }
    .concat()
    .update { parameters }.with {
        it.append(Parameter("receiver", Type(receiverType, false), null))
    }

fun DataFrame<Bridge>.generateAtomsTests() {

    fun writeTestStub(name: String, s: String) {
        // val root = TODO()
//        val atoms = File(root, "kotlin/org/jetbrains/kotlinx/dataframe/plugin/testing/atoms").also { it.mkdirs() }
//        File(atoms, "$name.kt").writeText(s)
        println(s)
    }

    val name by column<String>()
    distinctBy { expr { type.name.substringBefore("<") } and type.vararg }
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
                        internal val Arguments.v: $approximation by $converter(lens = Interpreter.$lens)
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
