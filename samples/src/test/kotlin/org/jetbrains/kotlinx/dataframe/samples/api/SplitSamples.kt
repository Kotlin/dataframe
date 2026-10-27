package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.default
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.inplace
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.intoColumns
import org.jetbrains.kotlinx.dataframe.api.intoRows
import org.jetbrains.kotlinx.dataframe.api.inward
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.splitInto
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

/**
 * Samples for the KDocs of the `split` operation.
 */
class SplitSamples : DataFrameSampleHelper("split", "api") {

    @DataSchema
    interface Order {
        val item: String
        val price: Double
    }

    @DataSchema
    interface Customer {
        val name: String
        val address: String
        val phones: List<String>
        val location: Pair<Double, Double>
        val orders: DataFrame<Order>
    }

    private val df: DataFrame<Customer> = dataFrameOf(
        "name" to columnOf("Alice Cooper", "Bob Dylan", "Charlie Chaplin"),
        "address" to columnOf("London, Baker Street, 221B", "Paris, Rue de Rivoli", "Milan"),
        "phones" to columnOf<List<String>>(listOf("+44 20 1234", "+44 77 5678"), listOf("+33 1 2345"), emptyList()),
        "location" to columnOf(51.52 to -0.16, 48.86 to 2.35, 45.46 to 9.19),
        "orders" to columnOf(
            dataFrameOf("item" to columnOf("Book", "Pen"), "price" to columnOf(12.5, 1.5)),
            dataFrameOf("item" to columnOf("Lamp"), "price" to columnOf(30.0)),
            dataFrameOf("item" to emptyList<String>().toColumn(), "price" to emptyList<Double>().toColumn()),
        ),
    ).cast()

    @Test
    fun split_properties() {
        // SampleStart
        // Split each full name by the space into two new columns
        df.split { name }.by(" ").into("firstName", "lastName")
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("firstName", "lastName", "address", "phones", "location", "orders")
                it["firstName"].toList() shouldBe listOf("Alice", "Bob", "Charlie")
                it["lastName"].toList() shouldBe listOf("Cooper", "Dylan", "Chaplin")
            }
    }

    @Test
    fun split_strings() {
        // SampleStart
        // Split each full name by the space into two new columns
        df.split("name").by(" ").into("firstName", "lastName")
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("firstName", "lastName", "address", "phones", "location", "orders")
                it["firstName"].toList() shouldBe listOf("Alice", "Bob", "Charlie")
            }
    }

    @Test
    fun splitCast() {
        // SampleStart
        // `split(String)` doesn't know the column type: `cast` lets `by` treat the values as `String`
        df.split("name").cast<String>().by { it.split(" ").reversed() }.into("lastName", "firstName")
            // SampleEnd
            .also {
                it["lastName"].toList() shouldBe listOf("Cooper", "Dylan", "Chaplin")
                it["firstName"].toList() shouldBe listOf("Alice", "Bob", "Charlie")
            }
    }

    @Test
    fun splitByCharDelimiters() {
        // SampleStart
        // Split each address by commas; parts are trimmed by default.
        // Rows with fewer parts get `null` in the missing columns
        df.split { address }.by(',').into("city", "street", "house")
            // SampleEnd
            .also {
                it["city"].toList() shouldBe listOf("London", "Paris", "Milan")
                it["street"].toList() shouldBe listOf("Baker Street", "Rue de Rivoli", null)
                it["house"].toList() shouldBe listOf("221B", null, null)
            }
    }

    @Test
    fun splitByStringDelimiters() {
        // SampleStart
        // Split each address by commas into at most two parts, without trimming them
        df.split { address }.by(", ", limit = 2, trim = false).into("city", "rest")
            // SampleEnd
            .also {
                it["city"].toList() shouldBe listOf("London", "Paris", "Milan")
                it["rest"].toList() shouldBe listOf("Baker Street, 221B", "Rue de Rivoli", null)
            }
    }

    @Test
    fun splitByTransform() {
        // SampleStart
        // Split each full name with a custom splitter function
        df.split { name }.by { it.split(" ").reversed() }.into("lastName", "firstName")
            // SampleEnd
            .also {
                it["lastName"].toList() shouldBe listOf("Cooper", "Dylan", "Chaplin")
                it["firstName"].toList() shouldBe listOf("Alice", "Bob", "Charlie")
            }
    }

    @Test
    fun splitDefault() {
        // SampleStart
        // Split the lists of phones into two columns; missing phones are replaced with "none"
        df.split { phones }.default("none").into("primaryPhone", "secondaryPhone")
            // SampleEnd
            .also {
                it["primaryPhone"].toList() shouldBe listOf("+44 20 1234", "+33 1 2345", "none")
                it["secondaryPhone"].toList() shouldBe listOf("+44 77 5678", "none", "none")
            }
    }

    @Test
    fun splitWithTransformDefault() {
        // SampleStart
        // Split each address by commas; missing parts are replaced with "unknown"
        df.split { address }.by(",").default("unknown").into("city", "street", "house")
            // SampleEnd
            .also {
                it["street"].toList() shouldBe listOf("Baker Street", "Rue de Rivoli", "unknown")
                it["house"].toList() shouldBe listOf("221B", "unknown", "unknown")
            }
    }

    @Test
    fun splitIntoDefaultNames() {
        // SampleStart
        // Only one name is given: the other two new columns are named "split1" and "split2"
        df.split { address }.by(",").into("city")
            // SampleEnd
            .also {
                it.columnNames() shouldBe
                    listOf("name", "city", "split1", "split2", "phones", "location", "orders")
            }
    }

    @Test
    fun splitIntoExtraNames() {
        // SampleStart
        // Only one name is given: the other two new columns are named
        // by `extraNamesGenerator`, which receives the index of each extra column, starting from 1
        df.split { address }.by(",").into("city") { "addressPart" + it }
            // SampleEnd
            .also {
                it.columnNames() shouldBe
                    listOf("name", "city", "addressPart1", "addressPart2", "phones", "location", "orders")
            }
    }

    @Test
    fun splitIterableInto() {
        // SampleStart
        // `List` values are split into their elements, so no `by` is needed
        df.split { phones }.into("primaryPhone", "secondaryPhone")
            // SampleEnd
            .also {
                it["primaryPhone"].toList() shouldBe listOf("+44 20 1234", "+33 1 2345", null)
                it["secondaryPhone"].toList() shouldBe listOf("+44 77 5678", null, null)
            }
    }

    @Test
    fun splitFrameInto() {
        // SampleStart
        // `DataFrame` values are split into their rows, so each new column is a column group
        df.split { orders }.into("firstOrder", "secondOrder")
            // SampleEnd
            .also {
                it.getColumnGroup("firstOrder")["item"].toList() shouldBe listOf("Book", "Lamp", null)
                it.getColumnGroup("secondOrder")["item"].toList() shouldBe listOf("Pen", null, null)
            }
    }

    @Test
    fun splitPairInto() {
        // SampleStart
        // `Pair` values are split into their first and second components
        df.split { location }.into("latitude", "longitude")
            // SampleEnd
            .also {
                it["latitude"].toList() shouldBe listOf(51.52, 48.86, 45.46)
                it["longitude"].toList() shouldBe listOf(-0.16, 2.35, 9.19)
            }
    }

    @Test
    fun splitInward() {
        // SampleStart
        // Split each full name by the space into two columns nested inside the "name" column group
        df.split { name }.by(" ").inward("firstName", "lastName")
            // SampleEnd
            .also {
                it.getColumnGroup("name").columnNames() shouldBe listOf("firstName", "lastName")
                it.getColumnGroup("name")["lastName"].toList() shouldBe listOf("Cooper", "Dylan", "Chaplin")
            }
    }

    @Test
    fun splitIterableInward() {
        // SampleStart
        // `List` values are split into their elements, nested inside the "phones" column group
        df.split { phones }.inward("primary", "secondary")
            // SampleEnd
            .also {
                it.getColumnGroup("phones")["primary"].toList() shouldBe listOf("+44 20 1234", "+33 1 2345", null)
            }
    }

    @Test
    fun splitFrameInward() {
        // SampleStart
        // `DataFrame` values are split into their rows, nested inside the "orders" column group
        df.split { orders }.inward("first", "second")
            // SampleEnd
            .also {
                it.getColumnGroup("orders").columnNames() shouldBe listOf("first", "second")
            }
    }

    @Test
    fun splitPairInward() {
        // SampleStart
        // `Pair` values are split into their components, nested inside the "location" column group
        df.split { location }.inward("latitude", "longitude")
            // SampleEnd
            .also {
                it.getColumnGroup("location")["longitude"].toList() shouldBe listOf(-0.16, 2.35, 9.19)
            }
    }

    @Test
    fun splitIntoColumns() {
        // SampleStart
        // Each `DataFrame` of orders becomes a single row of the "orders" column group,
        // where each column stores a `List` of the values of the original column
        df.split { orders }.intoColumns()
            // SampleEnd
            .also {
                it.getColumnGroup("orders")["item"].toList() shouldBe
                    listOf(listOf("Book", "Pen"), listOf("Lamp"), emptyList())
            }
    }

    @Test
    fun splitIntoRows() {
        // SampleStart
        // Split each address by commas, placing each part in a separate row.
        // Values in other columns are duplicated
        df.split { address }.by(",").intoRows()
            // SampleEnd
            .also {
                it["address"].toList() shouldBe
                    listOf("London", "Baker Street", "221B", "Paris", "Rue de Rivoli", "Milan")
                it["name"].toList() shouldBe
                    listOf("Alice Cooper", "Alice Cooper", "Alice Cooper", "Bob Dylan", "Bob Dylan", "Charlie Chaplin")
            }
    }

    @Test
    fun splitIterableIntoRows() {
        // SampleStart
        // Place each phone in a separate row.
        // Rows with an empty list of phones are removed, as `dropEmpty` is `true` by default
        df.split { phones }.intoRows()
            // SampleEnd
            .also {
                it["phones"].toList() shouldBe listOf("+44 20 1234", "+44 77 5678", "+33 1 2345")
                it["name"].toList() shouldBe listOf("Alice Cooper", "Alice Cooper", "Bob Dylan")
            }
    }

    @Test
    fun splitFrameIntoRows() {
        // SampleStart
        // Place each order in a separate row; "orders" becomes a column group.
        // Keep the customers without orders, with `null` values in "orders"
        df.split { orders }.intoRows(dropEmpty = false)
            // SampleEnd
            .also {
                it.getColumnGroup("orders")["item"].toList() shouldBe listOf("Book", "Pen", "Lamp", null)
                it["name"].toList() shouldBe listOf("Alice Cooper", "Alice Cooper", "Bob Dylan", "Charlie Chaplin")
            }
    }

    @Test
    fun splitInplace() {
        // SampleStart
        // Split each address by commas, keeping the parts as a `List` in the "address" column
        df.split { address }.by(",").inplace()
            // SampleEnd
            .also {
                it["address"].toList() shouldBe
                    listOf(listOf("London", "Baker Street", "221B"), listOf("Paris", "Rue de Rivoli"), listOf("Milan"))
            }
    }

    @Test
    fun splitColumnInto() {
        // SampleStart
        // Split a column of lists into a new DataFrame with a column for each element position
        df.phones.splitInto("primaryPhone", "secondaryPhone")
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("primaryPhone", "secondaryPhone")
                it["primaryPhone"].toList() shouldBe listOf("+44 20 1234", "+33 1 2345", null)
            }
    }
}
