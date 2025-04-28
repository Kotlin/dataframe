@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isValueColumn
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.explainer.PluginCallbackProxy
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn
import org.junit.After
import org.junit.Before

@Suppress("ktlint:standard:argument-list-wrapping")
public open class TestBase {

    companion object {
        internal const val OUTPUTS = "DATAFRAME_SAVE_OUTPUTS"
    }

    @Before
    fun start() {
        if (System.getenv(OUTPUTS) != null) {
            PluginCallbackProxy.start()
        }
    }

    @After
    fun save() {
        if (System.getenv(OUTPUTS) != null) {
            PluginCallbackProxy.save()
        }
    }

    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Charlie", "Daniels", 20, "Moscow", null, false,
        "Charlie", "Chaplin", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Wolf", 20, null, 55, false,
        "Charlie", "Byrd", 30, "Moscow", 90, true,
    ).group("firstName", "lastName").into("name").cast<Person>()

    val dfGroup = df.convert { name.firstName }.asColumn {
        val firstName by it
        val secondName by it.map<_, String?> { null }.asValueColumn()
        val thirdName by it.map<_, String?> { null }.asValueColumn()

        dataFrameOf(firstName, secondName, thirdName)
            .cast<FirstNames>(verify = true)
            .asColumnGroup("firstName")
    }.cast<Person2>(verify = true)

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name>
        val weight: Int?
        val isHappy: Boolean
    }

    interface NonDataSchemaPerson {
        val age: Int
        val city: String?
        val name: Name
        val weight: Int?
        val isHappy: Boolean
    }

    @DataSchema
    interface FirstNames {
        val firstName: String
        val secondName: String?
        val thirdName: String?
    }

    @DataSchema
    interface Name2 {
        val firstName: DataRow<FirstNames>
        val lastName: String
    }

    @DataSchema
    interface Person2 {
        val age: Int
        val city: String?
        val name: DataRow<Name2>
        val weight: Int?
        val isHappy: Boolean
    }

    infix fun <T, U : T> T.willBe(expected: U?) = shouldBe(expected)

    /**
     * Asserts that all elements of the iterable are equal to each other
     */
    fun <T> Iterable<T>.shouldAllBeEqual(): Iterable<T> {
        this should {
            it.reduce { a, b ->
                a shouldBe b
                b
            }
        }
        return this
    }

    /**
     * Helper function to print List<ColumnWithPath<*>> in a readable way
     */
    fun List<ColumnWithPath<*>>.print() {
        forEach {
            if (it.isValueColumn()) {
                println("${it.name}: ${it.type()}")
            } else {
                it.print()
            }
        }
        println()
    }

    /**
     * Overload for shouldBe for List<ColumnWithPath<*>> to compare only names and paths
     * since the instances of ColumnWithPath are different
     */
    infix fun List<ColumnWithPath<*>>.shouldBe(other: List<ColumnWithPath<*>>) {
        this.map { it.name to it.path } shouldBe other.map { it.name to it.path }
    }

    /**
     * Overload for shouldNotBe for List<ColumnWithPath<*>> to compare only names and paths
     * since the instances of ColumnWithPath are different
     */
    infix fun List<ColumnWithPath<*>>.shouldNotBe(other: List<ColumnWithPath<*>>) {
        this.map { it.name to it.path } shouldNotBe other.map { it.name to it.path }
    }
}
