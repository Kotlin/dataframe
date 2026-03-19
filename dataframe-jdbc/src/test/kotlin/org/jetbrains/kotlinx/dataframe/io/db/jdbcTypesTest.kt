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
        val expectedKotlinType: KType,
    ) {
        fun mockkColMetaData() =
            TableColumnMetadata(
                "name",
                sqlTypeName,
                jdbcType,
                10,
                javaClassName,
                false,
            )
    }

    class MariaDBTypes {

        object BIGINT_UNSIGNED : ColumnType(
            "BIGINT UNSIGNED",
            20,
            "java.math.BigInteger",
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
}
