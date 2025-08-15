package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.createTypeUsing
import org.jetbrains.kotlinx.dataframe.types.TypeProjectionTests.TypeInferenceTest1.Test1A
import org.jetbrains.kotlinx.dataframe.types.TypeProjectionTests.TypeInferenceTest1.Test1X
import org.jetbrains.kotlinx.dataframe.types.TypeProjectionTests.TypeInferenceTest2.B
import org.jetbrains.kotlinx.dataframe.types.TypeProjectionTests.TypeInferenceTest2.Test2A
import org.jetbrains.kotlinx.dataframe.types.TypeProjectionTests.TypeInferenceTest2.Test2X
import org.junit.Test
import kotlin.reflect.typeOf

class TypeProjectionTests {
    class TypeInferenceTest1 {
        interface Test1A<T>

        interface Test1X<T> : Test1A<List<T>>
    }

    @Test
    fun test1() {
        Test1X::class.createTypeUsing<Test1A<List<Int>>>() shouldBe typeOf<Test1X<Int>>()
        Test1A::class.createTypeUsing<Test1X<Int>>() shouldBe typeOf<Test1A<List<Int>>>()
    }

    class TypeInferenceTest2 {
        interface Test2A<out T>

        interface B<T> : Test2A<Test2A<T>>

        interface C<T> : Test2A<B<T>>

        interface D<T>

        interface Test2X<T : Number, V : Number> :
            C<T>,
            D<V>
    }

    @Test
    fun test2() {
        Test2X::class.createTypeUsing<Test2A<Test2A<Test2A<Int>>>>() shouldBe typeOf<Test2X<Int, *>>()
        Test2A::class.createTypeUsing<Test2X<Double, Int>?>() shouldBe typeOf<Test2A<B<Double>>?>()
    }

    @Test
    fun `collection to list projection`() {
        List::class.createTypeUsing<Collection<Int>?>() shouldBe typeOf<List<Int>?>()
        Collection::class.createTypeUsing<List<Int>>() shouldBe typeOf<Collection<Int>>()
    }

    @Test
    fun `column group projections`() {
        ColumnGroup::class.createTypeUsing<ColumnReference<DataRow<Int>>>() shouldBe typeOf<ColumnGroup<Int>>()
        SingleColumn::class.createTypeUsing<ColumnGroupWithParent<Int>>() shouldBe typeOf<SingleColumn<DataRow<Int>>>()
    }

    @Test
    fun `common type tests`() {
        listOf(typeOf<List<Int>>(), typeOf<Set<Double?>>()).commonType() shouldBe typeOf<Collection<out Number?>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>>()).commonType(false) shouldBe typeOf<Collection<out Any?>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>>()).commonType() shouldBe typeOf<Collection<*>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>?>()).commonType(false) shouldBe typeOf<Collection<out Any?>?>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>?>()).commonType() shouldBe typeOf<Collection<*>?>()
    }
}
