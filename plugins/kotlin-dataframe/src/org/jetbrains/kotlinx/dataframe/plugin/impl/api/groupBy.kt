package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeNullability
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.FirImplicitBuiltinTypeRef
import org.jetbrains.kotlin.fir.types.isNullable
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.typeContext
import org.jetbrains.kotlin.fir.types.withNullability
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlinx.dataframe.plugin.InterpretationErrorReporter
import org.jetbrains.kotlinx.dataframe.plugin.extensions.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.Interpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.Present
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleFrameColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.add
import org.jetbrains.kotlinx.dataframe.plugin.impl.data.ColumnWithPathApproximation
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.groupBy
import org.jetbrains.kotlinx.dataframe.plugin.impl.ignore
import org.jetbrains.kotlinx.dataframe.plugin.impl.makeNullable
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf
import org.jetbrains.kotlinx.dataframe.plugin.impl.type
import org.jetbrains.kotlinx.dataframe.plugin.interpret
import org.jetbrains.kotlinx.dataframe.plugin.loadInterpreter

class GroupBy(val keys: PluginDataFrameSchema, val groups: PluginDataFrameSchema) {
    companion object {
        val EMPTY = GroupBy(PluginDataFrameSchema.EMPTY, PluginDataFrameSchema.EMPTY)
    }
}

class DataFrameGroupBy : AbstractInterpreter<GroupBy>() {
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    val Arguments.moveToTop: Boolean by arg(defaultValue = Present(true))
    val Arguments.cols: ColumnsResolver by arg()

    override fun Arguments.interpret(): GroupBy {
        return GroupBy(keys = createPluginDataFrameSchema(cols.resolve(receiver), moveToTop), groups = receiver)
    }
}

class NamedValue(val name: String, val type: ConeKotlinType)

class GroupByDsl {
    val columns = mutableListOf<NamedValue>()
}

class AggregateDslInto : AbstractInterpreter<Unit>() {
    val Arguments.dsl: GroupByDsl by arg()
    val Arguments.receiver: FirExpression by arg(lens = Interpreter.Id)
    val Arguments.name: String by arg()

    override fun Arguments.interpret() {
        dsl.columns.add(NamedValue(name, receiver.resolvedType))
    }
}

class Aggregate : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: GroupBy by groupBy()
    val Arguments.body: FirAnonymousFunctionExpression by arg(lens = Interpreter.Id)
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return aggregate(
            receiver,
            InterpretationErrorReporter.DEFAULT,
            body
        )
    }
}

fun KotlinTypeFacade.aggregate(
    groupBy: GroupBy,
    reporter: InterpretationErrorReporter,
    firAnonymousFunctionExpression: FirAnonymousFunctionExpression
): PluginDataFrameSchema {
    val body = firAnonymousFunctionExpression.anonymousFunction.body
    val lastExpression = (body?.statements?.lastOrNull() as? FirReturnExpression)?.result
    val type = lastExpression?.resolvedType
    return if (type != session.builtinTypes.unitType) {
        val dsl = GroupByDsl()
        val calls = buildList {
            body?.statements?.filterIsInstance<FirFunctionCall>()?.let { addAll(it) }
            if (lastExpression is FirFunctionCall) add(lastExpression)
        }
        calls.forEach { call ->
            val schemaProcessor = call.loadInterpreter() ?: return@forEach
            interpret(
                call,
                schemaProcessor,
                mapOf("dsl" to Interpreter.Success(dsl)),
                reporter
            )
        }

        val cols = groupBy.keys.columns() + dsl.columns.map {
            simpleColumnOf(it.name, it.type)
        }
        PluginDataFrameSchema(cols)
    } else {
        PluginDataFrameSchema.EMPTY
    }
}

fun KotlinTypeFacade.createPluginDataFrameSchema(
    keys: List<ColumnWithPathApproximation>,
    moveToTop: Boolean
): PluginDataFrameSchema {
    fun addToHierarchy(
        path: List<String>,
        column: SimpleCol,
        columns: List<SimpleCol>
    ): List<SimpleCol> {
        if (path.isEmpty()) return columns

        val groupName = path[0]
        val remainingPath = path.drop(1)

        val updatedColumns = columns.map {
            if (it is SimpleColumnGroup && it.name == groupName) {
                SimpleColumnGroup(it.name, columns = addToHierarchy(remainingPath, column, it.columns()))
            } else {
                it
            }
        }

        return if (updatedColumns.any { it is SimpleColumnGroup && it.name == groupName }) {
            updatedColumns
        } else {
            val newGroup = if (remainingPath.isEmpty()) {
                column
            } else {
                SimpleColumnGroup(groupName, addToHierarchy(remainingPath, column, emptyList()))
            }
            updatedColumns + newGroup
        }
    }

    var rootColumns: List<SimpleCol> = emptyList()

    if (moveToTop) {
        rootColumns = keys.map { it.column }
    } else {
        for (key in keys) {
            val path = key.path.path
            val column = key.column
            rootColumns = addToHierarchy(path, column, rootColumns)
        }
    }


    return PluginDataFrameSchema(rootColumns)
}

class GroupByInto : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: GroupBy by groupBy()
    val Arguments.column: String by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val grouped = listOf(SimpleFrameColumn(column, receiver.groups.columns()))
        return PluginDataFrameSchema(
            receiver.keys.columns() + grouped
        )
    }
}

class GroupByToDataFrame : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: GroupBy by groupBy()
    val Arguments.groupedColumnName: String? by arg(defaultValue = Present(null))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val grouped = listOf(SimpleFrameColumn(groupedColumnName ?: "group", receiver.groups.columns()))
        return PluginDataFrameSchema(
            receiver.keys.columns() + grouped
        )
    }
}

class GroupByAdd : AbstractInterpreter<GroupBy>() {
    val Arguments.receiver: GroupBy by groupBy()
    val Arguments.name: String by arg()
    val Arguments.infer by ignore()
    val Arguments.type: TypeApproximation by type(name("expression"))

    override fun Arguments.interpret(): GroupBy {
        return GroupBy(receiver.keys, receiver.groups.add(name, type.type, context = this))
    }
}

abstract class GroupByAggregator(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.expression by type()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val aggregated = makeNullable(simpleColumnOf(name ?: defaultName, expression.type))
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

class GroupByMaxOf : GroupByAggregator(defaultName = "max")

class GroupByMinOf : GroupByAggregator(defaultName = "min")

abstract class GroupByAggregatorExpressionMean(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.expression by type()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val aggregated = makeNullable(simpleColumnOf(name ?: defaultName, session.builtinTypes.doubleType.type))
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

class GroupByMeanOf : GroupByAggregatorExpressionMean(defaultName = "mean")

class GroupByStdOf : GroupByAggregatorExpressionMean(defaultName = "std")

abstract class GroupByAggregatorExpressionComparable(val defaultName: String) :
    AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.expression by type()

    override fun Arguments.interpret(): PluginDataFrameSchema {

        val aggregated = makeNullable(simpleColumnOf(name ?: defaultName, createComparableType(session)))
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

class GroupByMedianOf : GroupByAggregatorExpressionComparable(defaultName = "median")

/**
 * Provides a base implementation for a custom schema modification interpreter
 * that groups data by specified criteria and produces aggregated results.
 *
 * The class uses a `defaultName` to define a fallback name for the result column
 * if no specific name is provided. It leverages `Arguments` properties to define
 * and resolve the group-by receiver, result name, and expression type.
 *
 * Key Components:
 * - [receiver] Represents the input data that will be grouped.
 * - [resultName] Optional name for the resulting aggregated column. Defaults to `defaultName`.
 * - [expression] Defines the type of the expression for aggregation.
 */
abstract class GroupByAggregatorExpressionSum(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.resultName: String? by arg(defaultValue = Present(null))
    val Arguments.expression by type()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val aggregated = makeNullable(simpleColumnOf(resultName ?: defaultName, expression.type))
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

/** Implementation for `sumOf` */
class GroupBySumOf : GroupByAggregatorExpressionSum(defaultName = "sum")

/**
 * Provides a base implementation for a custom schema modification interpreter
 * that groups data by specified criteria and produces aggregated results.
 *
 * The class uses a `defaultName` to define a fallback name for the result column
 * if no specific name is provided. It leverages `Arguments` properties to define
 * and resolve the group-by receiver, result name, and expression type.
 *
 * Key Components:
 * - `receiver`: Represents the input data that will be grouped.
 * - `resultName`: Optional name for the resulting aggregated column. Defaults to `defaultName`.
 * - `columns`: ColumnsResolver to define which columns to include in the grouping operation.
 */
abstract class GroupByAggregator3(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.columns: ColumnsResolver? by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        if (name == null) {
            val resolvedColumns = columns?.resolve(receiver.keys)?.map { it.column }!!.toList()
            return PluginDataFrameSchema(receiver.keys.columns() + resolvedColumns)
        } else {
            val resolvedColumns = columns?.resolve(receiver.keys)?.map { it.column }!!.toList()
            // TODO: how to handle type of multiple columns
            val aggregated =
                makeNullable(simpleColumnOf(name ?: defaultName, (resolvedColumns[0] as SimpleDataColumn).type.type))
            return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
        }
    }
}

/** Implementation for `sum` */
class GroupBySum0 : GroupByAggregator3(defaultName = "sum")

/** Implementation for `median` */
class GroupByMedian0 : GroupByAggregator3(defaultName = "median")

/** Implementation for `median` */
class GroupByMin0 : GroupByAggregator3(defaultName = "min")

/** Implementation for `median` */
class GroupByMax0 : GroupByAggregator3(defaultName = "max")

abstract class GroupByAggregatorMean(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.columns: ColumnsResolver? by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        if (name == null) {
            val resolvedColumns = columns?.resolve(receiver.keys)
                ?.map { col ->
                    simpleColumnOf(col.column.name, session.builtinTypes.doubleType.type)
                }!!.toList()
            return PluginDataFrameSchema(receiver.keys.columns() + resolvedColumns)

        } else {
            val aggregated =
                makeNullable(simpleColumnOf(name ?: defaultName, session.builtinTypes.doubleType.type))
            return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
        }
    }
}

/** Implementation for `mean` */
class GroupByMean0 : GroupByAggregatorMean(defaultName = "mean")

/** Implementation for `std` */
class GroupByStd0 : GroupByAggregatorMean(defaultName = "std")

abstract class GroupByAggregator4() : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.groups.columns()
            .filter {
                it is SimpleDataColumn
                    && it.type.type.isSubtypeOf(session.builtinTypes.numberType.type, session)
            }

        return PluginDataFrameSchema(receiver.keys.columns() + resolvedColumns)
    }
}

class GroupBySum1 : GroupByAggregator4()

abstract class GroupByAggregator4Mean() : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.groups.columns()
            .filter {
                it is SimpleDataColumn
                    && it.type.type.isSubtypeOf(session.builtinTypes.numberType.type, session)
            }.map { col ->
                simpleColumnOf(col.name, session.builtinTypes.doubleType.type)
            }.toList()

        return PluginDataFrameSchema(receiver.keys.columns() + resolvedColumns)
    }
}

class GroupByMean1 : GroupByAggregator4Mean()

class GroupByStd1 : GroupByAggregator4Mean()

private fun ConeKotlinType.isSubtypeOfComparable(session: FirSession): Boolean {
    val comparableTypes: List<FirImplicitBuiltinTypeRef> = listOf(
        session.builtinTypes.booleanType,
        session.builtinTypes.numberType,
        session.builtinTypes.byteType,
        session.builtinTypes.shortType,
        session.builtinTypes.intType,
        session.builtinTypes.longType,
        session.builtinTypes.doubleType,
        session.builtinTypes.floatType,
        session.builtinTypes.uIntType,
        session.builtinTypes.charType,
        session.builtinTypes.stringType
    )

    return comparableTypes.any { it.type.isSubtypeOf(this, session) }
}

abstract class GroupByAggregatorComparable() : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.groups.columns()
            .filter {
                it is SimpleDataColumn
                    && isIntraComparable(it, session)
            }

        return PluginDataFrameSchema(receiver.keys.columns() + resolvedColumns)
    }
}

class GroupByMax1 : GroupByAggregatorComparable()

class GroupByMin1 : GroupByAggregatorComparable()

class GroupByMedian1 : GroupByAggregatorComparable()

private fun createComparableType(session: FirSession): ConeKotlinType {
    val lookupTag = ConeClassLikeLookupTagImpl(StandardClassIds.Comparable)
    val type = lookupTag.constructType(arrayOf(session.builtinTypes.nullableAnyType.type), isNullable = false).type
    return type
}

private fun isIntraComparable(col: SimpleDataColumn, session: FirSession): Boolean {
    val comparable = StandardClassIds.Comparable.constructClassLikeType(
        typeArguments = arrayOf(col.type.type.withNullability(ConeNullability.NOT_NULL, session.typeContext)),
        isNullable = col.type.type.isNullable,
    )
    return col.type.type.isSubtypeOf(comparable, session)
}




