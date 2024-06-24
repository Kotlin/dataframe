package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class FrameColTests : ColumnsSelectionDslTests() {
    @Test
    fun `frameCol exceptions`() {
        shouldThrow<IllegalArgumentException> {
            dfWithFrames.select { frameCol("name") }
        }
        shouldThrow<IllegalStateException> {
            dfWithFrames.select { frameCol("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            dfWithFrames.select { name.frameCol("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            dfWithFrames.select { "age".frameCol("test") }
        }
        shouldThrow<IndexOutOfBoundsException> {
            dfWithFrames.select { frameCol(100) }
        }
    }

    @Test
    fun `frameCol at top-level`() {
        listOf(
            dfWithFrames.select { frameCol },
            dfWithFrames.select { frameCol(frameCol) },
            dfWithFrames.select { frameCol("frameCol") },
            dfWithFrames.select { frameCol<Person>("frameCol") },
            dfWithFrames.select { frameCol(pathOf("frameCol")) },
            dfWithFrames.select { frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { frameCols().frameCol(0) },
            dfWithFrames.select { all().frameCol(5) },
            dfWithFrames.select { frameCol(5) },
            dfWithFrames.select { frameCol<Person>(5) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `frameCol at lower level`() {
        val frameColAccessor = frameCol
        listOf(
            dfWithFrames.select { name[frameCol] },
            // reference
            dfWithFrames.select { name.frameCol(frameColAccessor) },
            dfWithFrames.select { colGroup("name").frameCol(frameColAccessor) },
            dfWithFrames.select { "name".frameCol(frameColAccessor) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol(frameColAccessor) },
            dfWithFrames.select { Person::name.frameCol(frameColAccessor) },
            dfWithFrames.select { pathOf("name").frameCol(frameColAccessor) },
            // name
            dfWithFrames.select { name.frameCol("frameCol") },
            dfWithFrames.select { name.frameCol<Person>("frameCol") },
            dfWithFrames.select { colGroup("name").frameCol("frameCol") },
            dfWithFrames.select { colGroup("name").frameCol<Person>("frameCol") },
            dfWithFrames.select { "name".frameCol("frameCol") },
            dfWithFrames.select { "name".frameCol<Person>("frameCol") },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol("frameCol") },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol<Person>("frameCol") },
            dfWithFrames.select { Person::name.frameCol("frameCol") },
            dfWithFrames.select { Person::name.frameCol<Person>("frameCol") },
            dfWithFrames.select { pathOf("name").frameCol("frameCol") },
            dfWithFrames.select { pathOf("name").frameCol<Person>("frameCol") },
            // path
            dfWithFrames.select { name.frameCol(pathOf("frameCol")) },
            dfWithFrames.select { name.frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { colGroup("name").frameCol(pathOf("frameCol")) },
            dfWithFrames.select { colGroup("name").frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { "name".frameCol(pathOf("frameCol")) },
            dfWithFrames.select { "name".frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol(pathOf("frameCol")) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { Person::name.frameCol(pathOf("frameCol")) },
            dfWithFrames.select { Person::name.frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { pathOf("name").frameCol(pathOf("frameCol")) },
            dfWithFrames.select { pathOf("name").frameCol<Person>(pathOf("frameCol")) },
            dfWithFrames.select { frameCol("name"["frameCol"]) },
            dfWithFrames.select { frameCol<Person>("name"["frameCol"]) },
            dfWithFrames.select { asSingleColumn().frameCol("name"["frameCol"]) },
            dfWithFrames.select { asSingleColumn().frameCol<Person>("name"["frameCol"]) },
            // property
            dfWithFrames.select { name.frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { colGroup("name").frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { "name".frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { Person::name.frameCol(PersonWithFrame::frameCol) },
            dfWithFrames.select { pathOf("name").frameCol(PersonWithFrame::frameCol) },
            // index
            dfWithFrames.select { name.frameCol(2) },
            dfWithFrames.select { name.frameCol<Person>(2) },
            dfWithFrames.select { colGroup("name").frameCol(2) },
            dfWithFrames.select { colGroup("name").frameCol<Person>(2) },
            dfWithFrames.select { "name".frameCol(2) },
            dfWithFrames.select { "name".frameCol<Person>(2) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol(2) },
            dfWithFrames.select { NonDataSchemaPerson::name.frameCol<Person>(2) },
            dfWithFrames.select { Person::name.frameCol(2) },
            dfWithFrames.select { Person::name.frameCol<Person>(2) },
            dfWithFrames.select { pathOf("name").frameCol(2) },
            dfWithFrames.select { pathOf("name").frameCol<Person>(2) },
        ).shouldAllBeEqual()
    }
}
