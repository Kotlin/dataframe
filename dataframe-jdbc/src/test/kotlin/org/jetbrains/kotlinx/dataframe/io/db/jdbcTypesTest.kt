@file:Suppress("ClassName")

package org.jetbrains.kotlinx.dataframe.io.db

import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// TODO: complete and enhance (#1736)
@RunWith(Enclosed::class)
class JdbcTypesTest {

    abstract class ColumnType(
        val sqlTypeName: String,
        val jdbcType: Int,
        val javaClassName: String,
        val isNullable: Boolean,
        val expectedKotlinType: KType,
    ) {
        fun mockkColMetaData() =
            TableColumnMetadata(
                "name",
                sqlTypeName,
                jdbcType,
                10,
                javaClassName,
                isNullable,
            )
    }

    class MariaDBTypes {

        object BIGINT_UNSIGNED : ColumnType(
            "BIGINT UNSIGNED",
            20,
            "java.math.BigInteger",
            false,
            typeOf<BigInteger>(),
        )

        val types: List<ColumnType> = listOf(
            BIGINT_UNSIGNED,
        )

        @Test
        fun `all MariaDB SQL types should match expected type`() {
            types.forEach { type ->
                MariaDb.getExpectedJdbcType(type.mockkColMetaData()) shouldBe type.expectedKotlinType
            }
        }
    }

    class MySqlDBTypes {

        object BIGINT_UNSIGNED : ColumnType(
            "BIGINT UNSIGNED",
            20,
            "java.math.BigInteger",
            false,
            typeOf<BigInteger>(),
        )

        val types: List<ColumnType> = listOf(
            BIGINT_UNSIGNED,
        )

        @Test
        fun `all MariaDB SQL types should match expected type`() {
            types.forEach { type ->
                MySql.getExpectedJdbcType(type.mockkColMetaData()) shouldBe type.expectedKotlinType
            }
        }
    }

    class SqliteTypes {

        // Taken from #964

        object LONGVARCHAR_NON_NULL : ColumnType(
            "LONGVARCHAR",
            -2,
            "java.lang.Object",
            false,
            typeOf<String>(),
        )

        object LONGVARCHAR_NULLABLE : ColumnType(
            "LONGVARCHAR",
            12,
            "java.lang.String",
            true,
            typeOf<String?>(),
        )

        @Test
        fun `identity forType with non-null T resolves to non-null String`() {
            // User's KType is authoritative; column nullability is not applied on top of it.
            val sqliteCustom = Sqlite.withCustomConverters {
                forType<String>("LONGVARCHAR")
            }
            sqliteCustom.getExpectedJdbcType(LONGVARCHAR_NON_NULL.mockkColMetaData()) shouldBe
                LONGVARCHAR_NON_NULL.expectedKotlinType
        }

        @Test
        fun `identity forType with nullable T resolves to nullable String`() {
            val sqliteCustom = Sqlite.withCustomConverters {
                forType<String?>("LONGVARCHAR")
            }
            sqliteCustom.getExpectedJdbcType(LONGVARCHAR_NULLABLE.mockkColMetaData()) shouldBe
                LONGVARCHAR_NULLABLE.expectedKotlinType
        }
    }
}
