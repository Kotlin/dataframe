package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.junit.Ignore
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class ReplaceTests {

    @Test
    fun `replace named`() {
        val df = dataFrameOf("a")(1)
        val conv = df.replace { "a"<Int>() named "b" }.with { it.convertToDouble() }
        conv.columnNames() shouldBe listOf("b")
        conv.columnTypes() shouldBe listOf(typeOf<Double>())
    }

    private val df = dataFrameOf("a", "b", "c")(
        1, "x", 10.0,
        2, "y", 20.0,
    )

    // `b` and `c` grouped into `g`
    private val nested = df.group("b", "c").into("g")

    // region replace

    @Test
    fun `replaced column keeps its name and position when the new column has the same name`() {
        val res = df.replace("b").with { it.map { value -> value.toString().uppercase() } }
        res shouldBe dataFrameOf("a", "b", "c")(
            1, "X", 10.0,
            2, "Y", 20.0,
        )
    }

    @Test
    fun `renamed replacement column takes the position of the replaced column`() {
        val res = df.replace("b").with { it.rename("B") }
        res shouldBe dataFrameOf("a", "B", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        )
    }

    @Test
    fun `columns selected with the selector DSL are replaced`() {
        val res = df.replace { colsOf<Number>() }.with { it.rename(it.name().uppercase()) }
        res shouldBe dataFrameOf("A", "b", "C")(
            1, "x", 10.0,
            2, "y", 20.0,
        )
    }

    @Test
    fun `with vararg replaces several columns`() {
        val res = df.replace("a", "c").with(columnOf(7, 8) named "a", columnOf(70, 80) named "c")
        res shouldBe dataFrameOf("a", "b", "c")(
            7, "x", 70,
            8, "y", 80,
        )
    }

    @Test
    fun `with list replaces several columns and the names come from the new columns`() {
        val res = df.replace { "a"<Int>() and "c"<Double>() }
            .with(listOf(columnOf(7, 8) named "p", columnOf(70, 80) named "q"))
        res shouldBe dataFrameOf("p", "b", "q")(
            7, "x", 70,
            8, "y", 80,
        )
    }

    @Test
    fun `new columns are matched to selected columns in the order of the selector and take their positions`() {
        // the selector names `c` first, so `c` gets the first new column, though `a` comes first in `df`
        val res = df.replace { "c"<Double>() and "a"<Int>() }
            .with(columnOf("first", "first") named "first", columnOf("second", "second") named "second")
        res shouldBe dataFrameOf("second", "b", "first")(
            "second", "x", "first",
            "second", "y", "first",
        )
    }

    @Test
    fun `fewer new columns than selected columns throws`() {
        val e = shouldThrow<IllegalArgumentException> {
            df.replace("a", "c").with(columnOf(7, 8) named "a")
        }
        e.message shouldBe "Insufficient number of new columns in 'replace': 1 instead of 2"
    }

    @Test
    fun `new columns beyond the number of selected columns are ignored`() {
        val res = df.replace("a").with(columnOf(7, 8) named "a", columnOf(70, 80) named "z")
        res shouldBe dataFrameOf("a", "b", "c")(
            7, "x", 10.0,
            8, "y", 20.0,
        )
    }

    @Test
    fun `replace with an empty selection returns the dataframe unchanged`() {
        df.replace { colsOf<Boolean>() }.with(columnOf(7, 8) named "z") shouldBe df
    }

    @Test
    fun `transform receives the original dataframe as receiver and the selected column as argument`() {
        // while `c` is transformed, the receiver still holds the original `a`, although `a` is replaced too
        val res = df.replace("a", "c").with { col ->
            this["a"].map { value -> "$value-${col.name()}" } named col.name()
        }
        res shouldBe dataFrameOf("a", "b", "c")(
            "1-a", "x", "1-c",
            "2-a", "y", "2-c",
        )
    }

    @Test
    fun `nested value column is replaced inside its column group`() {
        val res = nested.replace { "g"["b"] }.with { it.map { value -> "$value!" } }
        res shouldBe dataFrameOf("a", "b", "c")(
            1, "x!", 10.0,
            2, "y!", 20.0,
        ).group("b", "c").into("g")
    }

    @Test
    fun `renamed nested column stays in its column group`() {
        val res = nested.replace { "g"["b"] }.with { it.rename("bb") }
        res shouldBe dataFrameOf("a", "bb", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        ).group("bb", "c").into("g")
    }

    @Test
    fun `column group can be replaced with a value column`() {
        val res = nested.replace("g").with { columnOf("G1", "G2") named "g" }
        res shouldBe dataFrameOf("a", "g")(
            1, "G1",
            2, "G2",
        )
    }

    @Test
    fun `renaming the new column to the name of another column throws`() {
        shouldThrow<DuplicateColumnPathInsertException> {
            df.replace("a").with { it.rename("b") }
        }
    }

    @Test
    fun `new column may take the name of a column in another column group`() {
        // `a` is taken at the top level, but `b` is inside `g`
        val res = nested.replace { "g"["b"] }.with { it.rename("a") }
        res shouldBe dataFrameOf("top", "a", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        ).group("a", "c").into("g").rename("top").into("a")
    }

    @Test
    fun `renaming the new column to the name of a sibling in the same column group throws`() {
        shouldThrow<DuplicateColumnPathInsertException> {
            nested.replace { "g"["b"] }.with { it.rename("c") }
        }
    }

    @Test
    fun `replaced columns can swap their names`() {
        val res = df.replace("a", "b").with { if (it.name() == "a") it.rename("b") else it.rename("a") }
        res shouldBe dataFrameOf("b", "a", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        )
    }

    @Test
    fun `new column of a different size throws`() {
        shouldThrow<UnequalColumnSizesException> {
            df.replace("a").with(columnOf(1, 2, 3) named "a")
        }
    }

    // region known issue: replacing a column group together with its child
    // TODO(#418): once fixed, remove the two pinning tests below, enable the two ignored ones, and remove
    //  `ReplaceGroupAndChildSnippet` from `api/replace.kt` (included in `replace { }`, `with(newColumns)`
    //  and `with { }`) and the section "Replace a column group and a column inside it" from `replace.md`

    @Test
    fun `selecting a column group and its child replaces only the group`() {
        val res = nested.replace { "g" and "g"["b"] }.with { it.rename(it.name() + "_new") }
        // the transform is never called on `b`, and no error is reported
        res shouldBe dataFrameOf("a", "b", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        ).group("b", "c").into("g_new")
    }

    @Ignore("replace calls the transform only on the group when a column group and its child are selected")
    @Test
    fun `selecting a column group and its child replaces both`() {
        val res = nested.replace { "g" and "g"["b"] }.with { it.rename(it.name() + "_new") }
        res shouldBe dataFrameOf("a", "b_new", "c")(
            1, "x", 10.0,
            2, "y", 20.0,
        ).group("b_new", "c").into("g_new")
    }

    @Test
    fun `with new columns, a column group and its child take one new column`() {
        // two columns are selected, but only one new column is needed, and no error is reported
        val res = nested.replace { "g" and "g"["b"] }.with(columnOf("G1", "G2") named "g")
        res shouldBe dataFrameOf("a", "g")(
            1, "G1",
            2, "G2",
        )
    }

    @Ignore("replace uses no new column for a column inside a selected column group")
    @Test
    fun `with new columns, fewer new columns than a column group and its child throws`() {
        shouldThrow<IllegalArgumentException> {
            nested.replace { "g" and "g"["b"] }.with(columnOf("G1", "G2") named "g")
        }
    }

    // endregion

    // endregion

    // region replaceAll

    private val people = dataFrameOf("name", "city", "n")(
        "unknown", null, 1,
        "Bob", "unknown", null,
        "Al", "Paris", 2,
    )

    @Test
    fun `replaceAll replaces a value in every column`() {
        people.replaceAll("unknown" to "?") shouldBe dataFrameOf("name", "city", "n")(
            "?", null, 1,
            "Bob", "?", null,
            "Al", "Paris", 2,
        )
    }

    @Test
    fun `replaceAll replaces several values at once`() {
        people.replaceAll("unknown" to "?", "Bob" to "Robert", 1 to 100) shouldBe dataFrameOf("name", "city", "n")(
            "?", null, 100,
            "Robert", "?", null,
            "Al", "Paris", 2,
        )
    }

    @Test
    fun `replaceAll replaces values only in the selected columns`() {
        people.replaceAll("unknown" to "?", columns = { "city"<String?>() }) shouldBe dataFrameOf("name", "city", "n")(
            "unknown", null, 1,
            "Bob", "?", null,
            "Al", "Paris", 2,
        )
    }

    @Test
    fun `replaceAll replaces values inside column groups by default`() {
        nested.replaceAll("x" to "X", 10.0 to 0.0) shouldBe dataFrameOf("a", "b", "c")(
            1, "X", 0.0,
            2, "y", 20.0,
        ).group("b", "c").into("g")
    }

    @Test
    fun `replaceAll without matches returns an equal dataframe`() {
        people.replaceAll("nobody" to "?") shouldBe people
    }

    @Test
    fun `replaceAll uses the last pair when the same value is given twice`() {
        people.replaceAll("unknown" to "first", "unknown" to "second") shouldBe dataFrameOf("name", "city", "n")(
            "second", null, 1,
            "Bob", "second", null,
            "Al", "Paris", 2,
        )
    }

    @Test
    fun `replaceAll replaces nulls and the column becomes non-nullable`() {
        val res = people.replaceAll(null to "N/A", columns = { "city"<String?>() })
        res shouldBe dataFrameOf("name", "city", "n")(
            "unknown", "N/A", 1,
            "Bob", "unknown", null,
            "Al", "Paris", 2,
        )
        res["city"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `replaceAll cannot put a value of another type into a column`() {
        // the default selection includes `n: Int?`, which cannot hold "N/A"
        val e = shouldThrow<IllegalStateException> {
            people.replaceAll(null to "N/A")
        }
        e.message shouldBe
            "Could not update column 'n': Cannot add a value of class kotlin.String to a column of type kotlin.Int?. " +
            "Value: 'N/A'."
    }

    @Test
    fun `replaceAll replaces a value with null`() {
        val res = people.replaceAll("unknown" to null)
        res shouldBe dataFrameOf("name", "city", "n")(
            null, null, 1,
            "Bob", null, null,
            "Al", "Paris", 2,
        )
        res["name"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `replaceAll puts null into a column of non-nullable values, and the column becomes nullable`() {
        val res = df.replaceAll(1 to null)
        res["a"].values() shouldBe listOf(null, 2)
        res["a"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `replaceAll does not change the dataframes inside frame columns`() {
        val grouped = dataFrameOf("k", "v")(
            1, "unknown",
            2, "x",
        ).groupBy("k").toDataFrame()
        val res = grouped.replaceAll("unknown" to "?")
        res["group"][0] shouldBe dataFrameOf("k", "v")(1, "unknown")
    }

    // endregion
}
