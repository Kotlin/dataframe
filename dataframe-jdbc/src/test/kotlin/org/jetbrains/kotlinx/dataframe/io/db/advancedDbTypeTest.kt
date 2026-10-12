package org.jetbrains.kotlinx.dataframe.io.db

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.sql.ResultSet
import java.sql.Types
import kotlin.reflect.typeOf

class AdvancedDbTypeTest {

    // Picks the converter by [TableColumnMetadata.size], as its KDoc allows a custom DbType to do.
    private val sizeAwareDbType = object : AdvancedDbType("size-aware") {
        override val driverClassName: String get() = "does.not.matter"

        override fun isSystemTable(tableMetadata: TableMetadata): Boolean = false

        override fun buildTableMetadata(tables: ResultSet): TableMetadata = TableMetadata("", null, null)

        override fun generateConverter(tableColumnMetadata: TableColumnMetadata): AnyJdbcToDataFrameConverter =
            if (tableColumnMetadata.size > 1) jdbcToDfConverterFor<ByteArray>() else jdbcToDfConverterFor<Boolean>()
    }

    @Test
    fun `columns that differ only in size do not share a cached converter`() {
        val bitWide = TableColumnMetadata("bitWide", "BIT", Types.BIT, 8, "java.lang.Boolean")
        val bitFlag = TableColumnMetadata("bitFlag", "BIT", Types.BIT, 1, "java.lang.Boolean")

        sizeAwareDbType.getExpectedJdbcType(bitWide) shouldBe typeOf<ByteArray>()
        sizeAwareDbType.getExpectedJdbcType(bitFlag) shouldBe typeOf<Boolean>()
    }
}
