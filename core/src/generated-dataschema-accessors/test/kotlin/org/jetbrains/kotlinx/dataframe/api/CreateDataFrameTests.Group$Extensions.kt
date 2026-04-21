@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group>.id: DataColumn<String> @JvmName("Group_id") get() = this["id"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group>.id: String @JvmName("Group_id") get() = this["id"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group?>.id: DataColumn<String?> @JvmName("NullableGroup_id") get() = this["id"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group?>.id: String? @JvmName("NullableGroup_id") get() = this["id"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group>.participants: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>> @JvmName("Group_participants") get() = this["participants"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group>.participants: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person> @JvmName("Group_participants") get() = this["participants"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group?>.participants: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>> @JvmName("NullableGroup_participants") get() = this["participants"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Group?>.participants: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?> @JvmName("NullableGroup_participants") get() = this["participants"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>
