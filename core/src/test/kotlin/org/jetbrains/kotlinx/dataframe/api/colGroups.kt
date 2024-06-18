package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ColGroupsTests : ColumnsSelectionDslTests() {
    @Test
    fun `colGroups exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".colGroups() }
        }
    }

    @Test
    fun `colGroups at top-level`() {
        listOf(
            df.select { name },
            df.select { all().colGroups() },
            df.select { colGroups() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { name }.select { all() },
            df.select { name }.select { colGroups() },
            df.select { name }.select { colGroups().all() },
            df.select { name }.select { all().colGroups() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { colGroups { "e" in it.name() } },
            df.select { all().colGroups { "e" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `colGroups at lower level`() {
        listOf(
            dfGroup.select { name.firstName },
            dfGroup.select { name.colGroups { "Name" in it.name() } },
            dfGroup.select { name.colsOf<AnyRow> { "Name" in it.name() } },
            dfGroup.select { name.colsOf<AnyRow>().colGroups { "Name" in it.name() } },
            dfGroup.select { "name".colGroups { "Name" in it.name() } },
            dfGroup.select { Person::name.colGroups { "Name" in it.name() } },
            dfGroup.select { pathOf("name").colGroups { "Name" in it.name() } },
            dfGroup.select { it["name"].asColumnGroup().colGroups { "Name" in it.name() } },
        ).shouldAllBeEqual()
    }
}
