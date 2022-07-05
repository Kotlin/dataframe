import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.GenerateConstructor
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.InsertClause
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.addId
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.associateBy
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase

//private val mapping = mapOf(
//    DataFrame::class to PluginDataFrameSchema::class,
//    String::class to String::class,
//    KFunction::class to TypeApproximation::class
//)

//@OptIn(ExperimentalReflectionOnLambdas::class, ExperimentalStdlibApi::class)
//internal fun main() {
//    println("Hello, world!")
//
//    val insert1: DataFrame<*>.(String, Infer, RowExpression<*, *>) -> InsertClause<*> = DataFrame<*>::insert
//    val insert2: DataFrame<*>.(String, Infer, RowExpression<*, *>) -> InsertClause<*> = DataFrame<*>::insert
//    val insert3: DataFrame<*>.(String, Infer, RowExpression<*, *>) -> InsertClause<*> = DataFrame<*>::insert
//
////    println(insert1.reflect())
////    insert1.
//
//    val a = { it: String -> 42 }
//    println(a.reflect())
//    println(insert1.reflect())
//    println(::test.javaClass.getAnnotation(Metadata::class.java))
//
//    println(::test.reflect())
//
//    insert1.type().let {
//        it.arguments
//        val wtf = (it.javaType as ParameterizedType).rawType
//
//        wtf as Function<*>
//
//        (it.javaType as Function<*>).reflect()
//    }
//
//    Function4<*, *, *, *, *>::reflect
//}

public inline fun <reified T> T.type(): KType {
    return typeOf<T>()
}

class Prototype {

//    @Test
    fun `load enum from class name + instance name`() {
        (Class.forName("")!! as Class<Infer>).enumConstants

        val forName: Class<*> = Class.forName("")
        java.lang.Enum.valueOf(forName as Class<out Enum<*>>, "")
    }

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
            Parameter("column", "String", null),
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

    val functions = (otherFunctions.concat(dfFunctions))
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

    @DataSchema
    interface Bridge : DataRowSchema {
        val type: String
        val approximation: String
        val converter: String
        val lens: String

        @GenerateConstructor
        companion object
    }

    private val bridges = dataFrameOf(
        Bridge("DataColumn<C>", "SimpleCol", "dataColumn", "Value"),
        Bridge("DataFrame<T>", "PluginDataFrameSchema", "dataFrame", "Schema"),
        Bridge("String", "String", "string", "Value"),
        Bridge("Infer", "Infer", "enum", "Value"),
        Bridge("RowExpression<T, R>", "TypeApproximation", "type", "ReturnType"),
        Bridge("ColumnAccessor<R>", "ColumnAccessorApproximation", "columnAccessor", "Value"),
        Bridge("KProperty<R>", "KPropertyApproximation", "kproperty", "Value"),
        Bridge("ColumnSelector<T, *>", "ColumnWithPathApproximation", "columnWithPath", "Value"),
        Bridge("InsertClause<T>", "InsertClauseApproximation", "insertClause", "Value"),
        Bridge("ColumnPath", "ColumnPathApproximation", "columnPath", "Value"),
        Bridge("ColumnAccessor<*>", "ColumnAccessorApproximation", "columnAccessor", "Value"),
        Bridge("KProperty<*>", "KPropertyApproximation", "kproperty", "Value"),
        Bridge("AnyCol", "SimpleCol", "dataColumn", "Value")
    )


    private val mapping = bridges.associateBy { it["type"] }
    private fun findBridge(type: String) = mapping[type]

    // endregion

    /**
     * @see ConvertInterpreter
     */
    @Test
    fun `generate adapters`() {
        `generate adapters`(functions, bridges)
    }

    private fun `generate adapters`(functions: DataFrame<Function>, bridges: DataFrame<Bridge>) {
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
}

