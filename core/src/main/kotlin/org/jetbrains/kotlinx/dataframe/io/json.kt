package org.jetbrains.kotlinx.dataframe.io

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.KlaxonJson
import com.beust.klaxon.Parser
import com.beust.klaxon.json
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.splitInto
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

public class JSON : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readJson(stream, header = header)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readJson(file, header = header)

    override fun acceptsExtension(ext: String): Boolean = ext == "json"

    override val testOrder: Int = 10000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        return DefaultReadJsonMethod(pathRepresentation)
    }

    /**
     * Allows the choice of how to handle type clashes when reading a JSON file.
     * Such as:
     * ```json
     * [
     *     { "a": "text" },
     *     { "a": { "b": 2 } },
     *     { "a": [6, 7, 8] }
     *  ]
     * ```
     *
     * [ARRAY_AND_VALUE_COLUMNS] (default) will create a [DataFrame] looking like:
     * ```
     * ⌌----------------------------------------------⌍
     * |  | a:{b:Int?, value:String?, array:List<Int>}|
     * |--|-------------------------------------------|
     * | 0|                             { value:text }|
     * | 1|                                    { b:2 }|
     * | 2|                        { array:[6, 7, 8] }|
     * ⌎----------------------------------------------⌏
     * ```
     * So, for the type clashing argument it will create a [ColumnGroup] with the properties `value`, `array`,
     * and the unwrapped properties of the objects the property can be.
     *
     * [ANY_COLUMNS] will create a [DataFrame] looking like:
     * ```
     * ⌌-------------⌍
     * |  |     a:Any|
     * |--|----------|
     * | 0|      text|
     * | 1|   { b:2 }|
     * | 2| [6, 7, 8]|
     * ⌎-------------⌏
     * ```
     *
     */
    public enum class TypeClashTactic {
        ARRAY_AND_VALUE_COLUMNS,
        ANY_COLUMNS,
    }
}

public fun DataFrame.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(file.toURI().toURL(), header, typeClashTactic)

public fun DataRow.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(file, header, typeClashTactic).single()

public fun DataFrame.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(asURL(path), header, typeClashTactic)

public fun DataRow.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(path, header, typeClashTactic).single()

public fun DataFrame.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = catchHttpResponse(url) { readJson(it, header, typeClashTactic) }

public fun DataRow.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(url, header, typeClashTactic).single()

public fun DataFrame.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(Parser.default().parse(stream), header, typeClashTactic)

public fun DataRow.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(stream, header, typeClashTactic).single()

public fun DataFrame.Companion.readJsonStr(
    text: String,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(Parser.default().parse(StringBuilder(text)), header, typeClashTactic)

public fun DataRow.Companion.readJsonStr(
    text: String,
    header: List<String> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJsonStr(text, header, typeClashTactic).single()

private fun readJson(
    parsed: Any?,
    header: List<String>,
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): DataFrame<*> {
    val df: AnyFrame = when (typeClashTactic) {
        ARRAY_AND_VALUE_COLUMNS -> {
            when (parsed) {
                is JsonArray<*> -> fromJsonListArrayAndValueColumns(parsed.value, header)
                else -> fromJsonListArrayAndValueColumns(listOf(parsed))
            }
        }

        ANY_COLUMNS -> {
            when (parsed) {
                is JsonArray<*> -> fromJsonListAnyColumns(parsed.value, header)
                else -> fromJsonListAnyColumns(listOf(parsed))
            }
        }
    }
    return df.unwrapUnnamedColumns()
}

private fun DataFrame<Any?>.unwrapUnnamedColumns() =
    dataFrameOf(columns().map { it.unwrapUnnamedColumn() })

private fun AnyCol.unwrapUnnamedColumn() = if (this is UnnamedColumn) col else this

private enum class AnyColType {
    ANY,
    ARRAYS,
    OBJECTS,
}

internal fun fromJsonListAnyColumns(records: List<*>, header: List<String> = emptyList()): AnyFrame {
    fun AnyFrame.isSingleUnnamedColumn() = ncol == 1 && getColumn(0) is UnnamedColumn

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

    val columns: List<AnyCol> = when (colType) {
        AnyColType.ANY -> {
            val collector = createDataCollector<Any?>(records.size, typeOf<Any?>())
            val nanIndices = mutableListOf<Int>()
            records.forEachIndexed { i, v ->
                when (v) {
                    is JsonObject -> {
                        val parsed = fromJsonListAnyColumns(listOf(v))
                        collector.add(
                            if (parsed.isSingleUnnamedColumn()) (parsed.getColumn(0) as UnnamedColumn).col.values.first()
                            else parsed.unwrapUnnamedColumns().first()
                        )
                    }

                    is JsonArray<*> -> {
                        val parsed = fromJsonListAnyColumns(v)
                        collector.add(
                            if (parsed.isSingleUnnamedColumn()) (parsed.getColumn(0) as UnnamedColumn).col.values.asList()
                            else parsed.unwrapUnnamedColumns()
                        )
                    }

                    "NaN" -> {
                        nanIndices.add(i); collector.add(null)
                    }

                    else -> collector.add(v)
                }
            }
            val column = collector.toColumn(valueColumnName)
            val res = if (nanIndices.isNotEmpty()) {
                fun <C> DataColumn<C>.updateNaNs(nanValue: C): DataColumn<C> {
                    var j = 0
                    var nextNanIndex = nanIndices[j]
                    return mapIndexed(column.type) { i, v ->
                        if (i == nextNanIndex) {
                            j++
                            nextNanIndex = if (j < nanIndices.size) nanIndices[j] else -1
                            nanValue
                        } else v
                    }
                }
                when (column.typeClass) {
                    Double::class -> column.cast<Double?>().updateNaNs(Double.NaN)
                    Float::class -> column.cast<Float?>().updateNaNs(Float.NaN)
                    String::class -> column.cast<String?>().updateNaNs("NaN")
                    else -> column
                }
            } else column
            listOf(UnnamedColumn(res))
        }

        AnyColType.ARRAYS -> {
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
            val parsed = fromJsonListAnyColumns(values)

            val res = when {
                parsed.isSingleUnnamedColumn() -> {
                    val col = (parsed.getColumn(0) as UnnamedColumn).col
                    val elementType = col.type
                    val values = col.values.asList().splitByIndices(startIndices.asSequence()).toList()
                    DataColumn.createValueColumn(
                        name = arrayColumnName,
                        values = values,
                        type = List::class.createType(listOf(KTypeProjection.invariant(elementType)))
                    )
                }

                else -> DataColumn.createFrameColumn(arrayColumnName, parsed.unwrapUnnamedColumns(), startIndices)
            }
            listOf(UnnamedColumn(res))
        }

        AnyColType.OBJECTS -> {
            nameGenerator.names.map { colName ->
                val values = ArrayList<Any?>(records.size)

                records.forEach {
                    when (it) {
                        is JsonObject -> values.add(it[colName])
                        else -> error("Expected JsonObject, got $it")
                    }
                }

                val parsed = fromJsonListAnyColumns(values)
                when {
                    parsed.ncol == 0 -> DataColumn.createValueColumn(
                        name = colName,
                        values = arrayOfNulls<Any?>(values.size).toList(),
                        type = typeOf<Any?>(),
                    )

                    parsed.isSingleUnnamedColumn() -> (parsed.getColumn(0) as UnnamedColumn).col.rename(colName)
                    else -> DataColumn.createColumnGroup(colName, parsed.unwrapUnnamedColumns()) as AnyCol
                }
            }
        }
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

internal const val arrayColumnName: String = "array"
internal const val valueColumnName: String = "value"

internal fun fromJsonListArrayAndValueColumns(records: List<*>, header: List<String> = emptyList()): AnyFrame {
    fun AnyFrame.isSingleUnnamedColumn() = ncol == 1 && getColumn(0) is UnnamedColumn

    var hasPrimitive = false
    var hasArray = false
    // list element type can be JsonObject, JsonArray or primitive
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

    val valueColumn = if (hasPrimitive || records.isEmpty()) {
        nameGenerator.addUnique(valueColumnName)
    } else valueColumnName

    val arrayColumn = if (hasArray) {
        nameGenerator.addUnique(arrayColumnName)
    } else arrayColumnName

    val columns: List<AnyCol> = nameGenerator.names.map { colName ->
        when {
            colName == valueColumn && (hasPrimitive || records.isEmpty()) -> {
                val collector = createDataCollector(records.size)
                val nanIndices = mutableListOf<Int>()
                records.forEachIndexed { i, v ->
                    when (v) {
                        is JsonObject -> collector.add(null)
                        is JsonArray<*> -> collector.add(null)
                        "NaN" -> {
                            nanIndices.add(i); collector.add(null)
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
                            } else v
                        }
                    }
                    when (column.typeClass) {
                        Double::class -> column.cast<Double?>().updateNaNs(Double.NaN)
                        Float::class -> column.cast<Float?>().updateNaNs(Float.NaN)
                        String::class -> column.cast<String?>().updateNaNs("NaN")
                        else -> column
                    }
                } else column
                UnnamedColumn(res)
            }

            colName == arrayColumn && hasArray -> {
                val values = mutableListOf<Any?>()
                val startIndices = ArrayList<Int>()
                records.forEach {
                    startIndices.add(values.size)
                    if (it is JsonArray<*>) values.addAll(it.value)
                }
                val parsed = fromJsonListArrayAndValueColumns(values)

                val res = when {
                    parsed.isSingleUnnamedColumn() -> {
                        val col = (parsed.getColumn(0) as UnnamedColumn).col
                        val elementType = col.type
                        val values = col.values.asList().splitByIndices(startIndices.asSequence()).toList()
                        DataColumn.createValueColumn(
                            name = colName,
                            values = values,
                            type = List::class.createType(listOf(KTypeProjection.invariant(elementType)))
                        )
                    }

                    else -> DataColumn.createFrameColumn(colName, parsed.unwrapUnnamedColumns(), startIndices)
                }
                UnnamedColumn(res)
            }

            else -> {
                val values = ArrayList<Any?>(records.size)

                records.forEach {
                    when (it) {
                        is JsonObject -> values.add(it[colName])
                        else -> values.add(null)
                    }
                }

                val parsed = fromJsonListArrayAndValueColumns(values)
                when {
                    parsed.ncol == 0 -> DataColumn.createValueColumn(
                        name = colName,
                        values = arrayOfNulls<Any?>(values.size).toList(),
                        type = typeOf<Any?>(),
                    )

                    parsed.isSingleUnnamedColumn() -> (parsed.getColumn(0) as UnnamedColumn).col.rename(colName)
                    else -> DataColumn.createColumnGroup(colName, parsed.unwrapUnnamedColumns()) as AnyCol
                }
            }
        }
    }

    return when {
        columns.isEmpty() -> DataFrame.empty(records.size)
        columns.size == 1 && hasArray && header.isNotEmpty() && columns[0].typeClass == List::class -> columns[0].cast<List<*>>()
            .splitInto(*header.toTypedArray())

        else -> columns.toDataFrame()
    }
}

// we need it to check if AnyFrame created by recursive call has single unnamed column,
// unnamed column means this column is not created from field of a record [{"value": 1}, {"value": 2}],
// but filtered values [1, { ... }, []] -> [1, null, null]
// or arrays: [1, { ...}, []] -> [null, null, []]
private class UnnamedColumn(val col: DataColumn<Any?>) : DataColumn<Any?> by col

private val valueTypes =
    setOf(Boolean::class, Double::class, Int::class, Float::class, Long::class, Short::class, Byte::class)

internal fun KlaxonJson.encodeRow(frame: ColumnsContainer<*>, index: Int): JsonObject? {
    val values = frame.columns().mapNotNull { col ->
        when {
            col is ColumnGroup<*> -> encodeRow(col, index)
            col is FrameColumn<*> -> col[index]?.let { encodeFrame(it) }
            col.isList() -> {
                col[index]?.let { array(it as List<*>) } ?: array()
            }

            col.typeClass in valueTypes -> {
                val v = col[index]
                if ((v is Double && v.isNaN()) || (v is Float && v.isNaN())) {
                    v.toString()
                } else v
            }

            else -> col[index]?.toString()
        }?.let { col.name to it }
    }
    if (values.isEmpty()) return null
    return obj(values)
}

internal fun KlaxonJson.encodeFrame(frame: AnyFrame): JsonArray<*> {
    val allColumns = frame.columns()

    // if there is only 1 column, then `isValidValueColumn` always true.
    // But at the same time, we shouldn't treat dataFrameOf("value")(1,2,3) like unnamed column
    // because it was created by user.
    val isPossibleToFindUnnamedColumns = allColumns.size != 1
    val valueColumn = allColumns.filter { it.name.startsWith(valueColumnName) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }?.let { valueCol ->
            if (valueCol.kind() != ColumnKind.Value) { // check that value in this column is not null only when other values are null
                null
            } else {
                // check that value in this column is not null only when other values are null
                val isValidValueColumn = frame.rows().all { row ->
                    if (valueCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != valueCol.name) col[row] == null
                            else true
                        }
                    } else true
                }
                if (isValidValueColumn) valueCol
                else null
            }
        }

    val arrayColumn = frame.columns().filter { it.name.startsWith(arrayColumnName) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }?.let { arrayCol ->
            if (arrayCol.kind() == ColumnKind.Group) null
            else {
                // check that value in this column is not null only when other values are null
                val isValidArrayColumn = frame.rows().all { row ->
                    if (arrayCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != arrayCol.name) col[row] == null
                            else true
                        }
                    } else true
                }
                if (isValidArrayColumn) arrayCol
                else null
            }
        }

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data = frame.indices().map { rowIndex ->
        valueColumn?.get(rowIndex) ?: arrayColumn?.get(rowIndex)
            ?.let { if (arraysAreFrames) encodeFrame(it as AnyFrame) else null } ?: encodeRow(frame, rowIndex)
    }
    return array(data)
}

public fun AnyFrame.toJson(prettyPrint: Boolean = false, canonical: Boolean = false): String {
    return json {
        encodeFrame(this@toJson)
    }.toJsonString(prettyPrint, canonical)
}

public fun AnyRow.toJson(prettyPrint: Boolean = false, canonical: Boolean = false): String {
    return json {
        encodeRow(df(), index())
    }?.toJsonString(prettyPrint, canonical) ?: ""
}

public fun AnyFrame.writeJson(file: File, prettyPrint: Boolean = false, canonical: Boolean = false) {
    file.writeText(toJson(prettyPrint, canonical))
}

public fun AnyFrame.writeJson(path: String, prettyPrint: Boolean = false, canonical: Boolean = false): Unit =
    writeJson(File(path), prettyPrint, canonical)

public fun AnyFrame.writeJson(writer: Appendable, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writer.append(toJson(prettyPrint, canonical))
}

public fun AnyRow.writeJson(file: File, prettyPrint: Boolean = false, canonical: Boolean = false) {
    file.writeText(toJson(prettyPrint, canonical))
}

public fun AnyRow.writeJson(path: String, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writeJson(File(path), prettyPrint, canonical)
}

public fun AnyRow.writeJson(writer: Appendable, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writer.append(toJson(prettyPrint, canonical))
}
