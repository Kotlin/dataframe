package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
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
 * @param P the type of the column values after postprocessing. Will be equal to [D] if [columnPostprocessor] is `null`.
 *
 * @property targetSchema the target schema of the column after running the optional
 *   [valuePreprocessor] and [columnPostprocessor].
 * @property valuePreprocessor an optional function that converts values from [java.sql.ResultSet.getObject]
 *   to a cell/row suitable to be put into a [DataColumn].
 * @property columnPostprocessor an optional function that converts a [DataColumn] with values of type [D]
 *   to a [DataColumn] of with values of type [P].
 */
public open class TypeInformation<J : Any, D : Any, P : Any>(
    public open val targetSchema: ColumnSchema,
    public open val valuePreprocessor: DbValuePreprocessor<J, D>?,
    public open val columnPostprocessor: DbColumnPostprocessor<D, P>?,
) {
    public open fun preprocess(value: J?): D? {
        valuePreprocessor?.let { valuePreprocessor ->
            return valuePreprocessor.preprocess(value, this)
        }
        return value as D?
    }

    public open fun postprocess(column: DataColumn<D?>): DataColumn<P?> {
        columnPostprocessor?.let { columnPostprocessor ->
            return columnPostprocessor.postprocess(column, this)
        }
        return column.cast()
    }
}

public fun <J : Any, D : Any, P : Any> TypeInformation<*, *, *>.cast(): TypeInformation<J, D, P> =
    this as TypeInformation<J, D, P>

public fun TypeInformation<*, *, *>.castToAny(): TypeInformation<Any, Any, Any> = cast()

// region generic constructors

public fun <J : Any, D : Any, P : Any> typeInformationWithProcessingFor(
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
    columnPostprocessor: DbColumnPostprocessor<D, P>?,
): TypeInformation<J, D, P> =
    TypeInformation(
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnPostprocessor = columnPostprocessor,
    )

public fun <J : Any> typeInformationFor(targetSchema: ColumnSchema): TypeInformation<J, J, J> =
    typeInformationWithProcessingFor(
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnPostprocessor = null,
    )

public fun <J : Any, D : Any> typeInformationWithPreprocessingFor(
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithProcessingFor(
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnPostprocessor = null,
    )

public fun <J : Any, P : Any> typeInformationWithPostprocessingFor(
    targetSchema: ColumnSchema,
    columnPostprocessor: DbColumnPostprocessor<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithProcessingFor(
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnPostprocessor = columnPostprocessor,
    )

// endregion

// region ValueColumn constructors

public fun <J : Any> typeInformationForValueColumnOf(kType: KType): TypeInformation<J, J, J> =
    typeInformationFor(targetSchema = ColumnSchema.Value(kType))

public inline fun <reified J : Any> typeInformationForValueColumnOf(isNullable: Boolean): TypeInformation<J, J, J> =
    typeInformationForValueColumnOf(typeOf<J>().withNullability(isNullable))

public fun <J : Any, D : Any> typeInformationWithPreprocessingForValueColumnOf(
    targetColumnType: KType,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithPreprocessingFor(
        targetSchema = ColumnSchema.Value(targetColumnType),
        valuePreprocessor = valuePreprocessor,
    )

public inline fun <J : Any, reified D : Any> typeInformationWithPreprocessingForValueColumnOf(
    isNullable: Boolean,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): TypeInformation<J, D, D> =
    typeInformationWithPreprocessingForValueColumnOf(
        targetColumnType = typeOf<D>().withNullability(isNullable),
        valuePreprocessor = valuePreprocessor,
    )

public fun <J : Any, P : Any> typeInformationWithPostprocessingForValueColumnOf(
    targetColumnType: KType,
    columnPostprocessor: DbColumnPostprocessor<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithPostprocessingFor(
        targetSchema = ColumnSchema.Value(targetColumnType),
        columnPostprocessor = columnPostprocessor,
    )

public inline fun <J : Any, reified P : Any> typeInformationWithPostprocessingForValueColumnOf(
    isNullable: Boolean,
    columnPostprocessor: DbColumnPostprocessor<J, P>?,
): TypeInformation<J, J, P> =
    typeInformationWithPostprocessingForValueColumnOf(
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
public fun interface DbColumnPostprocessor<in D : Any, out P : Any> {

    /**
     * Converts the given [column]: [DataColumn] with values of type [D] to a [DataColumn] of with values of type [P].
     */
    public fun postprocess(
        column: DataColumn<D?>,
        typeInformation: TypeInformation<*, @UnsafeVariance D, @UnsafeVariance P>,
    ): DataColumn<P?>
}

public fun <D : Any, P : Any> DbColumnPostprocessor<*, *>.cast(): DbColumnPostprocessor<D, P> =
    this as DbColumnPostprocessor<D, P>

public fun DbColumnPostprocessor<*, *>.castToAny(): DbColumnPostprocessor<Any, Any> = cast()
