package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlin.fir.symbols.ConeClassLikeLookupTag
import org.jetbrains.kotlin.fir.types.ConeClassLikeErrorLookupTag
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeNullability
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import kotlin.reflect.KClass

fun ConeKotlinType.toKType(): KType? {
    if (this is ConeClassLikeType) {
        val classId: ClassId = this.lookupTag.classId
        val isNullable = this.nullability == ConeNullability.NULLABLE
        return when (classId.asString()) {
            "kotlin/Int" -> Int::class.createType(nullable = isNullable)
            "kotlin/Long" -> Long::class.createType(nullable = isNullable)
            "kotlin/Double" -> Double::class.createType(nullable = isNullable)
            "kotlin/Float" -> Float::class.createType(nullable = isNullable)
            "kotlin/Short" -> Short::class.createType(nullable = isNullable)
            "kotlin/Byte" -> Byte::class.createType(nullable = isNullable)
            else -> null
        }
    }
    return null
}

fun KType.toConeKotlinType(): ConeKotlinType? {
    val kClass = this.classifier as? KClass<*> ?: return null

    val classId = when (kClass) {
        Int::class -> ClassId.fromString("kotlin/Int")
        Long::class -> ClassId.fromString("kotlin/Long")
        Double::class -> ClassId.fromString("kotlin/Double")
        Float::class -> ClassId.fromString("kotlin/Float")
        Short::class -> ClassId.fromString("kotlin/Short")
        Byte::class -> ClassId.fromString("kotlin/Byte")
        else -> return null
    }

    val lookupTag: ConeClassLikeLookupTag = ConeClassLikeErrorLookupTag(classId)

    val nullability = if (this.isMarkedNullable) ConeNullability.NULLABLE else ConeNullability.NOT_NULL

    return ConeClassLikeTypeImpl(
        lookupTag = lookupTag,
        typeArguments = emptyArray(),
        isNullable = (nullability == ConeNullability.NULLABLE)
    )
}

private fun Arguments.calculateResultTypeForStatistic(
    aggregator: Aggregator<Number, Number>,
    resolvedColumns: List<SimpleDataColumn>
): List<SimpleCol> {
    val newColumns = resolvedColumns
        .map { col ->
            val columnType = col.type.type
            val inputColumnKType = columnType.toKType()
            val calculatedReturnKType = inputColumnKType?.let { aggregator.calculateReturnType(it, emptyInput = false) }
            val updatedType =
                calculatedReturnKType?.toConeKotlinType() ?: columnType // stay with input type by default
            simpleColumnOf(col.name, updatedType)
        }
        .toList()
    return newColumns
}

val skipNaN = true
val sum = Aggregators.sum(skipNaN)

/** Adds to the schema only numerical columns. */
abstract class Aggregator0 : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.columns()
            .filterIsInstance<SimpleDataColumn>()
            .filter { it.type.type.isSubtypeOf(session.builtinTypes.numberType.type, session) }

        val newColumns = calculateResultTypeForStatistic(sum, resolvedColumns)

        return PluginDataFrameSchema(receiver.columns() + newColumns)
    }
}

/** Implementation for `sum`. */
class Sum0 : Aggregator0()

/** Adds to the schema only numerical columns. */
abstract class Aggregator1 : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    private val Arguments.columns: ColumnsResolver by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = columns.resolve(receiver).map { it.column }.filterIsInstance<SimpleDataColumn>().toList()

        val newColumns = calculateResultTypeForStatistic(sum, resolvedColumns)

        return PluginDataFrameSchema(receiver.columns() + newColumns)
    }
}

/** Implementation for `sum`. */
class Sum1 : Aggregator1()
