package org.jetbrains.kotlinx.dataframe.impl.io

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.splitInto
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.DataCollectorBase
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumn
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.impl.schema.intersectSchemas
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices
import org.jetbrains.kotlinx.dataframe.io.ARRAY_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.VALUE_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.values
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

private fun DataFrame<Any?>.unwrapUnnamedColumns() = dataFrameOf(columns().map { it.unwrapUnnamedColumn() })

private fun AnyCol.unwrapUnnamedColumn() = if (this is UnnamedColumn) col else this

private enum class AnyColType {
    ANY,
    ARRAYS,
    OBJECTS,
}

internal interface AnyKeyValueProperty : KeyValueProperty<Any?> {
    override val value: Any?
}

internal fun readJson(
    parsed: Any?,
    header: List<String>,
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): DataFrame<*> {
    val df: AnyFrame = when (typeClashTactic) {
        ARRAY_AND_VALUE_COLUMNS -> {
            when (parsed) {
                is JsonArray<*> -> fromJsonListArrayAndValueColumns(
                    records = parsed.value,
                    header = header,
                    keyValuePaths = keyValuePaths,
                )

                else -> fromJsonListArrayAndValueColumns(
                    records = listOf(parsed),
                    keyValuePaths = keyValuePaths,
                )
            }
        }

        ANY_COLUMNS -> {
            when (parsed) {
                is JsonArray<*> -> fromJsonListAnyColumns(
                    records = parsed.value,
                    header = header,
                    keyValuePaths = keyValuePaths,
                )

                else -> fromJsonListAnyColumns(
                    records = listOf(parsed),
                    keyValuePaths = keyValuePaths,
                )
            }
        }
    }
    return df.unwrapUnnamedColumns()
}

/**
 * Json to DataFrame converter that creates [Any] columns.
 * A.k.a. [TypeClashTactic.ANY_COLUMNS].
 *
 * @param records List of json elements to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param header Optional list of column names. If given, [records] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [records].
 */
internal fun fromJsonListAnyColumns(
    records: List<*>,
    keyValuePaths: List<JsonPath> = emptyList(),
    header: List<String> = emptyList(),
    jsonPath: JsonPath = JsonPath(),
): AnyFrame {
    var hasPrimitive = false
    var hasArray = false
    var hasObject = false

    // list element type can be JsonObject, JsonArray or primitive
    val nameGenerator = ColumnNameGenerator()
    records.forEach {
        when (it) {
            is JsonObject -> {
                hasObject = true
                it.entries.forEach {
                    nameGenerator.addIfAbsent(it.key)
                }
            }

            is JsonArray<*> -> hasArray = true

            null -> Unit

            else -> hasPrimitive = true
        }
    }

    val colType = when {
        hasArray && !hasPrimitive && !hasObject -> AnyColType.ARRAYS
        hasObject && !hasPrimitive && !hasArray -> AnyColType.OBJECTS
        else -> AnyColType.ANY
    }
    val justPrimitives = hasPrimitive && !hasArray && !hasObject
    val isKeyValue = keyValuePaths.any { jsonPath.matches(it) }

    if (isKeyValue && colType != AnyColType.OBJECTS) {
        error("Key value path $jsonPath does not match objects.")
    }

    @Suppress("KotlinConstantConditions")
    val columns: List<AnyCol> = when {
        // Create one column of type Any? (or guessed primitive type) from all the records
        colType == AnyColType.ANY -> {
            val collector: DataCollectorBase<Any?> =
                if (justPrimitives) {
                    createDataCollector(records.size) // guess the type
                } else {
                    createDataCollector(records.size, typeOf<Any?>()) // use Any?
                }

            val nanIndices = mutableListOf<Int>()
            records.forEachIndexed { i, v ->
                when (v) {
                    is JsonObject -> {
                        val parsed =
                            fromJsonListAnyColumns(
                                records = listOf(v),
                                keyValuePaths = keyValuePaths,
                                jsonPath = jsonPath.replaceLastWildcardWithIndex(i),
                            )
                        collector.add(
                            if (parsed.isSingleUnnamedColumn()) {
                                (parsed.getColumn(0) as UnnamedColumn).col.values.first()
                            } else {
                                parsed.firstOrNull() ?: DataRow.empty
                            },
                        )
                    }

                    is JsonArray<*> -> {
                        val parsed = fromJsonListAnyColumns(
                            records = v,
                            keyValuePaths = keyValuePaths,
                            jsonPath = jsonPath.replaceLastWildcardWithIndex(i).appendArrayWithWildcard(),
                        )
                        collector.add(
                            if (parsed.isSingleUnnamedColumn()) {
                                (parsed.getColumn(0) as UnnamedColumn).col.values.asList()
                            } else {
                                parsed.unwrapUnnamedColumns()
                            },
                        )
                    }

                    "NaN" -> {
                        nanIndices.add(i)
                        collector.add(null)
                    }

                    else -> collector.add(v)
                }
            }
            val column = collector.toColumn(VALUE_COLUMN_NAME)
            val res = if (nanIndices.isNotEmpty()) {
                fun <C> DataColumn<C>.updateNaNs(nanValue: C): DataColumn<C> {
                    var j = 0
                    var nextNanIndex = nanIndices[j]
                    return mapIndexed(column.type) { i, v ->
                        if (i == nextNanIndex) {
                            j++
                            nextNanIndex = if (j < nanIndices.size) nanIndices[j] else -1
                            nanValue
                        } else {
                            v
                        }
                    }
                }
                when (column.typeClass) {
                    Double::class -> column.cast<Double?>().updateNaNs(Double.NaN)
                    Float::class -> column.cast<Float?>().updateNaNs(Float.NaN)
                    String::class -> column.cast<String?>().updateNaNs("NaN")
                    else -> column
                }
            } else {
                column
            }
            listOf(UnnamedColumn(res))
        }

        // Create one column of type FrameColumn, or List<> from all the records if they are all arrays
        colType == AnyColType.ARRAYS -> {
            val values = mutableListOf<Any?>()
            val startIndices = ArrayList<Int>()
            records.forEach {
                startIndices.add(values.size)
                when (it) {
                    is JsonArray<*> -> values.addAll(it.value)
                    null -> Unit
                    else -> error("Expected JsonArray, got $it")
                }
            }
            val parsed = fromJsonListAnyColumns(
                records = values,
                keyValuePaths = keyValuePaths,
                jsonPath = jsonPath.appendArrayWithWildcard(),
            )

            val res = when {
                parsed.isSingleUnnamedColumn() -> {
                    val col = (parsed.getColumn(0) as UnnamedColumn).col
                    val elementType = col.type
                    val values = col.values.asList().splitByIndices(startIndices.asSequence()).toList()
                    DataColumn.createValueColumn(
                        name = ARRAY_COLUMN_NAME,
                        values = values,
                        type = List::class.createType(listOf(KTypeProjection.invariant(elementType))),
                    )
                }

                else -> DataColumn.createFrameColumn(
                    name = ARRAY_COLUMN_NAME, // will be erased
                    df = parsed.unwrapUnnamedColumns(),
                    startIndices = startIndices,
                )
            }
            listOf(UnnamedColumn(res))
        }

        // Create one column of type FrameColumn<KeyValueProperty>
        colType == AnyColType.OBJECTS && isKeyValue -> {
            // collect the value types to make sure Value columns with lists and other values aren't all turned into lists
            val valueTypes = mutableSetOf<KType>()
            val dataFrames = records.map {
                when (it) {
                    is JsonObject -> {
                        val map = it.map.mapValues { (key, value) ->
                            val parsed = fromJsonListAnyColumns(
                                records = listOf(value),
                                keyValuePaths = keyValuePaths,
                                jsonPath = jsonPath.append(key),
                            )
                            if (parsed.isSingleUnnamedColumn()) {
                                (parsed.getColumn(0) as UnnamedColumn).col.values.first()
                            } else {
                                parsed.unwrapUnnamedColumns().firstOrNull()
                            }
                        }
                        val valueType = map.values.map {
                            guessValueType(sequenceOf(it))
                        }.commonType()

                        valueTypes += valueType

                        dataFrameOf(
                            columnOf(*map.keys.toTypedArray()).named(KeyValueProperty<*>::key.name),
                            createColumn(values = map.values, suggestedType = valueType, guessType = false)
                                .named(KeyValueProperty<*>::value.name),
                        )
                    }

                    null -> DataFrame.emptyOf<AnyKeyValueProperty>()

                    else -> error("Expected JsonObject, got $it")
                }
            }

            val valueColumns = dataFrames.map { it[KeyValueProperty<*>::value.name] }
            val valueColumnSchema = when {
                // in these cases we can safely combine the columns to get a single column schema
                valueColumns.all { it is ColumnGroup<*> } || valueColumns.all { it is FrameColumn<*> } ->
                    valueColumns.concat().extractSchema()

                // to avoid listification, we create the value columns schema ourselves (https://github.com/Kotlin/dataframe/issues/184)
                else -> ColumnSchema.Value(valueTypes.commonType())
            }

            listOf(
                UnnamedColumn(
                    DataColumn.createFrameColumn(
                        name = VALUE_COLUMN_NAME, // will be erased unless at top-level
                        groups = dataFrames,
                        schema = lazy {
                            DataFrameSchemaImpl(
                                columns = mapOf(
                                    KeyValueProperty<*>::key.name to ColumnSchema.Value(typeOf<String>()),
                                    KeyValueProperty<*>::value.name to valueColumnSchema,
                                ),
                            )
                        },
                    ),
                ),
            )
        }

        // Create multiple columns from all the records if they are all objects, merging the objects in essence
        colType == AnyColType.OBJECTS && !isKeyValue -> {
            nameGenerator.names.map { colName ->
                val values = ArrayList<Any?>(records.size)

                records.forEach {
                    when (it) {
                        is JsonObject -> values.add(it[colName])
                        null -> values.add(null)
                        else -> error("Expected JsonObject, got $it")
                    }
                }

                val parsed = fromJsonListAnyColumns(
                    records = values,
                    keyValuePaths = keyValuePaths,
                    jsonPath = jsonPath.append(colName),
                )
                when {
                    parsed.ncol == 0 ->
                        DataColumn.createValueColumn(
                            name = colName,
                            values = arrayOfNulls<Any?>(values.size).toList(),
                            type = typeOf<Any?>(),
                        )

                    parsed.isSingleUnnamedColumn() ->
                        (parsed.getColumn(0) as UnnamedColumn).col.rename(colName)

                    else ->
                        DataColumn.createColumnGroup(colName, parsed.unwrapUnnamedColumns()) as AnyCol
                }
            }
        }

        else -> error("")
    }

    return when {
        columns.isEmpty() -> DataFrame.empty(records.size)

        columns.size == 1 && hasArray && header.isNotEmpty() && columns[0].typeClass == List::class ->
            columns[0]
                .cast<List<*>>()
                .splitInto(*header.toTypedArray())

        else -> columns.toDataFrame()
    }
}

private fun AnyFrame.isSingleUnnamedColumn() = ncol == 1 && getColumn(0) is UnnamedColumn

/**
 * Json to DataFrame converter that creates allows creates `value` and `array` accessors
 * instead of [Any] columns.
 * A.k.a. [TypeClashTactic.ARRAY_AND_VALUE_COLUMNS].
 *
 * @param records List of json elements to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param header Optional list of column names. If given, [records] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [records].
 */
internal fun fromJsonListArrayAndValueColumns(
    records: List<*>,
    keyValuePaths: List<JsonPath> = emptyList(),
    header: List<String> = emptyList(),
    jsonPath: JsonPath = JsonPath(),
): AnyFrame {
    var hasPrimitive = false
    var hasArray = false
    val isKeyValue = keyValuePaths.any { jsonPath.matches(it) }

    // list element type can be JsonObject, JsonArray or primitive
    // So first, we gather all properties of objects to merge including "array" and "value" if needed
    // so the resulting type of a property with instances 123, ["abc"], and { "a": 1, "b": 2 } will be
    // { array: List<String>, value: Int?, a: Int?, b: Int? }
    // and instances will look like
    // { "array": [], "value": 123, "a": null, "b": null }

    val nameGenerator = ColumnNameGenerator()
    records.forEach {
        when (it) {
            is JsonObject -> it.entries.forEach {
                nameGenerator.addIfAbsent(it.key)
            }

            is JsonArray<*> -> hasArray = true

            null -> Unit

            else -> hasPrimitive = true
        }
    }
    if (records.all { it == null }) hasPrimitive = true

    // Add a value column to the collected names if needed
    val valueColumn = if (hasPrimitive || records.isEmpty()) {
        nameGenerator.addUnique(VALUE_COLUMN_NAME)
    } else {
        null
    }

    // Add an array column to the collected names if needed
    val arrayColumn = if (hasArray) {
        nameGenerator.addUnique(ARRAY_COLUMN_NAME)
    } else {
        null
    }

    // only properties that consist of just objects (or are empty) can be merged to key/value FrameColumns
    if (isKeyValue && (hasPrimitive || hasArray)) {
        error("Key value path $jsonPath does not match objects.")
    }

    // Create columns from the collected names
    val columns: List<AnyCol> = when {
        // instead of using the names, generate a single key/value frame column
        isKeyValue -> {
            val dataFrames = records.map {
                when (it) {
                    is JsonObject -> {
                        val map = it.map.mapValues { (key, value) ->
                            val parsed = fromJsonListArrayAndValueColumns(
                                records = listOf(value),
                                keyValuePaths = keyValuePaths,
                                jsonPath = jsonPath.append(key),
                            )
                            if (parsed.isSingleUnnamedColumn()) {
                                (parsed.getColumn(0) as UnnamedColumn).col.values.first()
                            } else {
                                parsed.unwrapUnnamedColumns().firstOrNull()
                            }
                        }
                        val valueType =
                            map.values.map { guessValueType(sequenceOf(it)) }
                                .commonType()

                        dataFrameOf(
                            columnOf(*map.keys.toTypedArray()).named(KeyValueProperty<*>::key.name),
                            createColumn(
                                values = map.values,
                                suggestedType = valueType,
                                guessType = false,
                            ).named(KeyValueProperty<*>::value.name),
                        )
                    }

                    null -> DataFrame.emptyOf<AnyKeyValueProperty>()

                    else -> error("Expected JsonObject, got $it")
                }
            }

            listOf(
                UnnamedColumn(
                    DataColumn.createFrameColumn(
                        name = VALUE_COLUMN_NAME, // will be erased unless at top-level
                        groups = dataFrames,
                        schema = lazy {
                            dataFrames.mapNotNull { it.takeIf { it.nrow > 0 }?.schema() }.intersectSchemas()
                        },
                    ),
                ),
            )
        }

        // generate columns using the collected names
        else ->
            nameGenerator.names.map { colName ->
                when {
                    // Collect primitive values from records into the `value` column if needed
                    colName == valueColumn && (hasPrimitive || records.isEmpty()) -> {
                        val collector = createDataCollector(records.size)
                        val nanIndices = mutableListOf<Int>()
                        records.forEachIndexed { i, v ->
                            when (v) {
                                is JsonObject -> collector.add(null)

                                is JsonArray<*> -> collector.add(null)

                                "NaN" -> {
                                    nanIndices.add(i)
                                    collector.add(null)
                                }

                                else -> collector.add(v)
                            }
                        }
                        val column = collector.toColumn(colName)
                        val res = if (nanIndices.isNotEmpty()) {
                            fun <C> DataColumn<C>.updateNaNs(nanValue: C): DataColumn<C> {
                                var j = 0
                                var nextNanIndex = nanIndices[j]
                                return mapIndexed(column.type) { i, v ->
                                    if (i == nextNanIndex) {
                                        j++
                                        nextNanIndex = if (j < nanIndices.size) nanIndices[j] else -1
                                        nanValue
                                    } else {
                                        v
                                    }
                                }
                            }
                            when (column.typeClass) {
                                Double::class -> column.cast<Double?>().updateNaNs(Double.NaN)
                                Float::class -> column.cast<Float?>().updateNaNs(Float.NaN)
                                String::class -> column.cast<String?>().updateNaNs("NaN")
                                else -> column
                            }
                        } else {
                            column
                        }
                        UnnamedColumn(res)
                    }

                    // Collect arrays from records into the `array` column if needed
                    colName == arrayColumn && hasArray -> {
                        val values = mutableListOf<Any?>()
                        val startIndices = ArrayList<Int>()
                        records.forEach {
                            startIndices.add(values.size)
                            if (it is JsonArray<*>) values.addAll(it.value)
                        }
                        val parsed = fromJsonListArrayAndValueColumns(
                            records = values,
                            keyValuePaths = keyValuePaths,
                            jsonPath = jsonPath.appendArrayWithWildcard(),
                        )

                        val res = when {
                            parsed.isSingleUnnamedColumn() -> {
                                val col = (parsed.getColumn(0) as UnnamedColumn).col
                                val elementType = col.type
                                val values = col.values.asList().splitByIndices(startIndices.asSequence()).toList()
                                DataColumn.createValueColumn(
                                    name = colName,
                                    values = values,
                                    type = List::class.createType(listOf(KTypeProjection.invariant(elementType))),
                                )
                            }

                            else -> DataColumn.createFrameColumn(colName, parsed.unwrapUnnamedColumns(), startIndices)
                        }
                        UnnamedColumn(res)
                    }

                    // Collect the current column name as property from the objects in records
                    else -> {
                        val values = ArrayList<Any?>(records.size)
                        records.forEach {
                            when (it) {
                                is JsonObject -> values.add(it[colName])
                                else -> values.add(null)
                            }
                        }

                        val parsed = fromJsonListArrayAndValueColumns(
                            records = values,
                            keyValuePaths = keyValuePaths,
                            jsonPath = jsonPath.append(colName),
                        )
                        when {
                            parsed.ncol == 0 ->
                                DataColumn.createValueColumn(
                                    name = colName,
                                    values = arrayOfNulls<Any?>(values.size).toList(),
                                    type = typeOf<Any?>(),
                                )

                            parsed.isSingleUnnamedColumn() ->
                                (parsed.getColumn(0) as UnnamedColumn).col.rename(colName)

                            else ->
                                DataColumn.createColumnGroup(colName, parsed.unwrapUnnamedColumns()) as AnyCol
                        }
                    }
                }
            }
    }

    return when {
        columns.isEmpty() ->
            DataFrame.empty(records.size)

        columns.size == 1 && hasArray && header.isNotEmpty() && columns[0].typeClass == List::class ->
            columns[0]
                .cast<List<*>>()
                .splitInto(*header.toTypedArray())

        else ->
            columns.toDataFrame()
    }
}

// we need it to check if AnyFrame created by recursive call has single unnamed column,
// unnamed column means this column is not created from field of a record [{"value": 1}, {"value": 2}],
// but filtered values [1, { ... }, []] -> [1, null, null]
// or arrays: [1, { ...}, []] -> [null, null, []]
private class UnnamedColumn(val col: DataColumn<Any?>) : DataColumn<Any?> by col
