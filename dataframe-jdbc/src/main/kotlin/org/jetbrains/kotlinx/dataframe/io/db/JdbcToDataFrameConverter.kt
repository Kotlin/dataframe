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
 * Use [jdbcToDfConverterFor] to create an instance.
 *
 * This API is experimental and subject to change.
 *
 * @param J the type of the value coming from the JDBC driver, [expectedJdbcType].
 * @param D the type of the column values after preprocessing, [preprocessedValueType]. Will be equal to [J] if [valuePreprocessor] is `null`.
 * @param P the type of the column values after postprocessing, comparable to [DataColumn][DataColumn]`<`[P][P]`>` of [targetSchema].
 *   Will be equal to [D] if [columnBuilder] is `null`.
 *
 * @property targetSchema the target schema of the column after running the optional
 *   [valuePreprocessor] and [columnBuilder]. Can be `null` if the target schema is dependent on the runtime input
 *   and thus cannot be determined from input types alone.
 * @property valuePreprocessor an optional function that converts values from [ResultSet.getObject]
 *   to a cell/row suitable to be put into a [DataColumn].
 * @property columnBuilder an optional function that converts a [List] with values of type [D]
 *   to a [DataColumn] of with values of type [P].
 */
public class JdbcToDataFrameConverter<J, D, P>(
    public val expectedJdbcType: KType,
    public val resultSetReader: DbResultSetReader<J>?,
    public val preprocessedValueType: KType,
    public val valuePreprocessor: DbValuePreprocessor<J, D>?,
    public val targetSchema: ColumnSchema?,
    public val columnBuilder: DbColumnBuilder<D, P>?,
) {
    public fun getValueFromResultSetOrElse(rs: ResultSet, columnIndex: Int, default: () -> J): J {
        resultSetReader?.let { reader ->
            return reader.getValue(rs, columnIndex)
        }
        return default()
    }

    @Suppress("UNCHECKED_CAST")
    public fun preprocessOrCast(value: J): D {
        valuePreprocessor?.let { valuePreprocessor ->
            return valuePreprocessor.preprocess(value)
        }
        return value as D
    }

    public fun buildDataColumnOrNull(name: String, values: List<D>, inferNullability: Boolean): DataColumn<P>? {
        columnBuilder?.let { columnPostprocessor ->
            return columnPostprocessor.buildDataColumn(name, values, inferNullability)
        }
        return null
    }

    public fun copy(
        expectedJdbcType: KType = this.expectedJdbcType,
        resultSetReader: DbResultSetReader<J>? = this.resultSetReader,
        preprocessedValueType: KType = this.preprocessedValueType,
        valuePreprocessor: DbValuePreprocessor<J, D>? = this.valuePreprocessor,
        targetSchema: ColumnSchema? = this.targetSchema,
        columnBuilder: DbColumnBuilder<D, P>? = this.columnBuilder,
    ): JdbcToDataFrameConverter<J, D, P> =
        JdbcToDataFrameConverter(
            expectedJdbcType = expectedJdbcType,
            resultSetReader = resultSetReader,
            preprocessedValueType = preprocessedValueType,
            valuePreprocessor = valuePreprocessor,
            targetSchema = targetSchema,
            columnBuilder = columnBuilder,
        )
}

@Suppress("UNCHECKED_CAST")
public fun <J, D, P> JdbcToDataFrameConverter<*, *, *>.cast(): JdbcToDataFrameConverter<J, D, P> =
    this as JdbcToDataFrameConverter<J, D, P>

public fun JdbcToDataFrameConverter<*, *, *>.castToAny(): JdbcToDataFrameConverter<Any?, Any?, Any?> = cast()

// region builders

public fun <J> jdbcToDfConverterFor(jdbcSourceType: KType): JdbcToDataFrameConverter<J?, J?, J?> =
    JdbcToDataFrameConverter(
        expectedJdbcType = jdbcSourceType,
        resultSetReader = null,
        preprocessedValueType = jdbcSourceType,
        valuePreprocessor = null,
        targetSchema = ColumnSchema.Value(jdbcSourceType),
        columnBuilder = null,
    )

public inline fun <reified J> jdbcToDfConverterFor(
    isNullable: Boolean = typeOf<J>().isMarkedNullable,
): JdbcToDataFrameConverter<J?, J?, J?> =
    jdbcToDfConverterFor(
        jdbcSourceType = typeOf<J?>().withNullability(isNullable),
    )

public fun <J, D, P> JdbcToDataFrameConverter<J, D, P>.withResultReader(
    resultSetReader: DbResultSetReader<J>?,
): JdbcToDataFrameConverter<J, D, P> = copy(resultSetReader = resultSetReader)

public fun <J, D> JdbcToDataFrameConverter<J, *, *>.withPreprocessor(
    preprocessedValueType: KType,
    canIntroduceNulls: Boolean = false,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): JdbcToDataFrameConverter<J, D, D> {
    // carry definitely-not-null knowledge from expectedJdbcType to preprocessedValueType
    // if the valuePreprocessor does not introduce nulls itself
    val preprocessedValueType =
        if (!expectedJdbcType.isMarkedNullable && !canIntroduceNulls) {
            preprocessedValueType.withNullability(false)
        } else {
            preprocessedValueType
        }
    return cast<J, D, D>()
        .copy(
            preprocessedValueType = preprocessedValueType,
            valuePreprocessor = valuePreprocessor,
            targetSchema = ColumnSchema.Value(preprocessedValueType),
            columnBuilder = null,
        )
}

public inline fun <J, reified D> JdbcToDataFrameConverter<J, *, *>.withPreprocessor(
    canIntroduceNulls: Boolean = false,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): JdbcToDataFrameConverter<J, D, D> =
    withPreprocessor(
        canIntroduceNulls = canIntroduceNulls,
        preprocessedValueType = typeOf<D>(),
        valuePreprocessor = valuePreprocessor,
    )

public fun <J, D, P> JdbcToDataFrameConverter<J, D, P>.withTargetSchema(
    targetSchema: ColumnSchema?,
): JdbcToDataFrameConverter<J, D, P> =
    withColumnBuilder(
        targetSchema = targetSchema,
        canIntroduceNulls = false,
        columnBuilder = null,
    )

public fun <J, D, P> JdbcToDataFrameConverter<J, D, *>.withColumnBuilder(
    targetSchema: ColumnSchema?,
    canIntroduceNulls: Boolean = false,
    columnBuilder: DbColumnBuilder<D, P>?,
): JdbcToDataFrameConverter<J, D, P> {
    // carry definitely-not-null knowledge from preprocessedValueType to targetSchema
    // if the columnBuilder does not introduce nulls itself
    val targetSchema =
        if (targetSchema != null && !preprocessedValueType.isMarkedNullable && !canIntroduceNulls) {
            when (targetSchema) {
                is ColumnSchema.Value ->
                    ColumnSchema.Value(targetSchema.type.withNullability(false))

                is ColumnSchema.Group -> targetSchema

                is ColumnSchema.Frame -> ColumnSchema.Frame(
                    schema = targetSchema.schema,
                    nullable = false,
                    contentType = targetSchema.contentType,
                )
            }
        } else {
            targetSchema
        }
    return cast<J, D, P>()
        .copy(
            targetSchema = targetSchema,
            columnBuilder = columnBuilder,
        )
}

// endregion

public fun interface DbResultSetReader<out J> {

    public fun getValue(rs: ResultSet, columnIndex: Int): J
}

/**
 * This preprocessor can be created for types where you want to convert the values
 * coming from [ResultSet.getObject] to a different type more suitable to be put in a [DataColumn]
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
