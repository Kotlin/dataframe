package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun AnyFrame.toSequenceImpl(type: KType): Sequence<Any> {
    val clazz = type.jvmErasure
    require(clazz.isData) {
        "`$clazz` is not a data class. `toList`, `toListOf`, `toSequence` and `toSequenceOf` support only data classes."
    }

    val constructor = clazz.primaryConstructor
    require(constructor != null) { "Class `$clazz` doesn't have a primary constructor" }

    val columnNames = clazz.memberProperties.associate { it.name to it.columnName }

    // type arguments of a generic data class, e.g. `V` -> `Int` for `Box<Int>`
    val substitution = type.typeParametersSubstitution()

    val convertedColumns = constructor.parameters.mapNotNull {
        require(it.name != null) { "Parameter name can not be null. Parameter = $it" }
        val parameterType = it.type.substitute(substitution)
        val columnName = columnNames[it.name]

        check(columnName != null) { "Can not find member property for parameter ${it.name}" }
        val index = getColumnIndex(columnName)

        // a parameter with a default value gets it when there is no column for it
        if (index < 0 && it.isOptional) return@mapNotNull null
        check(index >= 0) { "Can not find column `$columnName` in DataFrame" }

        val column = getColumn(index)
        val convertedColumn = if (column.type != parameterType) {
            when (column.kind) {
                ColumnKind.Frame -> {
                    val col: AnyCol = if (parameterType.jvmErasure == List::class) {
                        val elementType = parameterType.arguments[0].type
                        require(elementType != null) { "FrameColumn can not be converted to type `List<*>`" }
                        column.asAnyFrameColumn().map { it.toSequenceImpl(elementType).toList() }
                    } else if (parameterType.jvmErasure == DataFrame::class) {
                        // the same as `convertTo`: frames go into a `DataFrame<S>` converted to the schema of `S`,
                        // and into an `AnyFrame` as they are
                        val schemaType = parameterType.arguments[0].type
                        if (schemaType == null || schemaType.classifier == Any::class) {
                            column
                        } else {
                            column.asAnyFrameColumn().map { it.convertTo(schemaType) }
                        }
                    } else {
                        error("FrameColumn can not be converted to type `$parameterType`")
                    }
                    col
                }

                ColumnKind.Group -> {
                    DataColumn.createValueColumn(
                        column.name(),
                        column.asColumnGroup().toSequenceImpl(parameterType).toList(),
                    )
                }

                ColumnKind.Value -> {
                    require(!column.hasNulls() || parameterType.isMarkedNullable) {
                        "Can not set `null` in non-nullable property `${it.name}: $parameterType`"
                    }
                    val converted = column.convertTo(parameterType)
                    require(converted.type.withNullability(false).isSubtypeOf(parameterType)) {
                        "Can not convert ${column.type()} to $parameterType for column `${column.name()}`"
                    }
                    converted
                }
            }
        } else {
            column
        }
        it to convertedColumn
    }

    // `callBy` is only needed to let skipped parameters take their default values; `call` is faster
    val allParametersPresent = convertedColumns.size == constructor.parameters.size

    return rows().asSequence().map { row ->
        if (allParametersPresent) {
            constructor.call(*Array(convertedColumns.size) { row[convertedColumns[it].second] })
        } else {
            constructor.callBy(convertedColumns.associate { (parameter, column) -> parameter to row[column] })
        }
    }
}

/**
 * Replaces the type parameters of a generic data class in this type with the type arguments from [<code>substitution</code>][substitution],
 * at any depth, and keeps the `?` of each use: `List<V?>` becomes `List<Int?>` for `V` = `Int`.
 */
private fun KType.substitute(substitution: Map<KTypeParameter, KType>): KType =
    when (val classifier = classifier) {
        is KTypeParameter -> substitution[classifier]
            ?.let { if (isMarkedNullable) it.withNullability(true) else it }
            ?: this

        is KClass<*> -> classifier.createType(
            arguments.map { KTypeProjection(it.variance, it.type?.substitute(substitution)) },
            nullable = isMarkedNullable,
        )

        else -> this
    }
