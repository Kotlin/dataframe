package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
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
import org.jetbrains.kotlinx.dataframe.samples.api.thirdName
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class AllExceptTests : ColumnsSelectionDslTests() {

    @Test
    fun `top-level`() {
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { allExcept { name and city } },
            df.select { allExcept(name and city) }, // legacy, but does no harm, so supported
            df.select { allExcept { cols { it.name in listOf("name", "city") } } },
            df.select { allExcept("name", "city") },
            df.select { allExcept(Person::name, Person::city) },
            df.select { allExcept(pathOf("name"), pathOf("city")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { allExcept { name } },
            df.select { allExcept(name) }, // legacy, but does no harm, so supported
            df.select { allExcept { cols { it.name == "name" } } },
            df.select { allExcept("name") },
            df.select { allExcept(Person::name) },
            df.select { allExcept(pathOf("name")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { all() },

            df.select { allExcept { none() } },
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

        listOf(
            df.select { all() },

            df.select { all() except { none() } },
            df.select { all() except none() },
        ).shouldAllBeEqual()

        // might not work as expected, use colsAtAnyDepth instead
        df.select { name.allCols() except { cols { "last" in it.name } } }
        df.select { name.allCols() except cols { "last" in it.name } }
        // or, you know
        df.select { name.cols { "last" !in it.name } }

        listOf(
            df.select { name.firstName },

            df.select { name.allCols() except { colsAtAnyDepth { "last" in it.name } } },
            df.select { name.allCols() except colsAtAnyDepth { "last" in it.name } },
            df.select { name.allCols() except { name.lastName } },
            df.select { name.allCols() except name.lastName },
            df.select { name.allCols() except { colGroup("name").col("lastName") } },
            df.select { name.allCols() except colGroup("name").col("lastName") },
            df.select { name.allCols() except { "name"["lastName"] } },
            df.select { name.allCols() except "name"["lastName"] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.allCols() },

            df.select { name.allCols() except { none() } },
            df.select { name.allCols() except none() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `on lower level`() {
        val lastNameAccessor = column<String>("lastName")
        listOf(
            df.select { name.firstName },

            df.select { name.allColsExcept { lastName } },
            df.select { name.allColsExcept { lastNameAccessor } },
//            df.select { name.allColsExcept(name.lastName) }, // blocked
//            df.select { name.allColsExcept(lastNameAccessor) }, // blocked
            df.select { name.allColsExcept("lastName") },
            df.select { name.allColsExcept(Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName")) },
//            df.select { name.allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { name.allColsExcept { cols { "last" in it.name } } },

            df.select { "name".allColsExcept { lastNameAccessor } },
//            df.select { "name".allColsExcept(name.lastName) }, // blocked
//            df.select { "name".allColsExcept(lastNameAccessor) }, // blocked
            df.select { "name".allColsExcept("lastName") },
            df.select { "name".allColsExcept(Name::lastName) },
            df.select { "name".allColsExcept(pathOf("lastName")) },
//            df.select { "name".allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { "name".allColsExcept { cols { "last" in it.name } } },

            df.select { Person::name.allColsExcept { lastNameAccessor } },
//            df.select { Person::name.allColsExcept(name.lastName) }, // blocked
//            df.select { Person::name.allColsExcept(lastNameAccessor) }, // blocked
            df.select { Person::name.allColsExcept("lastName") },
            df.select { Person::name.allColsExcept(Name::lastName) },
            df.select { Person::name.allColsExcept(pathOf("lastName")) },
//            df.select { Person::name.allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { Person::name.allColsExcept { cols { "last" in it.name } } },

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
//            df.select { name.allColsExcept { name.lastName } }.alsoDebug(),

//            df.select { name.allColsExcept("name"["lastName"]) }.alsoDebug(),
            df.select { name.allColsExcept { "lastName"() } }.alsoDebug(),
            df.select { name.allColsExcept("lastName") }.alsoDebug(),
        ).shouldAllBeEqual()
    }

    @Test
    fun `should work`() {
        listOf(
            dfGroup.remove { name.firstName.secondName }.select { name.allCols() }.alsoDebug(),

            dfGroup.select {
                name.allColsExcept("firstName"["secondName"])
            },
            dfGroup.select {
                name.allColsExcept { firstName.secondName and firstName.secondName }
            },
            dfGroup.select {
                name.allColsExcept { colGroup("firstName").col("secondName") }
            },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.remove { name.firstName.secondName }.select { name.firstName.allCols() }.alsoDebug(),

            dfGroup.select {
                name.firstName.allColsExcept("secondName")
            },
            dfGroup.select {
                name.firstName.allColsExcept { secondName }
            },
            dfGroup.select {
                name.firstName.allColsExcept(pathOf("secondName"))
            },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName { secondName and thirdName } },
            dfGroup.select { name { firstName.allColsExcept("firstName") } }.alsoDebug(),
            dfGroup.select { name { firstName.allColsExcept(pathOf("firstName")) } }.alsoDebug(),
            dfGroup.select { (name.allColsExcept("firstName"["firstName"])).first().asColumnGroup().allCols() },
            dfGroup.remove { name.firstName.firstName }.select { name.firstName.allCols() },
        ).shouldAllBeEqual()
    }

    @Test
    fun temp() {
//        df.alsoDebug()
//        df.select {
//            name allColsExcept "lastName"
//        }.alsoDebug()
//

//        dfGroup.select {
//            name {
//                firstName allColsExcept firstName.firstName
//            }
//        }.alsoDebug()

//        df.select {
//            name.allColsExcept("firstName")
//            allExcept(name.firstName)
//
//            name.allColsExcept { name.firstName }
//            name.allColsExcept { column<String>("firstName") }
//        }
//
//        dfGroup.select { name { firstName.allColsExcept { firstName.firstName } } }.alsoDebug() // should work
//        dfGroup.select { name { firstName.allColsExcept { firstName } } }.alsoDebug() // should fail

        shouldThrow<IllegalArgumentException> {
            dfGroup.select {
                name.firstName.allColsExcept("firstName"["secondName"])
            }
        }

        shouldThrow<IllegalArgumentException> {
            dfGroup.select {
                name.firstName.allColsExcept(pathOf("name", "firstName", "secondName"))
            }
        }


    }

    @Test
    fun `lower level`() {
//        df.select {
//            name.allColsExcept("name"["lastName"])
//        }.alsoDebug()

        df.select {
            name.allColsExcept { first() }
        }.alsoDebug()

        val lastNameAccessor = column<String>("lastName")
        val fullLastNameAccessor = column<String>(pathOf("name", "lastName"))
        listOf(
            df.select { name.firstName },

            df.select { name.allColsExcept { lastName } },
            df.select { name.allColsExcept { cols { it.name == "lastName" } } },
            df.select { name.allColsExcept { lastName and lastName } },
            df.select { name.allColsExcept { lastNameAccessor and lastNameAccessor } },
            df.select { name.allColsExcept("lastName", "lastName") },
            df.select { name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },

            df.select { name.allColsExcept { lastName } },
            df.select { Person::name.allColsExcept { lastName } },
            df.select { NonDataSchemaPerson::name.allColsExcept { lastName } },
//            df.select { name.allColsExcept(name.lastName and name.lastName) },
//            df.select { name allColsExcept (name.lastName and "lastName") },//
            df.select { name.allCols() except name.lastName },
            df.select { name.allCols() except { name.lastName } },
//            df.select { name.allColsExcept(fullLastNameAccessor) },
            df.select { name.allCols() except fullLastNameAccessor },

            df.select { name.allColsExcept("lastName") },
            df.select { name.allColsExcept(Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `2 levels deep`() {
        val secondNameAccessor = column<String?>("secondName")
        val thirdNameAccessor = column<String?>("thirdName")

        listOf(
            dfGroup.select { name.firstName.firstName },

            dfGroup.select { name.firstName.allColsExcept { secondName and thirdName } },
            dfGroup.select { name.firstName.allColsExcept { secondNameAccessor and thirdNameAccessor } },
            dfGroup.select { name.firstName.allColsExcept { cols { it.name in listOf("secondName", "thirdName") } } },
//            dfGroup.select { name.firstName.allColsExcept(name.firstName.secondName and name.firstName.thirdName) },
//            dfGroup.select { name.firstName.allColsExcept(name.firstName.secondName and name.firstName.thirdName) },
//            dfGroup.select { name.firstName.allColsExcept(name.firstName.secondName and thirdNameAccessor) },
//            dfGroup.select { name.firstName.allColsExcept(secondNameAccessor and thirdNameAccessor) },
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

//        df.select { !(age and name) }.alsoDebug()
        df.select { allExcept { age and name } }.alsoDebug()
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
