package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingleWithContext
import org.jetbrains.kotlinx.dataframe.impl.columns.transformWithContext
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.junit.Test

class Experiments : ColumnsSelectionDslTests() {

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
