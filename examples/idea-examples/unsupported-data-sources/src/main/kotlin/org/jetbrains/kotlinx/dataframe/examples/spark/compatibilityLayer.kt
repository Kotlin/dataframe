package org.jetbrains.kotlinx.dataframe.examples.spark

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.ArrayType
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Decimal
import org.apache.spark.sql.types.DecimalType
import org.apache.spark.sql.types.MapType
import org.apache.spark.sql.types.StructType
import org.apache.spark.unsafe.types.CalendarInterval
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region Spark to DataFrame

/**
 * Converts an untyped Spark [Dataset] (Dataframe) to a Kotlin [DataFrame].
 * [StructTypes][StructType] are converted to [ColumnGroups][ColumnGroup].
 *
 * DataFrame supports type inference to do the conversion automatically.
 * This is usually fine for smaller data sets, but when working with larger datasets, a type map might be a good idea.
 * See [convertToDataFrame] for more information.
 */
fun Dataset<Row>.convertToDataFrameByInference(
    schema: StructType = schema(),
    prefix: List<String> = emptyList(),
): AnyFrame {
    val columns = schema.fields().map { field ->
        val name = field.name()
        when (val dataType = field.dataType()) {
            is StructType ->
                // a column group can be easily created from a dataframe and a name
                DataColumn.createColumnGroup(
                    name = name,
                    df = this.convertToDataFrameByInference(dataType, prefix + name),
                )

            else ->
                // we can use DataFrame type inference to create a column with the correct type
                // from Spark we use `select()` to select a single column
                // and `collectAsList()` to get all the values in a list of single-celled rows
                DataColumn.createByInference(
                    name = name,
                    values = this.select((prefix + name).joinToString("."))
                        .collectAsList()
                        .map { it[0] },
                    suggestedType = TypeSuggestion.Infer,
                    // Spark provides nullability :) you can leave this out if you want this to be inferred too
                    nullable = field.nullable(),
                )
        }
    }
    return columns.toDataFrame()
}

/**
 * Converts an untyped Spark [Dataset] (Dataframe) to a Kotlin [DataFrame].
 * [StructTypes][StructType] are converted to [ColumnGroups][ColumnGroup].
 *
 * This version uses a [type-map][DataType.convertToDataFrame] to convert the schemas with a fallback to inference.
 * For smaller data sets, inference is usually fine too.
 * See [convertToDataFrameByInference] for more information.
 */
fun Dataset<Row>.convertToDataFrame(schema: StructType = schema(), prefix: List<String> = emptyList()): AnyFrame {
    val columns = schema.fields().map { field ->
        val name = field.name()
        when (val dataType = field.dataType()) {
            is StructType ->
                // a column group can be easily created from a dataframe and a name
                DataColumn.createColumnGroup(
                    name = name,
                    df = convertToDataFrame(dataType, prefix + name),
                )

            else ->
                // we create a column with the correct type using our type-map with fallback to inference
                // from Spark we use `select()` to select a single column
                // and `collectAsList()` to get all the values in a list of single-celled rows
                DataColumn.createByInference(
                    name = name,
                    values = select((prefix + name).joinToString("."))
                        .collectAsList()
                        .map { it[0] },
                    suggestedType =
                        dataType.convertToDataFrame()
                            ?.let(TypeSuggestion::Use)
                            ?: TypeSuggestion.Infer, // fallback to inference if needed
                    nullable = field.nullable(),
                )
        }
    }
    return columns.toDataFrame()
}

/**
 * Returns the corresponding [Kotlin type][KType] for a given Spark [DataType].
 *
 * This list may be incomplete, but it can at least give you a good start.
 *
 * @return The [KType] that corresponds to the Spark [DataType], or null if no matching [KType] is found.
 */
fun DataType.convertToDataFrame(): KType? =
    when {
        this == DataTypes.ByteType -> typeOf<Byte>()

        this == DataTypes.ShortType -> typeOf<Short>()

        this == DataTypes.IntegerType -> typeOf<Int>()

        this == DataTypes.LongType -> typeOf<Long>()

        this == DataTypes.BooleanType -> typeOf<Boolean>()

        this == DataTypes.FloatType -> typeOf<Float>()

        this == DataTypes.DoubleType -> typeOf<Double>()

        this == DataTypes.StringType -> typeOf<String>()

        this == DataTypes.DateType -> typeOf<Date>()

        this == DataTypes.TimestampType -> typeOf<Timestamp>()

        this is DecimalType -> typeOf<Decimal>()

        this == DataTypes.CalendarIntervalType -> typeOf<CalendarInterval>()

        this == DataTypes.NullType -> nullableNothingType

        this == DataTypes.BinaryType -> typeOf<ByteArray>()

        this is ArrayType -> {
            when (elementType()) {
                DataTypes.ShortType -> typeOf<ShortArray>()
                DataTypes.IntegerType -> typeOf<IntArray>()
                DataTypes.LongType -> typeOf<LongArray>()
                DataTypes.FloatType -> typeOf<FloatArray>()
                DataTypes.DoubleType -> typeOf<DoubleArray>()
                DataTypes.BooleanType -> typeOf<BooleanArray>()
                else -> null
            }
        }

        this is MapType -> {
            val key = keyType().convertToDataFrame() ?: return null
            val value = valueType().convertToDataFrame() ?: return null
            Map::class.createType(
                listOf(
                    KTypeProjection.invariant(key),
                    KTypeProjection.invariant(value.withNullability(valueContainsNull())),
                ),
            )
        }

        else -> null
    }

// endregion

// region DataFrame to Spark

/**
 * Converts the [DataFrame] to a Spark [Dataset] of [Rows][Row] using the provided [SparkSession] and [JavaSparkContext].
 *
 * Spark needs both the data and the schema to be converted to create a correct [Dataset],
 * so we need to map our types somehow.
 *
 * @param spark The [SparkSession] object to use for creating the [DataFrame].
 * @param sc The [JavaSparkContext] object to use for converting the [DataFrame] to [RDD][JavaRDD].
 * @return A [Dataset] of [Rows][Row] representing the converted DataFrame.
 */
fun DataFrame<*>.convertToSpark(spark: SparkSession, sc: JavaSparkContext): Dataset<Row> {
    // Convert each row to spark rows
    val rows = sc.parallelize(this.rows().map { it.convertToSpark() })
    // convert the data schema to a spark StructType
    val schema = this.schema().convertToSpark()
    return spark.createDataFrame(rows, schema)
}

/**
 * Converts a [DataRow] to a Spark [Row] object.
 *
 * @return The converted Spark [Row].
 */
fun DataRow<*>.convertToSpark(): Row =
    RowFactory.create(
        *values().map {
            when (it) {
                // a row can be nested inside another row if it's a column group
                is DataRow<*> -> it.convertToSpark()

                is DataFrame<*> -> error("nested dataframes are not supported")

                else -> it
            }
        }.toTypedArray(),
    )

/**
 * Converts a [DataFrameSchema] to a Spark [StructType].
 *
 * @return The converted Spark [StructType].
 */
fun DataFrameSchema.convertToSpark(): StructType =
    DataTypes.createStructType(
        this.columns.map { (name, schema) ->
            DataTypes.createStructField(name, schema.convertToSpark(), schema.nullable)
        },
    )

/**
 * Converts a [ColumnSchema] object to Spark [DataType].
 *
 * @return The Spark [DataType] corresponding to the given [ColumnSchema] object.
 * @throws IllegalArgumentException if the column type or kind is unknown.
 */
fun ColumnSchema.convertToSpark(): DataType =
    when (this) {
        is ColumnSchema.Value -> type.convertToSpark() ?: error("unknown data type: $type")
        is ColumnSchema.Group -> schema.convertToSpark()
        is ColumnSchema.Frame -> error("nested dataframes are not supported")
        else -> error("unknown column kind: $this")
    }

/**
 * Returns the corresponding Spark [DataType] for a given [Kotlin type][KType].
 *
 * This list may be incomplete, but it can at least give you a good start.
 *
 * @return The Spark [DataType] that corresponds to the [Kotlin type][KType], or null if no matching [DataType] is found.
 */
fun KType.convertToSpark(): DataType? =
    when {
        isSubtypeOf(typeOf<Byte?>()) -> DataTypes.ByteType

        isSubtypeOf(typeOf<Short?>()) -> DataTypes.ShortType

        isSubtypeOf(typeOf<Int?>()) -> DataTypes.IntegerType

        isSubtypeOf(typeOf<Long?>()) -> DataTypes.LongType

        isSubtypeOf(typeOf<Boolean?>()) -> DataTypes.BooleanType

        isSubtypeOf(typeOf<Float?>()) -> DataTypes.FloatType

        isSubtypeOf(typeOf<Double?>()) -> DataTypes.DoubleType

        isSubtypeOf(typeOf<String?>()) -> DataTypes.StringType

        isSubtypeOf(typeOf<LocalDate?>()) -> DataTypes.DateType

        isSubtypeOf(typeOf<Date?>()) -> DataTypes.DateType

        isSubtypeOf(typeOf<Timestamp?>()) -> DataTypes.TimestampType

        isSubtypeOf(typeOf<Instant?>()) -> DataTypes.TimestampType

        isSubtypeOf(typeOf<Decimal?>()) -> DecimalType.SYSTEM_DEFAULT()

        isSubtypeOf(typeOf<BigDecimal?>()) -> DecimalType.SYSTEM_DEFAULT()

        isSubtypeOf(typeOf<BigInteger?>()) -> DecimalType.SYSTEM_DEFAULT()

        isSubtypeOf(typeOf<CalendarInterval?>()) -> DataTypes.CalendarIntervalType

        isSubtypeOf(nullableNothingType) -> DataTypes.NullType

        isSubtypeOf(typeOf<ByteArray?>()) -> DataTypes.BinaryType

        isSubtypeOf(typeOf<ShortArray?>()) -> DataTypes.createArrayType(DataTypes.ShortType, false)

        isSubtypeOf(typeOf<IntArray?>()) -> DataTypes.createArrayType(DataTypes.IntegerType, false)

        isSubtypeOf(typeOf<LongArray?>()) -> DataTypes.createArrayType(DataTypes.LongType, false)

        isSubtypeOf(typeOf<FloatArray?>()) -> DataTypes.createArrayType(DataTypes.FloatType, false)

        isSubtypeOf(typeOf<DoubleArray?>()) -> DataTypes.createArrayType(DataTypes.DoubleType, false)

        isSubtypeOf(typeOf<BooleanArray?>()) -> DataTypes.createArrayType(DataTypes.BooleanType, false)

        isSubtypeOf(typeOf<Array<*>>()) ->
            error("non-primitive arrays are not supported for now, you can add it yourself")

        isSubtypeOf(typeOf<List<*>>()) -> error("lists are not supported for now, you can add it yourself")

        isSubtypeOf(typeOf<Set<*>>()) -> error("sets are not supported for now, you can add it yourself")

        classifier == Map::class -> {
            val (key, value) = arguments
            DataTypes.createMapType(
                key.type?.convertToSpark(),
                value.type?.convertToSpark(),
                value.type?.isMarkedNullable ?: true,
            )
        }

        else -> null
    }

private val nullableNothingType: KType = typeOf<List<Nothing?>>().arguments.first().type!!

// endregion
