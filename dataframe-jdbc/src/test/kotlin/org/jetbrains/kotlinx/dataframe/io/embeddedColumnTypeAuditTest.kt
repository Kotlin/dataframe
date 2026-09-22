package org.jetbrains.kotlinx.dataframe.io

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.junit.Test
import java.sql.DriverManager

/**
 * The same audit as [assertColumnTypesMatchValues] runs for the containerized databases, for the three
 * embedded ones. They need no Docker, so this runs in the ordinary `test` task.
 */
class EmbeddedColumnTypeAuditTest {

    @Test
    fun `H2 declared column types accept the values the driver returns`() {
        DriverManager.getConnection("jdbc:h2:mem:columnTypeAudit;DB_CLOSE_DELAY=-1").use { connection ->
            connection.assertColumnTypesMatchValues(H2(H2.Mode.Regular), H2_AUDIT_DDL, H2_AUDIT_INSERT)
        }
    }

    @Test
    fun `SQLite declared column types accept the values the driver returns`() {
        DriverManager.getConnection("jdbc:sqlite::memory:").use { connection ->
            connection.assertColumnTypesMatchValues(Sqlite.default, SQLITE_AUDIT_DDL, SQLITE_AUDIT_INSERT)
        }
    }

    @Test
    fun `DuckDB declared column types accept the values the driver returns`() {
        DriverManager.getConnection("jdbc:duckdb:").use { connection ->
            connection.assertColumnTypesMatchValues(DuckDb, DUCKDB_AUDIT_DDL, DUCKDB_AUDIT_INSERT)
        }
    }
}

@Language("SQL")
private val H2_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      tinyintCol TINYINT, smallintCol SMALLINT, intCol INT, bigintCol BIGINT,
      numericCol NUMERIC(10,2), decfloatCol DECFLOAT, realCol REAL, doubleCol DOUBLE PRECISION,
      booleanCol BOOLEAN,
      charCol CHAR(10), varcharCol VARCHAR(50), varcharIgnorecaseCol VARCHAR_IGNORECASE(50),
      clobCol CLOB,
      binaryCol BINARY(10), varbinaryCol VARBINARY(10), blobCol BLOB,
      uuidCol UUID, enumCol ENUM('x','y'),
      dateCol DATE, timeCol TIME, timeTzCol TIME WITH TIME ZONE,
      timestampCol TIMESTAMP, timestampTzCol TIMESTAMP WITH TIME ZONE,
      intervalCol INTERVAL DAY TO SECOND,
      jsonCol JSON, geometryCol GEOMETRY,
      intArrayCol INT ARRAY
    )
    """.trimIndent()

@Language("SQL")
private val H2_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1.5,1.5,1.5,1.5,TRUE,
      'a','a','a','a',X'01',X'01',X'01',
      RANDOM_UUID(),'x',
      DATE '2024-01-01',TIME '10:00:00',TIME WITH TIME ZONE '10:00:00+02',
      TIMESTAMP '2024-01-01 10:00:00',TIMESTAMP WITH TIME ZONE '2024-01-01 10:00:00+02',
      INTERVAL '1' DAY,
      JSON '{"k":1}','POINT(1 1)',
      ARRAY[1,2])
    """.trimIndent()

@Language("SQL")
private val SQLITE_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      integerCol INTEGER, intCol INT, bigintCol BIGINT, tinyintCol TINYINT, smallintCol SMALLINT,
      realCol REAL, doubleCol DOUBLE, floatCol FLOAT,
      numericCol NUMERIC, decimalCol DECIMAL(10,2), booleanCol BOOLEAN,
      textCol TEXT, varcharCol VARCHAR(50), charCol CHARACTER(10), clobCol CLOB,
      blobCol BLOB, dateCol DATE, datetimeCol DATETIME
    )
    """.trimIndent()

@Language("SQL")
private val SQLITE_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1,1.5,1.5,1.5,1.5,1.5,1,
      'a','a','a','a',X'01','2024-01-01','2024-01-01 10:00:00')
    """.trimIndent()

@Language("SQL")
private val DUCKDB_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      tinyintCol TINYINT, smallintCol SMALLINT, integerCol INTEGER, bigintCol BIGINT,
      hugeintCol HUGEINT, utinyintCol UTINYINT, usmallintCol USMALLINT, uintegerCol UINTEGER,
      ubigintCol UBIGINT,
      floatCol FLOAT, doubleCol DOUBLE, decimalCol DECIMAL(10,2), booleanCol BOOLEAN,
      varcharCol VARCHAR, blobCol BLOB, uuidCol UUID,
      dateCol DATE, timeCol TIME, timestampCol TIMESTAMP, timestampTzCol TIMESTAMPTZ,
      intervalCol INTERVAL,
      listCol INTEGER[], structCol STRUCT(a INTEGER, b VARCHAR), mapCol MAP(VARCHAR, INTEGER)
    )
    """.trimIndent()

@Language("SQL")
private val DUCKDB_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1,1,1,1,1,1.5,1.5,1.5,TRUE,
      'a','\x01'::BLOB,'80000000-0000-0000-0000-000000000000'::UUID,
      DATE '2024-01-01',TIME '10:00:00',TIMESTAMP '2024-01-01 10:00:00',
      TIMESTAMPTZ '2024-01-01 10:00:00+02',INTERVAL 1 DAY,
      [1,2],{'a': 1, 'b': 'x'},MAP{'k': 1})
    """.trimIndent()
