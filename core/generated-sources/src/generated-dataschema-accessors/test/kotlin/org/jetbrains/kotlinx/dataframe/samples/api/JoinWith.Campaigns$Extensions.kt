@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.endDate: DataColumn<kotlinx.datetime.LocalDate> @JvmName("Campaigns_endDate") get() = this["endDate"] as DataColumn<kotlinx.datetime.LocalDate>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.endDate: kotlinx.datetime.LocalDate @JvmName("Campaigns_endDate") get() = this["endDate"] as kotlinx.datetime.LocalDate
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.endDate: DataColumn<kotlinx.datetime.LocalDate?> @JvmName("NullableCampaigns_endDate") get() = this["endDate"] as DataColumn<kotlinx.datetime.LocalDate?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.endDate: kotlinx.datetime.LocalDate? @JvmName("NullableCampaigns_endDate") get() = this["endDate"] as kotlinx.datetime.LocalDate?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.name: DataColumn<String> @JvmName("Campaigns_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.name: String @JvmName("Campaigns_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.name: DataColumn<String?> @JvmName("NullableCampaigns_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.name: String? @JvmName("NullableCampaigns_name") get() = this["name"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.startDate: DataColumn<kotlinx.datetime.LocalDate> @JvmName("Campaigns_startDate") get() = this["startDate"] as DataColumn<kotlinx.datetime.LocalDate>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns>.startDate: kotlinx.datetime.LocalDate @JvmName("Campaigns_startDate") get() = this["startDate"] as kotlinx.datetime.LocalDate
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.startDate: DataColumn<kotlinx.datetime.LocalDate?> @JvmName("NullableCampaigns_startDate") get() = this["startDate"] as DataColumn<kotlinx.datetime.LocalDate?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.Campaigns?>.startDate: kotlinx.datetime.LocalDate? @JvmName("NullableCampaigns_startDate") get() = this["startDate"] as kotlinx.datetime.LocalDate?
