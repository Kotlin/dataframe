package org.jetbrains.kotlinx.dataframe.io

/**
 * Represents the configuration for an internally managed JDBC database connection.
 *
 * This class defines connection parameters used by the library to create a `Connection`
 * when the user does not provide one explicitly.
 * It is designed for safe, read-only access by default.
 *
 * @property url The JDBC URL of the database, e.g., `"jdbc:postgresql://localhost:5432/mydb"`.
 *               Must follow the standard format: `jdbc:subprotocol:subname`.
 *
 * @property user The username used for authentication.
 *                Optional, default is an empty string.
 *
 * @property password The password used for authentication.
 *                    Optional, default is an empty string.
 *
 * @property readOnly If `true` (default), the library will create the connection in read-only mode.
 *                    This enables the following behavior:
 *                    - `Connection.setReadOnly(true)`
 *                    - `Connection.setAutoCommit(false)`
 *                    - automatic `rollback()` at the end of execution
 *
 *                    If `false`, the connection will be created with JDBC defaults (usually read-write),
 *                    but the library will still reject any queries that appear to modify data
 *                    (e.g. contain `INSERT`, `UPDATE`, `DELETE`, etc.).
 *
 * Note: Connections created using this configuration are managed entirely by the library.
 * Users do not have access to the underlying `Connection` instance and cannot commit or close it manually.
 *
 * ### Examples:
 *
 * ```kotlin
 * // Safe read-only connection (default)
 * val config = DbConnectionConfig("jdbc:sqlite::memory:")
 * val df = DataFrame.readSqlQuery(config, "SELECT * FROM books")
 *
 * // Use default JDBC connection settings (still protected against mutations)
 * val config = DbConnectionConfig(
 *     url = "jdbc:sqlite::memory:",
 *     readOnly = false
 * )
 * ```
 */
public class DbConnectionConfig(
    public val url: String,
    public val user: String = "",
    public val password: String = "",
    public val readOnly: Boolean = true,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DbConnectionConfig) return false

        if (url != other.url) return false
        if (user != other.user) return false
        if (password != other.password) return false
        if (readOnly != other.readOnly) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + readOnly.hashCode()
        return result
    }

    override fun toString(): String =
        "DbConnectionConfig(url='$url', user='$user', password='***', readOnly=$readOnly)"

    /**
     * Creates a copy of this configuration with the option to override specific properties.
     *
     * @param url The JDBC URL. If not specified, uses the current value.
     * @param user The username. If not specified, uses the current value.
     * @param password The password. If not specified, uses the current value.
     * @param readOnly The read-only flag. If not specified, uses the current value.
     * @return A new [DbConnectionConfig] instance with the specified changes.
     */
    public fun copy(
        url: String = this.url,
        user: String = this.user,
        password: String = this.password,
        readOnly: Boolean = this.readOnly,
    ): DbConnectionConfig = DbConnectionConfig(url, user, password, readOnly)
}
