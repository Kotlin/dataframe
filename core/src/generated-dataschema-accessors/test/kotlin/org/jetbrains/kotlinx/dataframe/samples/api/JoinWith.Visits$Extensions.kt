@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits>.date: DataColumn<kotlinx.datetime.LocalDate> @JvmName("Visits_date") get() = this["date"] as DataColumn<kotlinx.datetime.LocalDate>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits>.date: kotlinx.datetime.LocalDate @JvmName("Visits_date") get() = this["date"] as kotlinx.datetime.LocalDate
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits?>.date: DataColumn<kotlinx.datetime.LocalDate?> @JvmName("NullableVisits_date") get() = this["date"] as DataColumn<kotlinx.datetime.LocalDate?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits?>.date: kotlinx.datetime.LocalDate? @JvmName("NullableVisits_date") get() = this["date"] as kotlinx.datetime.LocalDate?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits>.userId: DataColumn<Int> @JvmName("Visits_userId") get() = this["userId"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits>.userId: Int @JvmName("Visits_userId") get() = this["userId"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits?>.userId: DataColumn<Int?> @JvmName("NullableVisits_userId") get() = this["userId"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Visits?>.userId: Int? @JvmName("NullableVisits_userId") get() = this["userId"] as Int?
