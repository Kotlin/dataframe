package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDateTime
import org.intellij.lang.annotations.Language
import org.postgresql.geometric.PGbox
import org.postgresql.geometric.PGcircle
import org.postgresql.geometric.PGline
import org.postgresql.geometric.PGlseg
import org.postgresql.geometric.PGpath
import org.postgresql.geometric.PGpoint
import org.postgresql.geometric.PGpolygon
import org.postgresql.util.PGInterval
import org.postgresql.util.PGmoney
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.SQLXML
import java.sql.Time
import java.time.OffsetDateTime
import java.util.Date
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Instant

// Probe tables for `assertColumnTypesMatchValues` and `assertColumnTypes`, one per containerized
// database, each holding a column per SQL type. The embedded databases' tables live next to their test
// in `embeddedColumnTypeAuditTest.kt`.
//
// They deliberately cover more types than the other tests use — including the ones the type-mapping
// pages under `docs/StardustDocs/topics/io/` document — because the point of the audit is to catch a
// type the reading pipeline gets wrong *before* a user runs into it. Every column must be non-null in
// the inserted row: a `null` tells us nothing about the class of the value.
//
// `BIT(1)` vs `BIT(3)` and the two `SQL_VARIANT` columns are not duplicates: in each pair the driver
// reports the same metadata but returns values of different classes, which is exactly the trap of #2087.

@Language("SQL")
internal val MYSQL_FAMILY_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      bit1Col BIT(1), bit3Col BIT(3),
      tinyintCol TINYINT, tinyintUnsignedCol TINYINT UNSIGNED,
      smallintCol SMALLINT, smallintUnsignedCol SMALLINT UNSIGNED,
      mediumintCol MEDIUMINT, mediumintUnsignedCol MEDIUMINT UNSIGNED,
      intCol INT, intUnsignedCol INT UNSIGNED, bigintCol BIGINT, bigintUnsignedCol BIGINT UNSIGNED,
      floatCol FLOAT, doubleCol DOUBLE, decimalCol DECIMAL(10,2),
      dateCol DATE, datetimeCol DATETIME, timestampCol TIMESTAMP, timeCol TIME, yearCol YEAR,
      charCol CHAR(10), varcharCol VARCHAR(50),
      tinytextCol TINYTEXT, textCol TEXT, mediumtextCol MEDIUMTEXT, longtextCol LONGTEXT,
      binaryCol BINARY(10), varbinaryCol VARBINARY(10),
      tinyblobCol TINYBLOB, blobCol BLOB, mediumblobCol MEDIUMBLOB, longblobCol LONGBLOB,
      enumCol ENUM('x','y'), setCol SET('a','b'), jsonCol JSON
    )
    """.trimIndent()

@Language("SQL")
internal val MYSQL_FAMILY_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,b'101',1,1,1,1,1,1,1,1,1,1,1.5,1.5,1.5,
      '2024-01-01','2024-01-01 10:00:00','2024-01-01 10:00:00','10:00:00',2024,
      'a','a','a','a','a','a','a','a','a','a','a','a','x','a','{"k":1}')
    """.trimIndent()

@Language("SQL")
internal val MSSQL_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      bigintCol BIGINT, intCol INT, smallintCol SMALLINT, tinyintCol TINYINT, bitCol BIT,
      decimalCol DECIMAL(10,2), numericCol NUMERIC(10,2), moneyCol MONEY, smallmoneyCol SMALLMONEY,
      floatCol FLOAT, float24Col FLOAT(24), realCol REAL,
      dateCol DATE, datetimeCol DATETIME, datetime2Col DATETIME2(3), dtoCol DATETIMEOFFSET(2),
      smalldatetimeCol SMALLDATETIME, timeCol TIME(3),
      charCol CHAR(10), varcharCol VARCHAR(50), varcharMaxCol VARCHAR(MAX), textCol TEXT,
      ncharCol NCHAR(10), nvarcharCol NVARCHAR(50), nvarcharMaxCol NVARCHAR(MAX), ntextCol NTEXT,
      binaryCol BINARY(10), varbinaryCol VARBINARY(10), varbinaryMaxCol VARBINARY(MAX),
      imageCol IMAGE, uniqueidentifierCol UNIQUEIDENTIFIER, xmlCol XML,
      sqlvariantIntCol SQL_VARIANT, sqlvariantStrCol SQL_VARIANT,
      geometryCol GEOMETRY, geographyCol GEOGRAPHY, hierarchyidCol HIERARCHYID,
      rowversionCol ROWVERSION
    )
    """.trimIndent()

/**
 * Lists its columns explicitly because `rowversionCol` cannot be inserted into: SQL Server maintains a
 * `ROWVERSION` column itself, and it is never null, so the audit still covers it.
 */
@Language("SQL")
internal val MSSQL_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit(bigintCol,intCol,smallintCol,tinyintCol,bitCol,decimalCol,numericCol,
      moneyCol,smallmoneyCol,floatCol,float24Col,realCol,dateCol,datetimeCol,datetime2Col,dtoCol,
      smalldatetimeCol,timeCol,charCol,varcharCol,varcharMaxCol,textCol,ncharCol,nvarcharCol,
      nvarcharMaxCol,ntextCol,binaryCol,varbinaryCol,varbinaryMaxCol,imageCol,
      uniqueidentifierCol,xmlCol,sqlvariantIntCol,sqlvariantStrCol,geometryCol,geographyCol,
      hierarchyidCol)
    VALUES(1,1,1,1,1,1.5,1.5,1.5,1.5,1.5,1.5,1.5,'2024-01-01','2024-01-01T10:00:00',
      '2024-01-01T10:00:00','2024-01-02T03:04:05.06+02:00','2024-01-01T10:00:00','10:00:00',
      'a','a','a','a','a','a','a','a',0x01,0x01,0x01,0x01,NEWID(),'<a/>',
      CAST(1 AS SQL_VARIANT),CAST('a' AS SQL_VARIANT),
      geometry::Point(1,1,0),geography::Point(1,1,4326),CAST(0x58 AS HIERARCHYID))
    """.trimIndent()

@Language("SQL")
internal val POSTGRES_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      smallintCol SMALLINT, integerCol INTEGER, bigintCol BIGINT, serialCol SERIAL,
      decimalCol DECIMAL(10,2), numericCol NUMERIC(10,2), realCol REAL,
      doubleCol DOUBLE PRECISION, moneyCol MONEY,
      charCol CHAR(10), varcharCol VARCHAR(50), textCol TEXT,
      byteaCol BYTEA, boolCol BOOLEAN,
      dateCol DATE, timeCol TIME, timetzCol TIMETZ, timestampCol TIMESTAMP,
      timestamptzCol TIMESTAMPTZ, intervalCol INTERVAL,
      uuidCol UUID, jsonCol JSON, jsonbCol JSONB, xmlCol XML,
      pointCol POINT, lineCol LINE, lsegCol LSEG, boxCol BOX, pathCol PATH,
      polygonCol POLYGON, circleCol CIRCLE,
      cidrCol CIDR, inetCol INET, macaddrCol MACADDR, macaddr8Col MACADDR8,
      bit1Col BIT(1), bit3Col BIT(3), varbitCol VARBIT(3),
      tsvectorCol TSVECTOR, tsqueryCol TSQUERY,
      int4rangeCol INT4RANGE, int8rangeCol INT8RANGE, numrangeCol NUMRANGE,
      tsrangeCol TSRANGE, tstzrangeCol TSTZRANGE, daterangeCol DATERANGE,
      int4multirangeCol INT4MULTIRANGE,
      intArrayCol INTEGER[], textArrayCol TEXT[]
    )
    """.trimIndent()

/**
 * Lists its columns explicitly because `serialCol` is not inserted into: a `SERIAL` column takes its
 * value from a sequence, and it is never null, so the audit still covers it.
 */
@Language("SQL")
internal val POSTGRES_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit(smallintCol,integerCol,bigintCol,decimalCol,numericCol,realCol,
      doubleCol,moneyCol,charCol,varcharCol,textCol,byteaCol,boolCol,dateCol,timeCol,timetzCol,
      timestampCol,timestamptzCol,intervalCol,uuidCol,jsonCol,jsonbCol,xmlCol,pointCol,lineCol,
      lsegCol,boxCol,pathCol,polygonCol,circleCol,cidrCol,inetCol,macaddrCol,macaddr8Col,
      bit1Col,bit3Col,varbitCol,tsvectorCol,tsqueryCol,
      int4rangeCol,int8rangeCol,numrangeCol,tsrangeCol,tstzrangeCol,daterangeCol,int4multirangeCol,
      intArrayCol,textArrayCol)
    VALUES(1,1,1,1.5,1.5,1.5,1.5,1.5,'a','a','a','\x01',true,'2024-01-01','10:00:00',
      '10:00:00+02','2024-01-01T10:00:00','2024-01-01T10:00:00+02','1 day',
      gen_random_uuid(),'{"k":1}','{"k":1}','<a/>','(1,1)','{1,1,1}','[(1,1),(2,2)]',
      '((1,1),(2,2))','((1,1),(2,2))','((1,1),(2,2),(3,3))','<(1,1),1>','10.0.0.0/8',
      '10.0.0.1','08:00:2b:01:02:03','08:00:2b:01:02:03:04:05',
      B'1',B'101',B'101',to_tsvector('a'),to_tsquery('a'),
      '[1,2)','[1,2)','[1.0,2.0)','[2024-01-01,2024-01-02)','[2024-01-01,2024-01-02)',
      '[2024-01-01,2024-01-02)','{[1,2)}',
      ARRAY[1,2],ARRAY['a','b'])
    """.trimIndent()

/**
 * The column type every probe column of [MYSQL_FAMILY_AUDIT_DDL] is read as under MySQL, taken from a
 * real read against the containerized server — the claim `readSqlTypeMapping_MySQL.md` publishes.
 * See [assertColumnTypes].
 *
 * `bit1Col` vs `bit3Col` is the #2087 case: the driver reports identical metadata for both, and the
 * declared width is what tells them apart. `datetimeCol` is where MySQL and MariaDB disagree — see
 * [MARIADB_EXPECTED_TYPES].
 */
internal val MYSQL_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "bit1Col" to typeOf<Boolean>(),
    "bit3Col" to typeOf<ByteArray>(),
    "tinyintCol" to typeOf<Int>(),
    "tinyintUnsignedCol" to typeOf<Int>(),
    "smallintCol" to typeOf<Int>(),
    "smallintUnsignedCol" to typeOf<Int>(),
    "mediumintCol" to typeOf<Int>(),
    "mediumintUnsignedCol" to typeOf<Int>(),
    "intCol" to typeOf<Int>(),
    "intUnsignedCol" to typeOf<Long>(),
    "bigintCol" to typeOf<Long>(),
    "bigintUnsignedCol" to typeOf<BigInteger>(),
    "floatCol" to typeOf<Float>(),
    "doubleCol" to typeOf<Double>(),
    "decimalCol" to typeOf<BigDecimal>(),
    "dateCol" to typeOf<Date>(),
    "datetimeCol" to typeOf<LocalDateTime>(),
    "timestampCol" to typeOf<Instant>(),
    "timeCol" to typeOf<Time>(),
    "yearCol" to typeOf<Date>(),
    "charCol" to typeOf<String>(),
    "varcharCol" to typeOf<String>(),
    "tinytextCol" to typeOf<String>(),
    "textCol" to typeOf<String>(),
    "mediumtextCol" to typeOf<String>(),
    "longtextCol" to typeOf<String>(),
    "binaryCol" to typeOf<ByteArray>(),
    "varbinaryCol" to typeOf<ByteArray>(),
    "tinyblobCol" to typeOf<ByteArray>(),
    "blobCol" to typeOf<ByteArray>(),
    "mediumblobCol" to typeOf<ByteArray>(),
    "longblobCol" to typeOf<ByteArray>(),
    "enumCol" to typeOf<String>(),
    "setCol" to typeOf<String>(),
    "jsonCol" to typeOf<String>(),
)

/**
 * The same probe table read under MariaDB — the claim `readSqlTypeMapping_MariaDB.md` publishes.
 * See [assertColumnTypes].
 *
 * It differs from [MYSQL_EXPECTED_TYPES] in exactly two columns, and both differences are why there
 * are two maps rather than one: MariaDB reports `SMALLINT` as `java.lang.Short` and so reads it as
 * [Short], and it returns a `java.sql.Timestamp` for `DATETIME` where MySQL returns a
 * `java.time.LocalDateTime`, so the column becomes [Instant] instead of [LocalDateTime].
 */
internal val MARIADB_EXPECTED_TYPES: Map<String, KType> = MYSQL_EXPECTED_TYPES + mapOf(
    "smallintCol" to typeOf<Short>(),
    "datetimeCol" to typeOf<Instant>(),
)

/**
 * The column type every probe column of [MSSQL_AUDIT_DDL] is read as, taken from a real read against
 * the containerized server — the claim `readSqlTypeMapping_MsSql.md` publishes.
 * See [assertColumnTypes].
 *
 * The two `sqlvariant` columns hold values of different classes and are both typed [Any]; that is the
 * one column kind here where [Any] is the honest answer rather than a gap.
 */
internal val MSSQL_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "bigintCol" to typeOf<Long>(),
    "intCol" to typeOf<Int>(),
    "smallintCol" to typeOf<Short>(),
    "tinyintCol" to typeOf<Short>(),
    "bitCol" to typeOf<Boolean>(),
    "decimalCol" to typeOf<BigDecimal>(),
    "numericCol" to typeOf<BigDecimal>(),
    "moneyCol" to typeOf<BigDecimal>(),
    "smallmoneyCol" to typeOf<BigDecimal>(),
    "floatCol" to typeOf<Double>(),
    "float24Col" to typeOf<Float>(),
    "realCol" to typeOf<Float>(),
    "dateCol" to typeOf<Date>(),
    "datetimeCol" to typeOf<Instant>(),
    "datetime2Col" to typeOf<Instant>(),
    "dtoCol" to typeOf<OffsetDateTime>(),
    "smalldatetimeCol" to typeOf<Instant>(),
    "timeCol" to typeOf<Time>(),
    "charCol" to typeOf<String>(),
    "varcharCol" to typeOf<String>(),
    "varcharMaxCol" to typeOf<String>(),
    "textCol" to typeOf<String>(),
    "ncharCol" to typeOf<String>(),
    "nvarcharCol" to typeOf<String>(),
    "nvarcharMaxCol" to typeOf<String>(),
    "ntextCol" to typeOf<String>(),
    "binaryCol" to typeOf<ByteArray>(),
    "varbinaryCol" to typeOf<ByteArray>(),
    "varbinaryMaxCol" to typeOf<ByteArray>(),
    "imageCol" to typeOf<ByteArray>(),
    "uniqueidentifierCol" to typeOf<String>(),
    "xmlCol" to typeOf<String>(),
    "sqlvariantIntCol" to typeOf<Any>(),
    "sqlvariantStrCol" to typeOf<Any>(),
    "geometryCol" to typeOf<ByteArray>(),
    "geographyCol" to typeOf<ByteArray>(),
    "hierarchyidCol" to typeOf<ByteArray>(),
    "rowversionCol" to typeOf<ByteArray>(),
)

/**
 * The column type every probe column of [POSTGRES_AUDIT_DDL] is read as, taken from a real read
 * against the containerized server — the claim `readSqlTypeMapping_PostgreSQL.md` publishes.
 * See [assertColumnTypes]; PostgreSQL reports unquoted identifiers lower-cased.
 *
 * The geometric types, `money` and `interval` each get a driver `PGobject` subclass, because
 * `PostgreSql` requests those explicitly. Every other `PGobject`-backed type — `json`, `jsonb`,
 * `uuid`, the network and range types, `tsvector`/`tsquery`, `varbit` — is read as an opaque [Any]
 * today. The page says so, and these entries are what keeps it honest rather than an endorsement of
 * the mapping.
 */
internal val POSTGRES_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "smallintcol" to typeOf<Int>(),
    "integercol" to typeOf<Int>(),
    "bigintcol" to typeOf<Long>(),
    "serialcol" to typeOf<Int>(),
    "decimalcol" to typeOf<BigDecimal>(),
    "numericcol" to typeOf<BigDecimal>(),
    "realcol" to typeOf<Float>(),
    "doublecol" to typeOf<Double>(),
    "moneycol" to typeOf<PGmoney>(),
    "charcol" to typeOf<String>(),
    "varcharcol" to typeOf<String>(),
    "textcol" to typeOf<String>(),
    "byteacol" to typeOf<ByteArray>(),
    "boolcol" to typeOf<Boolean>(),
    "datecol" to typeOf<Date>(),
    "timecol" to typeOf<Time>(),
    "timetzcol" to typeOf<Time>(),
    "timestampcol" to typeOf<Instant>(),
    "timestamptzcol" to typeOf<Instant>(),
    "intervalcol" to typeOf<PGInterval>(),
    "uuidcol" to typeOf<Any>(),
    "jsoncol" to typeOf<Any>(),
    "jsonbcol" to typeOf<Any>(),
    "xmlcol" to typeOf<SQLXML>(),
    "pointcol" to typeOf<PGpoint>(),
    "linecol" to typeOf<PGline>(),
    "lsegcol" to typeOf<PGlseg>(),
    "boxcol" to typeOf<PGbox>(),
    "pathcol" to typeOf<PGpath>(),
    "polygoncol" to typeOf<PGpolygon>(),
    "circlecol" to typeOf<PGcircle>(),
    "cidrcol" to typeOf<Any>(),
    "inetcol" to typeOf<Any>(),
    "macaddrcol" to typeOf<Any>(),
    "macaddr8col" to typeOf<Any>(),
    "bit1col" to typeOf<Boolean>(),
    "bit3col" to typeOf<String>(),
    "varbitcol" to typeOf<Any>(),
    "tsvectorcol" to typeOf<Any>(),
    "tsquerycol" to typeOf<Any>(),
    "int4rangecol" to typeOf<Any>(),
    "int8rangecol" to typeOf<Any>(),
    "numrangecol" to typeOf<Any>(),
    "tsrangecol" to typeOf<Any>(),
    "tstzrangecol" to typeOf<Any>(),
    "daterangecol" to typeOf<Any>(),
    "int4multirangecol" to typeOf<Any>(),
    "intarraycol" to typeOf<Array<*>>(),
    "textarraycol" to typeOf<Array<*>>(),
)
