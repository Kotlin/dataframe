@file:Suppress("RemoveExplicitTypeArguments")

package org.jetbrains.kotlinx.dataframe.documentation.samples

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.jetbrains.kotlinx.dataframe.io.read

internal interface ApiLevels {

    fun strings() {
        DataFrame.read("titanic.csv")
            .add("lastName") { "name"<String>().split(",").last() }
            .dropNulls("age")
            .filter {
                "survived"<Boolean>() &&
                    "home"<String>().endsWith("NY") &&
                    "age"<Int>() in 10..20
            }
    }

    fun accessors1() {
        val survived by column<Boolean>() // accessor for Boolean column with name 'survived'
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()
    }

    fun accessors2() {
        val survived by column<Boolean>()
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()

        DataFrame.read("titanic.csv")
            .add(lastName) { name().split(",").last() }
            .dropNulls { age }
            .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
    }

    fun accessors3() {
        val survived by column<Boolean>()
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()

        DataFrame.read("titanic.csv")
            .add(lastName) { name().split(",").last() }
            .dropNulls { age }
            .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
    }

    fun kproperties1() {
        data class Passenger(
            val survived: Boolean,
            val home: String,
            val age: Int,
            val lastName: String
        )

        val passengers = DataFrame.read("titanic.csv")
            .add(Passenger::lastName) { "name"<String>().split(",").last() }
            .dropNulls(Passenger::age)
            .filter {
                it[Passenger::survived] &&
                    it[Passenger::home].endsWith("NY") &&
                    it[Passenger::age] in 10..20
            }
            .toListOf<Passenger>()
    }

    fun kproperties2() {
        data class Passenger(
            @ColumnName("survived") val isAlive: Boolean,
            @ColumnName("home") val city: String,
            val name: String
        )

        val passengers = DataFrame.read("titanic.csv")
            .filter { it.get(Passenger::city).endsWith("NY") }
            .toListOf<Passenger>()
    }

    //    @DataSchema
    interface TitanicPassenger {
        val survived: Boolean
        val home: String
        val age: Int
        val name: String
    }

    fun extensionProperties2() {
        val df = DataFrame.read("titanic.csv").cast<TitanicPassenger>()

        df.add("lastName") { name.split(",").last() }
            .dropNulls { age }
            .filter { survived && home.endsWith("NY") && age in 10..20 }
    }

    fun extensionProperties1() {
        val df = DataFrame.read("titanic.csv")
    }
}

internal val ColumnsContainer<ApiLevels.TitanicPassenger>.age: DataColumn<Int> @JvmName("TitanicPassenger_age") get() = this["age"] as DataColumn<Int>
internal val DataRow<ApiLevels.TitanicPassenger>.age: Int @JvmName("TitanicPassenger_age") get() = this["age"] as Int
internal val ColumnsContainer<ApiLevels.TitanicPassenger?>.age: DataColumn<Int?> @JvmName("NullableTitanicPassenger_age") get() = this["age"] as DataColumn<Int?>
internal val DataRow<ApiLevels.TitanicPassenger?>.age: Int? @JvmName("NullableTitanicPassenger_age") get() = this["age"] as Int?
internal val ColumnsContainer<ApiLevels.TitanicPassenger>.home: DataColumn<String> @JvmName("TitanicPassenger_home") get() = this["home"] as DataColumn<String>
internal val DataRow<ApiLevels.TitanicPassenger>.home: String @JvmName("TitanicPassenger_home") get() = this["home"] as String
internal val ColumnsContainer<ApiLevels.TitanicPassenger?>.home: DataColumn<String?> @JvmName("NullableTitanicPassenger_home") get() = this["home"] as DataColumn<String?>
internal val DataRow<ApiLevels.TitanicPassenger?>.home: String? @JvmName("NullableTitanicPassenger_home") get() = this["home"] as String?
internal val ColumnsContainer<ApiLevels.TitanicPassenger>.name: DataColumn<String> @JvmName("TitanicPassenger_name") get() = this["name"] as DataColumn<String>
internal val DataRow<ApiLevels.TitanicPassenger>.name: String @JvmName("TitanicPassenger_name") get() = this["name"] as String
internal val ColumnsContainer<ApiLevels.TitanicPassenger?>.name: DataColumn<String?> @JvmName("NullableTitanicPassenger_name") get() = this["name"] as DataColumn<String?>
internal val DataRow<ApiLevels.TitanicPassenger?>.name: String? @JvmName("NullableTitanicPassenger_name") get() = this["name"] as String?
internal val ColumnsContainer<ApiLevels.TitanicPassenger>.survived: DataColumn<Boolean> @JvmName("TitanicPassenger_survived") get() = this["survived"] as DataColumn<Boolean>
internal val DataRow<ApiLevels.TitanicPassenger>.survived: Boolean @JvmName("TitanicPassenger_survived") get() = this["survived"] as Boolean
internal val ColumnsContainer<ApiLevels.TitanicPassenger?>.survived: DataColumn<Boolean?> @JvmName("NullableTitanicPassenger_survived") get() = this["survived"] as DataColumn<Boolean?>
internal val DataRow<ApiLevels.TitanicPassenger?>.survived: Boolean? @JvmName("NullableTitanicPassenger_survived") get() = this["survived"] as Boolean?
