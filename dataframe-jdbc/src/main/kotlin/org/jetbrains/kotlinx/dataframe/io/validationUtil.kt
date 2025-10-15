package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromUrl
import java.sql.Connection
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

/**
 * Executes the given block with a managed JDBC connection created from [DbConnectionConfig].
 *
 * If [DbConnectionConfig.readOnly] is `true` (default), the connection will be:
 * - explicitly marked as read-only
 * - used with auto-commit disabled
 * - rolled back after execution to prevent unintended modifications
 *
 * This utility guarantees proper closing of the connection and safe rollback in read-only mode.
 * It should be used when the user does not manually manage JDBC connections.
 *
 * @param [dbConfig] The configuration used to create the connection.
 * @param [dbType] Optional database type (not used here but can be passed through for logging or future extensions).
 * @param [block] A lambda with receiver that runs with an open and managed [java.sql.Connection].
 * @return The result of the [block] execution.
 */
internal inline fun <T> withReadOnlyConnection(
    dbConfig: DbConnectionConfig,
    dbType: DbType? = null,
    block: (Connection) -> T,
): T {
    val actualDbType = dbType ?: extractDBTypeFromUrl(dbConfig.url)
    val connection = actualDbType.createConnection(dbConfig)

    return connection.use { conn ->
        try {
            if (dbConfig.readOnly) {
                conn.autoCommit = false
            }

            block(conn)
        } finally {
            if (dbConfig.readOnly) {
                try {
                    conn.rollback()
                } catch (e: SQLException) {
                    logger.warn(e) {
                        "Failed to rollback read-only transaction (url=${dbConfig.url})"
                    }
                }
            }
        }
    }
}

/**
 * A regular expression defining the valid pattern for SQL table names.
 *
 * This pattern enforces that table names must:
 * - Contain only Unicode letters, Unicode digits, or underscores.
 * - Optionally be segmented by dots to indicate schema and table separation.
 *
 * It ensures compatibility with most SQL database naming conventions, thus minimizing risks of invalid names
 * or injection vulnerabilities.
 *
 * Example of valid table names:
 * - `my_table`
 * - `schema1.table2`
 *
 * Example of invalid table names:
 * - `my-table` (contains a dash)
 * - `table!name` (contains special characters)
 * - `.startWithDot` (cannot start with a dot)
 */
internal const val TABLE_NAME_VALID_PATTERN = "^[\\p{L}\\p{N}_]+(\\.[\\p{L}\\p{N}_]+)*$"

internal fun isSqlQuery(sqlQueryOrTableName: String): Boolean {
    val queryPattern = Regex("(?i)\\b(SELECT)\\b")
    return queryPattern.containsMatchIn(sqlQueryOrTableName.trim())
}

/**
 * SQL table name pattern matching: __catalog.schema.table__
 * Allows alphanumeric characters and underscores, must start with letter or underscore
 */
private val SQL_TABLE_NAME_PATTERN = Regex("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*){0,2}$")

internal fun isSqlTableName(sqlQueryOrTableName: String): Boolean {
    // Match table names with optional schema and catalog (e.g., catalog.schema.table)
    return SQL_TABLE_NAME_PATTERN.matches(sqlQueryOrTableName.trim())
}

internal val FORBIDDEN_PATTERNS_REGEX = listOf(
    ";", // Separator for SQL statements
    "--", // Single-line comments
    "/\\*", // Start of multi-line comments
    "\\*/", // End of multi-line comments
    "\\bDROP\\b", // DROP as a full word
    "\\bDELETE\\b", // DELETE as a full word
    "\\bINSERT\\b", // INSERT as a full word
    "\\bUPDATE\\b", // UPDATE as a full word
    "\\bEXEC\\b", // EXEC as a full word
    "\\bEXECUTE\\b", // EXECUTE as a full word
    "\\bCREATE\\b", // CREATE as a full word
    "\\bALTER\\b", // ALTER as a full word
    "\\bGRANT\\b", // GRANT as a full word
    "\\bREVOKE\\b", // REVOKE as a full word
    "\\bMERGE\\b", // MERGE as a full word
).map { Regex(it, RegexOption.IGNORE_CASE) }

/**
 * Checks if a given string contains forbidden patterns or keywords.
 * Logs a clear and friendly message if any forbidden pattern is found.
 *
 * ### Forbidden SQL Examples:
 * 1. **Single-line comment** (using `--`):
 *    - `SELECT * FROM Sale WHERE amount = 100.0 -- AND id = 5`
 *
 * 2. **Multi-line comment** (using `/* */`):
 *    - `SELECT * FROM Customer /* Possible malicious comment */ WHERE id = 1`
 *
 * 3. **Multiple statements separated by semicolon (`;`)**:
 *    - `SELECT * FROM Sale WHERE amount = 500.0; DROP TABLE Customer`
 *
 * 4. **Potentially malicious SQL with single quotes for injection**:
 *    - `SELECT * FROM Sale WHERE id = 1 AND amount = 100.0 OR '1'='1`
 *
 * 5. **Usage of dangerous commands like `DROP`, `DELETE`, `ALTER`, etc.**:
 *    - `DROP TABLE Customer; SELECT * FROM Sale`
 *
 * ### Allowed SQL Examples:
 * 1. Query with names containing reserved words as parts of identifiers:
 *    - `SELECT last_update FROM HELLO_ALTER`
 *
 * 2. Query with fully valid syntax:
 *    - `SELECT id, name FROM Customers WHERE age > 25`
 *
 * 3. Query with identifiers resembling commands but not in forbidden contexts:
 *    - `SELECT id, amount FROM TRANSACTION_DROP`
 *
 * 4. Query with case-insensitive identifiers:
 *    - `select Id, Name from Hello_Table`
 *
 * ### Key Notes:
 * - Reserved keywords like `DROP`, `DELETE`, `ALTER`, etc., are forbidden **only when they appear as standalone commands**.
 * - Reserved words as parts of table or column names (e.g., `HELLO_ALTER`, `myDropTable`) **are allowed**.
 * - Inline or multi-line comments (`--` or `/* */`) are restricted to prevent potential SQL injection attacks.
 * - Multiple SQL statements separated by semicolons (`;`) are not allowed to prevent the execution of unintended commands.
 */
internal fun hasForbiddenPatterns(input: String): Boolean {
    for (regex in FORBIDDEN_PATTERNS_REGEX) {
        if (regex.containsMatchIn(input)) {
            logger.error {
                "Validation failed: The input contains a forbidden element matching '${regex.pattern}'. Please review the input: '$input'."
            }
            return true
        }
    }
    return false
}

/**
 * Allowed list of SQL operators
 */
internal val ALLOWED_SQL_OPERATORS = listOf("SELECT", "WITH", "VALUES", "TABLE")

/**
 * Validates if the SQL query is safe and starts with SELECT.
 * Ensures a proper syntax structure, checks for balanced quotes, and disallows dangerous commands or patterns.
 */
internal fun isValidSqlQuery(sqlQuery: String): Boolean {
    val normalizedSqlQuery = sqlQuery.trim().uppercase()

    // Log the query being validated
    logger.debug { "Validating SQL query: '$sqlQuery'" }

    // Ensure the query starts from one of the allowed SQL operators
    if (ALLOWED_SQL_OPERATORS.none { normalizedSqlQuery.startsWith(it) }) {
        logger.error {
            "Validation failed: The SQL query must start with one of: $ALLOWED_SQL_OPERATORS. Given query: '$sqlQuery'."
        }
        return false
    }

    // Validate against forbidden patterns
    if (hasForbiddenPatterns(normalizedSqlQuery)) {
        return false
    }

    // Check if there are balanced quotes (single and double)
    val singleQuotes = sqlQuery.count { it == '\'' }
    val doubleQuotes = sqlQuery.count { it == '"' }
    if (singleQuotes % 2 != 0) {
        logger.error {
            "Validation failed: Unbalanced single quotes in the SQL query. " +
                "Please correct the query: '$sqlQuery'."
        }
        return false
    }
    if (doubleQuotes % 2 != 0) {
        logger.error {
            "Validation failed: Unbalanced double quotes in the SQL query. " +
                "Please correct the query: '$sqlQuery'."
        }
        return false
    }

    logger.debug { "SQL query validation succeeded for query: '$sqlQuery'." }
    return true
}

/**
 * Validates if the given SQL table name is safe and logs any validation violations.
 */
internal fun isValidTableName(tableName: String): Boolean {
    val normalizedTableName = tableName.trim().uppercase()

    // Log the table name being validated
    logger.debug { "Validating SQL table name: '$tableName'" }

    // Validate against forbidden patterns
    if (hasForbiddenPatterns(normalizedTableName)) {
        return false
    }

    // Validate the table name structure: letters, numbers, underscores, and dots are allowed
    val tableNameRegex = Regex(TABLE_NAME_VALID_PATTERN)
    if (!tableNameRegex.matches(normalizedTableName)) {
        logger.error {
            "Validation failed: The table name contains invalid characters. " +
                "Only letters, numbers, underscores, and dots are allowed. Provided name: '$tableName'."
        }
        return false
    }

    logger.debug { "Table name validation passed for table: '$tableName'." }
    return true
}
