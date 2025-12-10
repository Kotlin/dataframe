package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema

public typealias AnyDbColumnTypeInformation = DbColumnTypeInformation<*, *, *>

/**
 * Represents all type information that can be retrieved from an SQL column.
 * This can be extended for your specific [DbType2] if you need extra information.
 *
 * @property targetSchema the target schema of the column after running the optional
 *   [valuePreprocessor] and [columnPostprocessor].
 * @property valuePreprocessor an optional function that converts values from [java.sql.ResultSet.getObject]
 *   to a cell/row suitable to be put into a [org.jetbrains.kotlinx.dataframe.DataColumn].
 * @property columnPostprocessor an optional function that converts a [org.jetbrains.kotlinx.dataframe.DataColumn] with values of type [D]
 *   to a [org.jetbrains.kotlinx.dataframe.DataColumn] of with values of type [P].
 */
public open class DbColumnTypeInformation<J, D, P>(
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

    public open fun postprocess(column: DataColumn<D>): DataColumn<P> {
        columnPostprocessor?.let { columnPostprocessor ->
            return columnPostprocessor.postprocess(column, this)
        }
        return column.cast()
    }
}

public fun <J, D, P> DbColumnTypeInformation<*, *, *>.cast(): DbColumnTypeInformation<J, D, P> =
    this as DbColumnTypeInformation<J, D, P>

public fun <T> dbColumnTypeInformation(targetSchema: ColumnSchema): DbColumnTypeInformation<T, T, T> =
    DbColumnTypeInformation(
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnPostprocessor = null,
    )

public fun <J, D> dbColumnTypeInformationWithPreprocessing(
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
): DbColumnTypeInformation<J, D, D> =
    DbColumnTypeInformation(
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnPostprocessor = null,
    )

public fun <J, P> dbColumnTypeInformationWithPostprocessing(
    targetSchema: ColumnSchema,
    columnPostprocessor: DbColumnPostprocessor<J, P>?,
): DbColumnTypeInformation<J, J, P> =
    DbColumnTypeInformation(
        targetSchema = targetSchema,
        valuePreprocessor = null,
        columnPostprocessor = columnPostprocessor,
    )

public fun <J, D, P> dbColumnTypeInformation(
    targetSchema: ColumnSchema,
    valuePreprocessor: DbValuePreprocessor<J, D>?,
    columnPostprocessor: DbColumnPostprocessor<D, P>?,
): DbColumnTypeInformation<J, D, P> =
    DbColumnTypeInformation(
        targetSchema = targetSchema,
        valuePreprocessor = valuePreprocessor,
        columnPostprocessor = columnPostprocessor,
    )

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
    public fun preprocess(
        jdbcValue: J?,
        dbColumnTypeInformation: DbColumnTypeInformation<@UnsafeVariance J, @UnsafeVariance D, *>,
    ): D?
}

public fun <J, D> DbValuePreprocessor<*, *>.cast(): DbValuePreprocessor<J, D> = this as DbValuePreprocessor<J, D>

/**
 * @param D the type of the column values before postprocessing.
 * @param P the type of the column values after postprocessing.
 */
public fun interface DbColumnPostprocessor<in D, out P> {

    /**
     * Converts the given [column]: [DataColumn] with values of type [D] to a [DataColumn] of with values of type [P].
     */
    public fun postprocess(
        column: DataColumn<D>,
        dbColumnTypeInformation: DbColumnTypeInformation<*, @UnsafeVariance D, @UnsafeVariance P>,
    ): DataColumn<P>
}

public fun <D, P> DbColumnPostprocessor<*, *>.cast(): DbColumnPostprocessor<D, P> = this as DbColumnPostprocessor<D, P>
