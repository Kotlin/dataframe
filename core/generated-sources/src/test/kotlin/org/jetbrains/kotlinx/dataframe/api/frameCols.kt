package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class FrameColsTests : ColumnsSelectionDslTests() {

    @Test
    fun `frameCols exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".frameCols() }
        }
    }

    @Test
    fun `frameCols at top-level`() {
        listOf(
            dfWithFrames.select { frameCol },
            dfWithFrames.select { all().frameCols() },
            dfWithFrames.select { frameCols() },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { name[frameCol] },
            dfWithFrames.select { name[frameCol] }.select { all() },
            dfWithFrames.select { name[frameCol] }.select { frameCols() },
            dfWithFrames.select { name[frameCol] }.select { frameCols().all() },
            dfWithFrames.select { name[frameCol] }.select { all().frameCols() },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { frameCol },
            dfWithFrames.select { frameCols { "e" in it.name() } },
            dfWithFrames.select { all().frameCols { "e" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `frameCols at lower level`() {
        listOf(
            dfWithFrames.select { name[frameCol] },
            dfWithFrames.select { name.frameCols { "frame" in it.name() } },
            dfWithFrames.select { name.colsOf<AnyFrame> { "frame" in it.name() } },
            dfWithFrames.select { name.colsOf<AnyFrame>().frameCols { "frame" in it.name() } },
            dfWithFrames.select { "name".frameCols { "frame" in it.name() } },
            dfWithFrames.select { Person::name.frameCols { "frame" in it.name() } },
            dfWithFrames.select { pathOf("name").frameCols { "frame" in it.name() } },
            dfWithFrames.select { it["name"].asColumnGroup().frameCols { "frame" in it.name() } },
        ).shouldAllBeEqual()
    }
}
