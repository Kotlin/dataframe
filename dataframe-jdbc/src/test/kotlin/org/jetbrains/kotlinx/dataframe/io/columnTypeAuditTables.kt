package org.jetbrains.kotlinx.dataframe.io

import org.intellij.lang.annotations.Language

/**
 * Probe tables for [assertColumnTypesMatchValues], one per database, each holding a column per SQL type.
 *
 * They deliberately cover more types than the other tests use — including the ones the type-mapping
 * pages under `docs/StardustDocs/topics/io/` document — because the point of the audit is to catch a
 * type the reading pipeline gets wrong *before* a user runs into it. Every column must be non-null in
 * the inserted row: a `null` tells us nothing about the class of the value.
 *
 * `BIT(1)` vs `BIT(3)` and the two `SQL_VARIANT` columns are not duplicates: in each pair the driver
 * reports the same metadata but returns values of different classes, which is exactly the trap of #2087.
 */

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
