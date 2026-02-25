package org.jetbrains.kotlinx.dataframe.impl.io

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.NameValueProperty
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.chunked
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
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.DataCollector
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.ARRAY_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.VALUE_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
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

internal interface AnyNameValueProperty : NameValueProperty<Any?> {
    override val value: Any?
}

internal fun readJsonImpl(
    parsed: Any?,
    unifyNumbers: Boolean,
    header: List<String>,
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): DataFrame<*> {
    val df: AnyFrame = when (typeClashTactic) {
        ARRAY_AND_VALUE_COLUMNS -> {
            when (parsed) {
                is JsonArray -> fromJsonListArrayAndValueColumns(
                    records = parsed,
                    unifyNumbers = unifyNumbers,
                    header = header,
                    keyValuePaths = keyValuePaths,
                )

                else -> fromJsonListArrayAndValueColumns(
                    records = listOf(parsed),
                    unifyNumbers = unifyNumbers,
                    keyValuePaths = keyValuePaths,
                )
            }
        }

        ANY_COLUMNS -> {
            when (parsed) {
                is JsonArray -> fromJsonListAnyColumns(
                    records = parsed,
                    unifyNumbers = unifyNumbers,
                    header = header,
                    keyValuePaths = keyValuePaths,
                )

                else -> fromJsonListAnyColumns(
                    records = listOf(parsed),
                    unifyNumbers = unifyNumbers,
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
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[NameValueProperty]>
 *     will be created.
 * @param header Optional list of column names. If given, [records] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [records].
 */
internal fun fromJsonListAnyColumns(
    records: List<*>,
    unifyNumbers: Boolean,
    keyValuePaths: List<JsonPath> = emptyList(),
    header: List<String> = emptyList(),
    jsonPath: JsonPath = JsonPath(),
): AnyFrame {
    var hasPrimitive = false
    var hasArray = false
    var hasObject = false

    // list element type can be JsonObject, JsonArray or primitive
    val nameGenerator = ColumnNameGenerator()
    records.forEach { record ->
        when (record) {
            is JsonObject -> {
                hasObject = true
                record.entries.forEach { nameGenerator.addIfAbsent(it.key) }
            }

            is JsonArray -> hasArray = true

            is JsonNull, null -> Unit

            is JsonPrimitive -> hasPrimitive = true
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
            val collector: DataCollector<Any?> =
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
                                unifyNumbers = unifyNumbers,
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

                    is JsonArray -> {
                        val parsed = fromJsonListAnyColumns(
                            records = v,
                            unifyNumbers = unifyNumbers,
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

                    is JsonNull -> collector.add(null)

                    is JsonPrimitive -> {
                        when {
                            v.content == "NaN" -> {
                                nanIndices.add(i)
                                collector.add(null)
                            }

                            v.isString -> collector.add(v.content)

                            v.booleanOrNull != null -> collector.add(v.boolean)

                            v.intOrNull != null -> collector.add(v.int)

                            v.longOrNull != null -> collector.add(v.long)

                            v.floatOrNull != null -> collector.add(v.float)

                            v.doubleOrNull != null -> collector.add(v.double)

                            else -> error("Malformed JSON element ${v::class}: $v")
                        }
                    }

                    else -> collector.add(v)
                }
            }
            val column = createColumnGuessingType(VALUE_COLUMN_NAME, collector.data, unifyNumbers = unifyNumbers)
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
                    is JsonArray -> values.addAll(it)
                    is JsonNull, null -> Unit
                    else -> error("Expected JsonArray, got $it")
                }
            }
            val parsed = fromJsonListAnyColumns(
                records = values,
                unifyNumbers = unifyNumbers,
                keyValuePaths = keyValuePaths,
                jsonPath = jsonPath.appendArrayWithWildcard(),
            )

            val res = when {
                parsed.isSingleUnnamedColumn() -> {
                    val col = (parsed.getColumn(0) as UnnamedColumn).col
                    val elementType = col.type
                    val columnValues = col.values
                        .asList()
                        .splitByIndices(startIndices.asSequence())
                        .toList()
                    DataColumn.createValueColumn(
                        name = ARRAY_COLUMN_NAME,
                        values = columnValues,
                        type = List::class.createType(listOf(KTypeProjection.invariant(elementType))),
                    )
                }

                else ->
                    parsed.unwrapUnnamedColumns()
                        .chunked(
                            startIndices = startIndices,
                            name = ARRAY_COLUMN_NAME, // will be erased
                        )
            }
            listOf(UnnamedColumn(res))
        }

        // Create one column of type FrameColumn<KeyValueProperty>
        colType == AnyColType.OBJECTS && isKeyValue -> {
            // collect the value types to make sure Value columns with lists and other values aren't all turned into lists
            val valueTypes = mutableSetOf<KType>()
            val dataFrames = records.map { record ->
                when (record) {
                    is JsonObject -> {
                        val map = record.mapValues { (key, value) ->
                            val parsed = fromJsonListAnyColumns(
                                records = listOf(value),
                                unifyNumbers = unifyNumbers,
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
                            guessValueType(sequenceOf(it), unifyNumbers = unifyNumbers)
                        }.commonType()

                        valueTypes += valueType

                        dataFrameOf(
                            columnOf(*map.keys.toTypedArray()).named(NameValueProperty<*>::name.name),
                            createColumnGuessingType(
                                values = map.values,
                                suggestedType = TypeSuggestion.Use(valueType),
                                unifyNumbers = unifyNumbers,
                            ).named(NameValueProperty<*>::value.name),
                        )
                    }

                    is JsonNull, null -> DataFrame.emptyOf<AnyNameValueProperty>()

                    else -> error("Expected JsonObject, got $record")
                }
            }

            val valueColumns = dataFrames.map { it[NameValueProperty<*>::value.name] }
            val valueColumnSchema = when {
                // in these cases we can safely combine the columns to get a single column schema
                valueColumns.all { it is ColumnGroup<*> } || valueColumns.all { it is FrameColumn<*> } ->
                    valueColumns.concat().toDataFrame().schema().columns.values.single()

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
                                    NameValueProperty<*>::name.name to ColumnSchema.Value(typeOf<String>()),
                                    NameValueProperty<*>::value.name to valueColumnSchema,
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
                        is JsonNull, null -> values.add(null)
                        else -> error("Expected JsonObject, got $it")
                    }
                }

                val parsed = fromJsonListAnyColumns(
                    records = values,
                    unifyNumbers = unifyNumbers,
                    keyValuePaths = keyValuePaths,
                    jsonPath = jsonPath.append(colName),
                )
                when {
                    parsed.columnsCount() == 0 ->
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

private fun AnyFrame.isSingleUnnamedColumn() = columnsCount() == 1 && getColumn(0) is UnnamedColumn

/**
 * Json to DataFrame converter that creates allows creates `value` and `array` accessors
 * instead of [Any] columns.
 * A.k.a. [TypeClashTactic.ARRAY_AND_VALUE_COLUMNS].
 *
 * @param records List of json elements to be converted to a [DataFrame].
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[NameValueProperty]>
 *     will be created.
 * @param header Optional list of column names. If given, [records] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [records].
 */
internal fun fromJsonListArrayAndValueColumns(
    records: List<*>,
    unifyNumbers: Boolean,
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
    records.forEach { record ->
        when (record) {
            is JsonObject -> record.entries.forEach {
                nameGenerator.addIfAbsent(it.key)
            }

            is JsonArray -> hasArray = true

            is JsonNull, null -> Unit

            is JsonPrimitive -> hasPrimitive = true
        }
    }
    if (records.all { it == null || it is JsonNull }) hasPrimitive = true

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
            val dataFrames = records.map { record ->
                when (record) {
                    is JsonObject -> {
                        val map = record.mapValues { (key, value) ->
                            val parsed = fromJsonListArrayAndValueColumns(
                                records = listOf(value),
                                unifyNumbers = unifyNumbers,
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
                            map.values
                                .map { guessValueType(sequenceOf(it), unifyNumbers = unifyNumbers) }
                                .commonType()

                        dataFrameOf(
                            columnOf(*map.keys.toTypedArray()).named(NameValueProperty<*>::name.name),
                            createColumnGuessingType(
                                values = map.values,
                                suggestedType = TypeSuggestion.Use(valueType),
                                unifyNumbers = unifyNumbers,
                            ).named(NameValueProperty<*>::value.name),
                        )
                    }

                    is JsonNull, null -> DataFrame.emptyOf<AnyNameValueProperty>()

                    else -> error("Expected JsonObject, got $record")
                }
            }

            listOf(
                UnnamedColumn(
                    DataColumn.createFrameColumn(
                        name = VALUE_COLUMN_NAME, // will be erased unless at top-level
                        groups = dataFrames,
                        schema = lazy {
                            dataFrames.mapNotNull { it.takeIf { it.rowsCount() > 0 }?.schema() }.intersectSchemas()
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
                        val collector: DataCollector<Any?> = createDataCollector(records.size)
                        val nanIndices = mutableListOf<Int>()
                        records.forEachIndexed { i, v ->
                            when (v) {
                                is JsonObject -> collector.add(null)

                                is JsonArray -> collector.add(null)

                                is JsonNull -> collector.add(null)

                                is JsonPrimitive -> {
                                    when {
                                        v.content == "NaN" -> {
                                            nanIndices.add(i)
                                            collector.add(null)
                                        }

                                        v.isString -> collector.add(v.content)

                                        v.booleanOrNull != null -> collector.add(v.boolean)

                                        v.intOrNull != null -> collector.add(v.int)

                                        v.longOrNull != null -> collector.add(v.long)

                                        v.floatOrNull != null -> collector.add(v.float)

                                        v.doubleOrNull != null -> collector.add(v.double)

                                        else -> error("Malformed JSON element ${v::class}: $v")
                                    }
                                }

                                else -> collector.add(v)
                            }
                        }
                        val column = createColumnGuessingType(colName, collector.data, unifyNumbers = unifyNumbers)
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
                            if (it is JsonArray) values.addAll(it.jsonArray)
                        }
                        val parsed = fromJsonListArrayAndValueColumns(
                            records = values,
                            unifyNumbers = unifyNumbers,
                            keyValuePaths = keyValuePaths,
                            jsonPath = jsonPath.appendArrayWithWildcard(),
                        )

                        val res = when {
                            parsed.isSingleUnnamedColumn() -> {
                                val col = (parsed.getColumn(0) as UnnamedColumn).col
                                val elementType = col.type
                                val columnValues =
                                    col.values
                                        .asList()
                                        .splitByIndices(startIndices.asSequence())
                                        .toList()
                                DataColumn.createValueColumn(
                                    name = colName,
                                    values = columnValues,
                                    type = List::class.createType(listOf(KTypeProjection.invariant(elementType))),
                                )
                            }

                            else -> parsed.unwrapUnnamedColumns().chunked(startIndices, colName)
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
                            unifyNumbers = unifyNumbers,
                            keyValuePaths = keyValuePaths,
                            jsonPath = jsonPath.append(colName),
                        )
                        when {
                            parsed.columnsCount() == 0 ->
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

// region friend module error suppression

@Suppress("INVISIBLE_REFERENCE")
private fun createDataCollector(initCapacity: Int = 0) =
    org.jetbrains.kotlinx.dataframe.impl.createDataCollector(initCapacity)

@Suppress("INVISIBLE_REFERENCE")
private fun <T> createDataCollector(initCapacity: Int = 0, type: KType) =
    org.jetbrains.kotlinx.dataframe.impl.createDataCollector<T>(initCapacity, type)

@Suppress("INVISIBLE_REFERENCE")
private fun <T> createColumnGuessingType(
    name: String,
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
    unifyNumbers: Boolean = false,
) = org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType(
    name = name,
    values = values,
    suggestedType = suggestedType,
    defaultValue = defaultValue,
    nullable = nullable,
    listifyValues = listifyValues,
    allColsMakesColGroup = allColsMakesColGroup,
    unifyNumbers = unifyNumbers,
)

@Suppress("INVISIBLE_REFERENCE")
private fun <T> createColumnGuessingType(
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
    unifyNumbers: Boolean = false,
) = org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType(
    values = values,
    suggestedType = suggestedType,
    defaultValue = defaultValue,
    nullable = nullable,
    listifyValues = listifyValues,
    allColsMakesColGroup = allColsMakesColGroup,
    unifyNumbers = unifyNumbers,
)

@Suppress("INVISIBLE_REFERENCE")
private fun guessValueType(
    values: Sequence<Any?>,
    upperBound: KType? = null,
    listifyValues: Boolean = false,
    allColsMakesRow: Boolean = false,
    unifyNumbers: Boolean = false,
) = org.jetbrains.kotlinx.dataframe.impl.guessValueType(
    values = values,
    upperBound = upperBound,
    listifyValues = listifyValues,
    allColsMakesRow = allColsMakesRow,
    unifyNumbers = unifyNumbers,
)

@Suppress("INVISIBLE_REFERENCE")
private fun <T> List<T>.splitByIndices(startIndices: Sequence<Int>) =
    org.jetbrains.kotlinx.dataframe.impl.splitByIndices(list = this, startIndices = startIndices)

@Suppress("INVISIBLE_REFERENCE")
private fun Iterable<KType?>.commonType(useStar: Boolean = true) =
    org.jetbrains.kotlinx.dataframe.impl.commonType(types = this, useStar)

@Suppress("INVISIBLE_REFERENCE")
private fun Iterable<DataFrameSchema>.intersectSchemas() =
    org.jetbrains.kotlinx.dataframe.impl.schema.intersectSchemas(schemas = this)

// endregion
