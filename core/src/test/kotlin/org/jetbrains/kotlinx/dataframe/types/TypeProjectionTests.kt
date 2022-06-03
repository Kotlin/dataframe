package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.createTypeUsing
import org.junit.Test
import kotlin.reflect.typeOf

class TypeProjectionTests {
    class TypeInferenceTest1 {
        interface A<T>
        interface X<T> : A<List<T>>

        @Test
        fun test() {
            X::class.createTypeUsing<A<List<Int>>>() shouldBe typeOf<X<Int>>()
            A::class.createTypeUsing<X<Int>>() shouldBe typeOf<A<List<Int>>>()
        }
    }

    class TypeInferenceTest2 {
        interface A<out T>
        interface B<T> : A<A<T>>
        interface C<T> : A<B<T>>
        interface D<T>
        interface X<T : Number, V : Number> : C<T>, D<V>

        @Test
        fun test() {
            X::class.createTypeUsing<A<A<A<Int>>>>() shouldBe typeOf<X<Int, *>>()
            A::class.createTypeUsing<X<Double, Int>?>() shouldBe typeOf<A<B<Double>>?>()
        }
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
        listOf(typeOf<List<Int>>(), typeOf<Set<Double?>>()).commonType() shouldBe typeOf<Collection<Number?>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>>()).commonType() shouldBe typeOf<Collection<Any?>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<*>?>()).commonType() shouldBe typeOf<Collection<Any?>?>()
    }
}
