package org.jetbrains.kotlinx.dataframe.plugin

//import io.kotest.matchers.shouldBe
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
//import org.junit.Test

//class SerializationTests {
//
//    @Test
//    fun `equals test`() {
//        val schema1 = PluginDataFrameSchema(
//            listOf(
//                SimpleCol("i", TypeApproximationImpl("kotlin.Int", nullable = false)),
//                SimpleColumnGroup(
//                    "group",
//                    columns = listOf(
//                        SimpleCol("s", TypeApproximationImpl("kotlin.String", false))
//                    )
//                )
//            )
//        )
//
//        val schema2 = PluginDataFrameSchema(
//            listOf(
//                SimpleCol("i", TypeApproximationImpl("kotlin.Int", nullable = false)),
//                SimpleColumnGroup(
//                    "group",
//                    columns = listOf(
//                        SimpleCol("s", TypeApproximationImpl("kotlin.String", false))
//                    )
//                )
//            )
//        )
//
//        schema1 shouldBe schema2
//    }
//
//    @Test
//    fun `serialize PluginDataFrameSchema`() {
//        val schema = PluginDataFrameSchema(
//            listOf(
//                SimpleCol("i", TypeApproximationImpl("kotlin.Int", nullable = false)),
//                SimpleColumnGroup(
//                    "group",
//                    columns = listOf(
//                        SimpleCol("s", TypeApproximationImpl("kotlin.String", false))
//                    )
//                )
//            )
//        )
//
//        val jsonString = pluginJsonFormat.encodeToString(schema)
//        val deserializedSchema = pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(jsonString)
//        deserializedSchema shouldBe schema
//    }
//}
