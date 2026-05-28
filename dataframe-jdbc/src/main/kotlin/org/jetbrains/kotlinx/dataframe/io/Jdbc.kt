package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

// TODO: https://github.com/Kotlin/dataframe/issues/450
public class Jdbc :
    SupportedCodeGenerationFormat,
    SupportedDataFrameFormat {
    public override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readJDBC(stream)

    public override fun readDataFrame(path: Path, header: List<String>): AnyFrame = DataFrame.readJDBC(path)

    override fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        generateHelperCompanionObject: Boolean,
    ): Code = throw IllegalStateException("Jdbc.readCodeForGeneration() is not yet implemented: Issue #450")

    override fun readCodeForGeneration(file: File, name: String, generateHelperCompanionObject: Boolean): Code =
        throw IllegalStateException("Jdbc.readCodeForGeneration() is not yet implemented: Issue #450")

    override fun acceptsExtension(ext: String): Boolean = ext == "jdbc"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 40000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadJdbcMethod(pathRepresentation)
}

/**
 * [DataFrameReadSource] for JDBC.
 *
 * Reading from JDBC always needs a "what" (a SQL query or table name) — unlike a file, a [Connection] doesn't
 * carry that instruction. Provide it via [ReadOptions.sqlQueryOrTableName]. The only exception is [ResultSet],
 * which is already an executed query.
 *
 * Supported source types: [Connection], [DataSource], [DbConnectionConfig], [ResultSet].
 *
 * `readAllSqlTables` returns a `Map<String, AnyFrame>` and doesn't fit the single-DataFrame contract; it
 * remains as a direct API call.
 */
public class Jdbc2 : DataFrameReadSource {

    public data class ReadOptions(
        /**
         * SQL query (e.g. `"SELECT * FROM users"`) or table name (e.g. `"users"`).
         * Required for [Connection], [DataSource], and [DbConnectionConfig] sources.
         * Ignored for [ResultSet] (it's already an executed query).
         */
        val sqlQueryOrTableName: String? = null,
        val limit: Int? = null,
        val inferNullability: Boolean = true,
        /** Optional, auto-detected from the source when `null`. */
        val dbType: DbType? = null,
        val strictValidation: Boolean = true,
        val configureStatement: (PreparedStatement) -> Unit = {},
        /**
         * Only used when the source is a [ResultSet] and [dbType] is `null`; provides a [Connection]
         * to auto-detect the database type. Ignored otherwise.
         */
        val resultSetConnection: Connection? = null,
    ) : DataFrameReadOptions

    override val supportedReadingTypes: Set<KType> =
        setOf(
            typeOf<Connection>(),
            typeOf<DataSource>(),
            typeOf<DbConnectionConfig>(),
            typeOf<ResultSet>(),
        )

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            @Suppress("RedundantReturnKeyword")
            return@runCatching when (source) {
                is ResultSet -> when {
                    opts.dbType != null ->
                        DataFrame.readResultSet(source, opts.dbType, opts.limit, opts.inferNullability)

                    opts.resultSetConnection != null ->
                        DataFrame.readResultSet(
                            source,
                            opts.resultSetConnection,
                            opts.limit,
                            opts.inferNullability,
                        )

                    // Without dbType or a connection we can't read a ResultSet — fall through.
                    else -> return Result.failure(
                        IllegalArgumentException(
                            "ResultSet read requires either ReadOptions.dbType or ReadOptions.resultSetConnection",
                        ),
                    )
                }

                is Connection -> {
                    val query = opts.sqlQueryOrTableName
                        ?: return Result.failure(
                            IllegalArgumentException("Connection read requires ReadOptions.sqlQueryOrTableName"),
                        )
                    source.readDataFrame(
                        sqlQueryOrTableName = query,
                        limit = opts.limit,
                        inferNullability = opts.inferNullability,
                        dbType = opts.dbType,
                        strictValidation = opts.strictValidation,
                        configureStatement = opts.configureStatement,
                    )
                }

                is DataSource -> {
                    val query = opts.sqlQueryOrTableName
                        ?: return Result.failure(
                            IllegalArgumentException("DataSource read requires ReadOptions.sqlQueryOrTableName"),
                        )
                    source.readDataFrame(
                        sqlQueryOrTableName = query,
                        limit = opts.limit,
                        inferNullability = opts.inferNullability,
                        dbType = opts.dbType,
                        strictValidation = opts.strictValidation,
                        configureStatement = opts.configureStatement,
                    )
                }

                is DbConnectionConfig -> {
                    val query = opts.sqlQueryOrTableName
                        ?: return Result.failure(
                            IllegalArgumentException("DbConnectionConfig read requires ReadOptions.sqlQueryOrTableName"),
                        )
                    source.readDataFrame(
                        sqlQueryOrTableName = query,
                        limit = opts.limit,
                        inferNullability = opts.inferNullability,
                        dbType = opts.dbType,
                        strictValidation = opts.strictValidation,
                        configureStatement = opts.configureStatement,
                    )
                }

                else -> return Result.failure(IllegalStateException("Unsupported JDBC source type: ${source::class}"))
            }
        }

    override fun readDataFrameSchema(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrameSchema> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            when (source) {
                // ResultSet has a true zero-row metadata-only path.
                is ResultSet -> when {
                    opts.dbType != null ->
                        DataFrameSchema.readResultSet(source, opts.dbType)

                    opts.resultSetConnection != null ->
                        DataFrameSchema.readResultSet(source, extractDBTypeFromConnection(opts.resultSetConnection))

                    else -> error("ResultSet schema read requires either ReadOptions.dbType or ReadOptions.resultSetConnection")
                }

                is Connection -> {
                    val query = opts.sqlQueryOrTableName
                        ?: error("Connection schema read requires ReadOptions.sqlQueryOrTableName")
                    source.readDataFrameSchema(sqlQueryOrTableName = query, dbType = opts.dbType)
                }

                is DataSource -> {
                    val query = opts.sqlQueryOrTableName
                        ?: error("DataSource schema read requires ReadOptions.sqlQueryOrTableName")
                    source.readDataFrameSchema(sqlQueryOrTableName = query, dbType = opts.dbType)
                }

                is DbConnectionConfig -> {
                    val query = opts.sqlQueryOrTableName
                        ?: error("DbConnectionConfig schema read requires ReadOptions.sqlQueryOrTableName")
                    source.readDataFrameSchema(sqlQueryOrTableName = query, dbType = opts.dbType)
                }

                else -> error("Unsupported source type: ${source::class}")
            }
        }

    override val testOrder: Int = 50_000

    override fun toString(): String = "Jdbc"
}

private fun DataFrame.Companion.readJDBC(stream: File): DataFrame<*> {
    TODO("Not yet implemented")
}

private fun DataFrame.Companion.readJDBC(path: Path): DataFrame<*> {
    TODO("Not yet implemented")
}

private fun DataFrame.Companion.readJDBC(stream: InputStream): DataFrame<*> {
    TODO("Not yet implemented")
}

internal class DefaultReadJdbcMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, READ_JDBC)

private const val READ_JDBC = "readJDBC"
