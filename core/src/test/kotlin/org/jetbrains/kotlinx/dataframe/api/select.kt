package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class SelectTests : ColumnsSelectionDslTests() {

    //    @Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
    @Test
    fun select() {
        // should not compile:
//         df.select {
//             name.select {
//                 name.firstName
//             }
//         }

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.select { firstName and lastName } },
            df.select { name { firstName and lastName } },
            df.select { "name".select { colsOf<String>() } },
            df.select { "name" { colsOf<String>() } },
            df.select { colGroup("name").select { colsOf<String>() } },
            df.select { colGroup<Name>("name").select { colsOf<String>() } },
            df.select { (colGroup<Name>("name")) { colsOf<String>() } },
            df.select { colGroup<Name>("name")() { colsOf<String>() } },
            df.select { "name".select { "firstName" and "lastName" } },
            df.select { "name" { "firstName" and "lastName" } },
            df.select { pathOf("name").select { "firstName" and "lastName" } },
            df.select { pathOf("name")() { "firstName" and "lastName" } },
            df.select { it["name"].asColumnGroup().select { colsOf<String>() } },
            df.select { it["name"].asColumnGroup()() { colsOf<String>() } },
            df.select { name { colsOf<String>() } },
            df.select { (it["name"].asColumnGroup()) { colsOf<String>() } },

            // TODO: remove warning due to [KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)
            df.select { Person::name.select { firstName and lastName } },
            df.select { NonDataSchemaPerson::name.select { firstName and lastName } },

            // TODO: remove warning due to [KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)
            df.select { Person::name { firstName and lastName } },
            df.select { NonDataSchemaPerson::name { firstName and lastName } },

            df.select { "name"<DataRow<Name>>().select { colsOf<String>() } },
            df.select { "name"<DataRow<Name>>()() { colsOf<String>() } },
            df.select { colGroup("name").select { colsOf<String>() } },
            df.select { colGroup("name")() { colsOf<String>() } },
        ).shouldAllBeEqual()
    }
}
