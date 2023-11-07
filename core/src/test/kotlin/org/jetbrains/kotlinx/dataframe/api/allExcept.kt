package org.jetbrains.kotlinx.dataframe.api

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingleWithContext
import org.jetbrains.kotlinx.dataframe.impl.columns.transformWithContext
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class AllExceptTests : ColumnsSelectionDslTests() {

    @Test
    fun `top-level`() {
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { allExcept { name and city } },
            df.select { allExcept { cols { it.name in listOf("name", "city") } } },
            df.select { allExcept(name, city) },
            df.select { allExcept(name and city) },
            df.select { allExcept("name", "city") },
            df.select { allExcept(Person::name, Person::city) },
            df.select { allExcept(pathOf("name"), pathOf("city")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { allExcept { name } },
            df.select { allExcept { cols { it.name == "name" } } },
            df.select { allExcept(name) },
            df.select { allExcept("name") },
            df.select { allExcept(Person::name) },
            df.select { allExcept(pathOf("name")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `on columnSet`() {
        val cityAccessor = column<String?>("city")
        val nameAccessor = column<String>("name")
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { cols().except { cols { it.name in listOf("name", "city") } } },
            df.select { cols().except { cityAccessor and nameAccessor } },
            df.select { cols().except { city and name } },
            df.select { cols().except(city and name) },
            df.select { cols().except(city, name) },
            df.select { cols().except(cityAccessor, nameAccessor) },
            df.select { cols().except("city", "name") },
            df.select { cols().except(Person::city, Person::name) },
            df.select { cols().except(pathOf("city"), pathOf("name")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { cols() except { cols { it.name == "name" } } },
            df.select { cols() except cols { it.name == "name" } },
            df.select { cols() except nameAccessor },
            df.select { cols() except name },
            df.select { cols() except "name" },
            df.select { cols() except Person::name },
            df.select { cols() except pathOf("name") },
        ).shouldAllBeEqual()
    }

    @Test
    fun `empty group`() {
        // TODO
        df.select {
            all() except name.allCols()
        }.alsoDebug()
    }

    @Test
    fun `relative path`() {
        listOf(
            df.select { name.allColsExcept { lastName } }.alsoDebug(),

//            df.select { name.allColsExcept("name"["lastName"]) }.alsoDebug(),
            df.select { name.allColsExcept { "lastName"() } }.alsoDebug(),
            df.select { name.allColsExcept("lastName") }.alsoDebug(),
        ).shouldAllBeEqual()
    }

    @Test
    fun temp() {
        df.alsoDebug()

        df.select {
            name.allColsExcept("lastName")
        }.alsoDebug()

        df.select {
            name.allColsExcept("name"["lastName"])
        }.alsoDebug()
    }

    @Test
    fun `lower level`() {
//        df.select {
//            name.allColsExcept("name"["lastName"])
//        }.alsoDebug()

        df.select {
//            name.allColsExcept("lastName")
            name.allColsExcept(first { true })
        }.alsoDebug()

        val lastNameAccessor = column<String>("lastName")
        val fullLastNameAccessor = column<String>(pathOf("name", "lastName"))
        listOf(
            df.select { name.firstName },

            df.select { name.allColsExcept { lastName } },
            df.select { name.allColsExcept { cols { it.name == "lastName" } } },
            df.select { name.allColsExcept(name.lastName, name.lastName) },
            df.select { name.allColsExcept(lastNameAccessor, lastNameAccessor) },
            df.select { name.allColsExcept("lastName", "lastName") },
            df.select { name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },

            df.select { name allColsExcept name.lastName },
            df.select { name allColsExcept (name.lastName and name.lastName) },
//            df.select { name allColsExcept (name.lastName and "lastName") },//
            df.select { name.allCols() except name.lastName },
            df.select { name allColsExcept fullLastNameAccessor },
            df.select { name.allCols() except fullLastNameAccessor },

            df.select { name allColsExcept "lastName" },
            df.select { name allColsExcept Name::lastName },
            df.select { name allColsExcept pathOf("lastName") },
        ).shouldAllBeEqual()
    }

    @Test
    fun `ambiguous cases`() {
        @Language("json")
        val json = """
            {
                 "a": {
                     "a": 1,
                     "b": 2
                 }
            }
        """.trimIndent()

        val df = DataFrame.readJsonStr(json).alsoDebug()

        listOf(
            df.select { "a"["b"] }.alsoDebug(),

            df.select { "a".allColsExcept("a") }.alsoDebug(), // ambiguous!
//            df.select { "a".allColsExcept("a"["a"]) }.alsoDebug(),
        ).shouldAllBeEqual()
    }

    @Test
    fun `test with except`() {
//        df.select { name.allCols() except name.firstName }.alsoDebug()

        df.select { !(age and name) }.alsoDebug()
        df.select { allExcept(age and name) }.alsoDebug()
    }

    // TODO (re)move
    @Test
    fun `TEMP experiments`() {
        dfGroup.select {

            @Suppress("UNCHECKED_CAST")
            fun ColumnSet<*>.containingGroups(): ColumnSet<DataRow<*>> = transformWithContext {
                it.mapNotNull {
                    if (it.path.size > 1) {
                        it.path.dropLast()
                            .resolveSingle(this@transformWithContext) as ColumnWithPath<DataRow<*>>?
                    } else {
                        null
                    }
                }
            }

            fun SingleColumn<*>.containingGroup(): SingleColumn<DataRow<*>> =
                asColumnSet().containingGroups().singleOrNullImpl()

            fun SingleColumn<*>.colsInSameColGroup(): ColumnSet<*> = transformSingleWithContext {
                val parent = containingGroup().resolveSingle(this)
                    ?: return@transformSingleWithContext emptyList()
                parent.cols().allColumnsExceptKeepingStructure(listOf(it))
            }

            fun SingleColumn<*>.colGroupNoOthers(): SingleColumn<DataRow<*>> =
                containingGroup().asColumnSet()
                    .except(colsInSameColGroup())
                    .singleOrNullImpl()

            fun SingleColumn<*>.colGroupsNoOthers(): ColumnSet<DataRow<*>> = transformSingleWithContext {
                buildList {
                    var current = it
                    while (true) {
                        val parent = current.colGroupNoOthers()
                            .resolveSingle(this@transformSingleWithContext)
                            ?: break
                        add(parent)
                        current = parent
                    }
                }
            }

            fun SingleColumn<*>.rootColGroupNoOthers(): SingleColumn<DataRow<*>> =
                colGroupsNoOthers().simplify().single()

            fun SingleColumn<*>.colGroups(): ColumnSet<DataRow<*>> = transformSingleWithContext {
                buildList {
                    var path = it.path
                    while (path.size > 1) {
                        path = path.dropLast(1)
                        val parent = path
                            .resolveSingle(this@transformSingleWithContext) as ColumnWithPath<DataRow<*>>?
                        if (parent != null) add(parent)
                    }
                }
            }

            fun SingleColumn<*>.rootColGroup(): SingleColumn<DataRow<*>> =
                colGroups().simplify().single()


//            colsAtAnyDepth { it.name == "secondName" }.single().parentNoSiblings().parentNoSiblings()
//            cols(name) except {
//                name {
//                    firstName {
//                        secondName and thirdName
//                    } and lastName
//                }
//            }

//            name.firstName.firstName.rootColGroup()

            (name.firstName and name.firstName.secondName).simplify()
        }.alsoDebug()
    }
}
