package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public typealias AnyJdbcToDataFrameConverter = JdbcToDataFrameConverter<*, *, *>

/**
 * Represents all type information that can be retrieved from an SQL column.
 * This can be extended for your specific [DbType] if you need extra information.
 *
 * This class needs to be stateless, so it can be memoized in [AdvancedDbType.generateConverter].
 *
 *
 * @param J the type of the value coming from the JDBC driver.
 * @param D the type of the column values after preprocessing. Will be equal to [J] if [valuePreprocessor] is `null`.
 * @param P the type of the column values after postprocessing. Will be equal to [D] if [columnBuilder] is `null`.
 *
 * @property targetSchema the target schema of the column after running the optional
 *   [valuePreprocessor] and [columnBuilder]. Can be `null` if the target schema is dependent on the runtime input
 *   and thus cannot be determined from input types alone.
 * @property valuePreprocessor an optional function that converts values from [java.sql.ResultSet.getObject]
 *   to a cell/row suitable to be put into a [DataColumn].
 * @property columnBuilder an optional function that converts a [List] with values of type [D]
 *   to a [DataColumn] of with values of type [P].
 */
public open class JdbcToDataFrameConverter<J, D, P>(
    public open val expectedJdbcType: KType,
    public open val preprocessedValueType: KType,
    public open val targetSchema: ColumnSchema?,
    public open val resultSetReader: DbResultSetReader<J>?,
    public open val valuePreprocessor: DbValuePreprocessor<J, D>?,
    public open val columnBuilder: DbColumnBuilder<D, P>?,
) {
    public open fun getValueFromResultSetOrElse(rs: ResultSet, columnIndex: Int, default: () -> J): J {
        resultSetReader?.let { reader ->
            return reader.getValue(rs, columnIndex)
        }
        return default()
    }

    @Suppress("UNCHECKED_CAST")
    public open fun preprocessOrCast(value: J): D {
        valuePreprocessor?.let { valuePreprocessor ->
            return valuePreprocessor.preprocess(value)
        }
        return value as D
    }

    public open fun buildDataColumnOrNull(name: String, values: List<D>, inferNullability: Boolean): DataColumn<P>? {
        columnBuilder?.let { columnPostprocessor ->
            return columnPostprocessor.buildDataColumn(name, values, inferNullability)
        }
        return null
    }
}

@Suppress("UNCHECKED_CAST")
public fun <J, D, P> JdbcToDataFrameConverter<*, *, *>.cast(): JdbcToDataFrameConverter<J, D, P> =
    this as JdbcToDataFrameConverter<J, D, P>

public fun JdbcToDataFrameConverter<*, *, *>.castToAny(): JdbcToDataFrameConverter<Any?, Any?, Any?> = cast()

// region generic constructors

public fun <J, D, P> jdbcToDfConverterWithProcessingFor(
    jdbcSourceType: KType,
    preprocessedValueType: KType, // = jdbcSourceType
    targetSchema: ColumnSchema?, // = ColumnSchema.Value(preprocessedValueType)
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
    columnBuilder: DbColumnBuilder<D?, P?>?,
): JdbcToDataFrameConverter<J?, D?, P?> =
    JdbcToDataFrameConverter(
        expectedJdbcType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = columnBuilder,
    )

public inline fun <reified J, reified D, P> jdbcToDfConverterWithProcessingFor(
    isNullable: Boolean,
    jdbcSourceType: KType = typeOf<J?>().withNullability(isNullable),
    preprocessedValueType: KType = typeOf<D?>().withNullability(isNullable),
    targetSchema: ColumnSchema?,
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
    columnBuilder: DbColumnBuilder<D?, P?>?,
): JdbcToDataFrameConverter<J?, D?, P?> =
    jdbcToDfConverterWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = columnBuilder,
    )

public fun <J> jdbcToDfConverterFor(
    jdbcSourceType: KType,
    preprocessedValueType: KType,
    targetSchema: ColumnSchema?,
    resultSetReader: DbResultSetReader<J?>? = null,
): JdbcToDataFrameConverter<J?, J?, J?> =
    jdbcToDfConverterWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = null,
        columnBuilder = null,
    )

public fun <J, D> jdbcToDfConverterWithPreprocessingFor(
    jdbcSourceType: KType,
    preprocessedValueType: KType,
    targetSchema: ColumnSchema?,
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
): JdbcToDataFrameConverter<J?, D?, D?> =
    jdbcToDfConverterWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = null,
    )

public inline fun <reified J, reified D> jdbcToDfConverterWithPreprocessingFor(
    isNullable: Boolean,
    jdbcSourceType: KType = typeOf<J?>().withNullability(isNullable),
    preprocessedValueType: KType = typeOf<D?>().withNullability(isNullable),
    targetSchema: ColumnSchema?,
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
): JdbcToDataFrameConverter<J?, D?, D?> =
    jdbcToDfConverterWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = null,
    )

public fun <J, P> jdbcToDfConverterWithPostprocessingFor(
    jdbcSourceType: KType,
    targetSchema: ColumnSchema?,
    resultSetReader: DbResultSetReader<J?>? = null,
    columnBuilder: DbColumnBuilder<J?, P?>?,
): JdbcToDataFrameConverter<J?, J?, P?> =
    jdbcToDfConverterWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = jdbcSourceType,
        targetSchema = targetSchema,
        resultSetReader = resultSetReader,
        valuePreprocessor = null,
        columnBuilder = columnBuilder,
    )

// endregion

// region ValueColumn constructors

public fun <J> jdbcToDfConverterForValueColumnOf(
    kType: KType,
    resultSetReader: DbResultSetReader<J?>? = null,
): JdbcToDataFrameConverter<J?, J?, J?> =
    jdbcToDfConverterFor(
        jdbcSourceType = kType,
        preprocessedValueType = kType,
        targetSchema = ColumnSchema.Value(kType),
        resultSetReader = resultSetReader,
    )

public inline fun <reified J> jdbcToDfConverterForValueColumnOf(
    isNullable: Boolean,
    targetColumnType: KType = typeOf<J?>().withNullability(isNullable),
    resultSetReader: DbResultSetReader<J?>? = null,
): JdbcToDataFrameConverter<J?, J?, J?> = jdbcToDfConverterForValueColumnOf(targetColumnType, resultSetReader)

public fun <J, D> jdbcToDfConverterWithPreprocessingForValueColumnOf(
    jdbcSourceType: KType,
    preprocessedValueType: KType,
    targetColumnType: KType,
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
): JdbcToDataFrameConverter<J?, D?, D?> =
    jdbcToDfConverterWithPreprocessingFor(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetSchema = ColumnSchema.Value(targetColumnType),
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
    )

public inline fun <reified J, reified D> jdbcToDfConverterWithPreprocessingForValueColumnOf(
    isNullable: Boolean,
    jdbcSourceType: KType = typeOf<J?>().withNullability(isNullable),
    preprocessedValueType: KType = typeOf<D?>().withNullability(isNullable),
    targetColumnType: KType = preprocessedValueType,
    resultSetReader: DbResultSetReader<J?>? = null,
    valuePreprocessor: DbValuePreprocessor<J?, D?>?,
): JdbcToDataFrameConverter<J?, D?, D?> =
    jdbcToDfConverterWithPreprocessingForValueColumnOf(
        jdbcSourceType = jdbcSourceType,
        preprocessedValueType = preprocessedValueType,
        targetColumnType = targetColumnType,
        resultSetReader = resultSetReader,
        valuePreprocessor = valuePreprocessor,
    )

public fun <J, P> jdbcToDfConverterWithPostprocessingForValueColumnOf(
    jdbcSourceType: KType,
    targetColumnType: KType,
    resultSetReader: DbResultSetReader<J?>? = null,
    columnPostprocessor: DbColumnBuilder<J?, P?>?,
): JdbcToDataFrameConverter<J?, J?, P?> =
    jdbcToDfConverterWithPostprocessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = ColumnSchema.Value(targetColumnType),
        resultSetReader = resultSetReader,
        columnBuilder = columnPostprocessor,
    )

public inline fun <reified J, reified P> jdbcToDfConverterWithPostprocessingForValueColumnOf(
    isNullable: Boolean,
    jdbcSourceType: KType = typeOf<J?>().withNullability(isNullable),
    targetColumnType: KType = typeOf<P?>().withNullability(isNullable),
    resultSetReader: DbResultSetReader<J?>? = null,
    columnPostprocessor: DbColumnBuilder<J?, P?>?,
): JdbcToDataFrameConverter<J?, J?, P?> =
    jdbcToDfConverterWithPostprocessingForValueColumnOf(
        jdbcSourceType = jdbcSourceType,
        targetColumnType = targetColumnType,
        resultSetReader = resultSetReader,
        columnPostprocessor = columnPostprocessor,
    )

// endregion

public fun interface DbResultSetReader<out J> {

    public fun getValue(rs: ResultSet, columnIndex: Int): J
}

/**
 * This preprocessor can be created for types where you want to convert the values
 * coming from [java.sql.ResultSet.getObject] to a different type more suitable to be put in a [DataColumn]
 *
 * @param J the type of the value coming from the JDBC driver.
 * @param D the type of the column values after preprocessing.
 */
public fun interface DbValuePreprocessor<in J, out D> {

    /**
     * Converts the given [jdbcValue]: [J] to a [D].
     *
     * If you intend to create a [org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * return a [org.jetbrains.kotlinx.dataframe.DataRow] here.
     *
     * If you intend to create a [org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
     * return a [org.jetbrains.kotlinx.dataframe.DataFrame] here.
     */
    public fun preprocess(jdbcValue: J): D
}

@Suppress("UNCHECKED_CAST")
public fun <J, D> DbValuePreprocessor<*, *>.cast(): DbValuePreprocessor<J, D> = this as DbValuePreprocessor<J, D>

public fun DbValuePreprocessor<*, *>.castToAny(): DbValuePreprocessor<Any?, Any?> = cast()

/**
 * @param D the type of the column values before entering the column.
 * @param P the type of the column values after entering the column.
 */
public fun interface DbColumnBuilder<in D, out P> {

    /**
     * Converts the given [values]: [DataColumn] with values of type [D] to a [DataColumn] of with values of type [P].
     */
    public fun buildDataColumn(name: String, values: List<D>, inferNullability: Boolean): DataColumn<P>
}

@Suppress("UNCHECKED_CAST")
public fun <D, P> DbColumnBuilder<*, *>.cast(): DbColumnBuilder<D, P> = this as DbColumnBuilder<D, P>

public fun DbColumnBuilder<*, *>.castToAny(): DbColumnBuilder<Any?, Any?> = cast()
