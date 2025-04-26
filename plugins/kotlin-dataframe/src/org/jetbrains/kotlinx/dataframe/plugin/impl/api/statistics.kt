package org.jetbrains.kotlinx.dataframe.plugin.impl.api

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
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import kotlin.reflect.KClass

private object PrimitiveClassIds {
    const val INT = "kotlin/Int"
    const val LONG = "kotlin/Long"
    const val DOUBLE = "kotlin/Double"
    const val FLOAT = "kotlin/Float"
    const val SHORT = "kotlin/Short"
    const val BYTE = "kotlin/Byte"
}

private fun KClass<*>.toClassId(): ClassId? = when (this) {
    Int::class -> ClassId.fromString(PrimitiveClassIds.INT)
    Long::class -> ClassId.fromString(PrimitiveClassIds.LONG)
    Double::class -> ClassId.fromString(PrimitiveClassIds.DOUBLE)
    Float::class -> ClassId.fromString(PrimitiveClassIds.FLOAT)
    Short::class -> ClassId.fromString(PrimitiveClassIds.SHORT)
    Byte::class -> ClassId.fromString(PrimitiveClassIds.BYTE)
    else -> null
}

private val primitiveTypeMap = mapOf(
    PrimitiveClassIds.INT to Int::class,
    PrimitiveClassIds.LONG to Long::class,
    PrimitiveClassIds.DOUBLE to Double::class,
    PrimitiveClassIds.FLOAT to Float::class,
    PrimitiveClassIds.SHORT to Short::class,
    PrimitiveClassIds.BYTE to Byte::class
)

fun ConeKotlinType.toKType(): KType? {
    return (this as? ConeClassLikeType)?.let { coneType ->
        val nullable = coneType.nullability == ConeNullability.NULLABLE
        primitiveTypeMap[coneType.lookupTag.classId.asString()]
            ?.createType(nullable = nullable)
    }
}

fun KType.toConeKotlinType(): ConeKotlinType? {
    val kClass = this.classifier as? KClass<*> ?: return null
    val classId = kClass.toClassId() ?: return null

    return classId.constructClassLikeType(
        typeArguments = emptyArray(),
        isNullable = this.isMarkedNullable
    )
}

private fun Arguments.generateStatisticResultColumns(
    statisticAggregator: Aggregator<Number, Number>,
    inputColumns: List<SimpleDataColumn>
): List<SimpleCol> {
    return inputColumns.map { col -> createUpdatedColumn(col, statisticAggregator) }
}

private fun Arguments.createUpdatedColumn(
    column: SimpleDataColumn,
    statisticAggregator: Aggregator<Number, Number>
): SimpleCol {
    val originalType = column.type.type
    val inputKType = originalType.toKType()
    val resultKType = inputKType?.let { statisticAggregator.calculateReturnType(it, emptyInput = true) }
    val updatedType = resultKType?.toConeKotlinType() ?: originalType
    return simpleColumnOf(column.name, updatedType)
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

        val newColumns = generateStatisticResultColumns(sum, resolvedColumns)

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

        val newColumns = generateStatisticResultColumns(sum, resolvedColumns)

        return PluginDataFrameSchema(receiver.columns() + newColumns)
    }
}

/** Implementation for `sum`. */
class Sum1 : Aggregator1()
