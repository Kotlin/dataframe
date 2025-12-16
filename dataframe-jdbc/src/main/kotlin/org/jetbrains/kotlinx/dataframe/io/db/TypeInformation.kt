package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public typealias AnyTypeInformation = TypeInformation<*, *, *>

/**
 * Represents all type information that can be retrieved from an SQL column.
 * This can be extended for your specific [DbType] if you need extra information.
 *
 * This class needs to be stateless, so it can be memoized in [DbType.getOrGenerateTypeInformation].
 *
 *
 * @param J the type of the value coming from the JDBC driver.
 * @param D the type of the column values after preprocessing. Will be equal to [J] if [valuePreprocessor] is `null`.
 * @param P the type of the column values after postprocessing. Will be equal to [D] if [columnBuilder] is `null`.
 *
 * @property targetSchema the target schema of the column after running the optional
 *   [valuePreprocessor] and [columnBuilder].
 * @property valuePreprocessor an optional function that converts values from [java.sql.ResultSet.getObject]
 *   to a cell/row suitable to be put into a [DataColumn].
 * @property columnBuilder an optional function that converts a [List] with values of type [D]
 *   to a [DataColumn] of with values of type [P].
 */
public open class TypeInformation<J : Any, D : Any, P : Any>(
    public open val jdbcSourceType: KType,
    public open val targetSchema: ColumnSchema,
    public open val valuePreprocessor: DbValuePreprocessor<J, D>?,
    public open val columnBuilder: DbColumnBuilder<D, P>?,
) {
    public open fun preprocess(value: J?): D? {
        valuePreprocessor?.let { valuePreprocessor ->
            return valuePreprocessor.preprocess(value, this)
        }
        return value as D?
    }

    public open fun buildDataColumn(name: String, values: List<D?>, inferNullability: Boolean): DataColumn<P?> {
        columnBuilder?.let { columnPostprocessor ->
            return columnPostprocessor.buildDataColumn(name, values, this, inferNullability)
        }
        return when (val schema = targetSchema) {
            is ColumnSchema.Value ->
                DataColumn.createValueColumn(
                    name = name,
                    values = values,
                    infer = if (inferNullability) Infer.Nulls else Infer.None,
                    type = schema.type,
                ).cast()

            // TODO, this case should be avoided.
            //  Creating `n` DataRows is heavy!
            is ColumnSchema.Group ->
                DataColumn.createColumnGroup(
                    name = name,
                    df = (values as List<AnyRow>).toDataFrame(),
                ).asDataColumn().cast()

            is ColumnSchema.Frame ->
                DataColumn.createFrameColumn(
                    name = name,
                    groups = values as List<AnyFrame>,
                    schema = lazy { schema.schema },
                ).cast()
        }
    }
}

public fun <J : Any, D : Any, P : Any> TypeInformation<*, *, *>.cast(): TypeInformation<J, D, P> =
    this as TypeInformation<J, D, P>

public fun TypeInformation<*, *, *>.castToAny(): TypeInformation<Any, Any, Any> = cast()

// region generic constructors

public fun <J : Any, D : Any, P : Any> typeInformationWithProcessingFor(
    jdbcSourceType: KType,
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
    columnBuilder: DbColumnBuilder<D, P>?,
): TypeInformation<J, D, P> =
    TypeInformation(
        jdbcSourceType = jdbcSourceType,
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = columnBuilder,
    )

public fun <J : Any> typeInformationFor(jdbcSourceType: KType, targetSchema: ColumnSchema): TypeInformation<J, J, J> =
    typeInformationWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnBuilder = null,
    )

public fun <J : Any, D : Any> typeInformationWithPreprocessingFor(
    jdbcSourceType: KType,
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnBuilder = null,
    )

public fun <J : Any, P : Any> typeInformationWithPostprocessingFor(
    jdbcSourceType: KType,
    targetSchema: ColumnSchema,
    columnBuilder: DbColumnBuilder<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithProcessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnBuilder = columnBuilder,
    )

// endregion

// region ValueColumn constructors

public fun <J : Any> typeInformationForValueColumnOf(kType: KType): TypeInformation<J, J, J> =
    typeInformationFor(jdbcSourceType = kType, targetSchema = ColumnSchema.Value(kType))

public inline fun <reified J : Any> typeInformationForValueColumnOf(isNullable: Boolean): TypeInformation<J, J, J> =
    typeInformationForValueColumnOf(typeOf<J>().withNullability(isNullable))

public fun <J : Any, D : Any> typeInformationWithPreprocessingForValueColumnOf(
    jdbcSourceType: KType,
    targetColumnType: KType,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithPreprocessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = ColumnSchema.Value(targetColumnType),
        valuePreprocessor = valuePreprocessor,
    )

public inline fun <reified J : Any, reified D : Any> typeInformationWithPreprocessingForValueColumnOf(
    isNullable: Boolean,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithPreprocessingForValueColumnOf(
        jdbcSourceType = typeOf<J>().withNullability(isNullable),
        targetColumnType = typeOf<D>().withNullability(isNullable),
        valuePreprocessor = valuePreprocessor,
    )

public fun <J : Any, P : Any> typeInformationWithPostprocessingForValueColumnOf(
    jdbcSourceType: KType,
    targetColumnType: KType,
    columnPostprocessor: DbColumnBuilder<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithPostprocessingFor(
        jdbcSourceType = jdbcSourceType,
        targetSchema = ColumnSchema.Value(targetColumnType),
        columnBuilder = columnPostprocessor,
    )

public inline fun <reified J : Any, reified P : Any> typeInformationWithPostprocessingForValueColumnOf(
    isNullable: Boolean,
    columnPostprocessor: DbColumnBuilder<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithPostprocessingForValueColumnOf(
        jdbcSourceType = typeOf<J>().withNullability(isNullable),
        targetColumnType = typeOf<P>().withNullability(isNullable),
        columnPostprocessor = columnPostprocessor,
    )

// endregion

/**
 * This preprocessor can be created for types where you want to convert the values
 * coming from [java.sql.ResultSet.getObject] to a different type more suitable to be put in a [DataColumn]
 *
 * @param J the type of the value coming from the JDBC driver.
 * @param D the type of the column values after preprocessing.
 */
public fun interface DbValuePreprocessor<in J : Any, out D : Any> {

    /**
     * Converts the given [jdbcValue]: [J] to a [D].
     *
     * If you intend to create a [org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * return a [org.jetbrains.kotlinx.dataframe.DataRow] here.
     *
     * If you intend to create a [org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
     * return a [org.jetbrains.kotlinx.dataframe.DataFrame] here.
     */
    public fun preprocess(jdbcValue: J?, typeInformation: TypeInformation<@UnsafeVariance J, @UnsafeVariance D, *>): D?
}

public fun <J : Any, D : Any> DbValuePreprocessor<*, *>.cast(): DbValuePreprocessor<J, D> =
    this as DbValuePreprocessor<J, D>

public fun DbValuePreprocessor<*, *>.castToAny(): DbValuePreprocessor<Any, Any> = cast()

/**
 * @param D the type of the column values before postprocessing.
 * @param P the type of the column values after postprocessing.
 */
public fun interface DbColumnBuilder<in D : Any, out P : Any> {

    /**
     * Converts the given [values]: [DataColumn] with values of type [D] to a [DataColumn] of with values of type [P].
     */
    public fun buildDataColumn(
        name: String,
        values: List<D?>,
        typeInformation: TypeInformation<*, @UnsafeVariance D, @UnsafeVariance P>,
        inferNullability: Boolean,
    ): DataColumn<P?>
}

public fun <D : Any, P : Any> DbColumnBuilder<*, *>.cast(): DbColumnBuilder<D, P> = this as DbColumnBuilder<D, P>

public fun DbColumnBuilder<*, *>.castToAny(): DbColumnBuilder<Any, Any> = cast()
