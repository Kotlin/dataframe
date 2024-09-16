package org.jetbrains.kotlinx.dataframe.impl.columns

import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.booleans.BooleanListIterator
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import it.unimi.dsi.fastutil.bytes.ByteListIterator
import it.unimi.dsi.fastutil.chars.CharArrayList
import it.unimi.dsi.fastutil.chars.CharListIterator
import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.doubles.DoubleListIterator
import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.floats.FloatListIterator
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntListIterator
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongListIterator
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import it.unimi.dsi.fastutil.shorts.ShortListIterator
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.BOOLEAN
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.BYTE
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.CHAR
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.DOUBLE
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.FLOAT
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.INT
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.LONG
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList.State.SHORT
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import org.jetbrains.kotlinx.dataframe.impl.columns.BOOLEAN as BOOLEAN_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.BYTE as BYTE_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.CHAR as CHAR_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.DOUBLE as DOUBLE_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.FLOAT as FLOAT_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.INT as INT_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.LONG as LONG_TYPE
import org.jetbrains.kotlinx.dataframe.impl.columns.SHORT as SHORT_TYPE

/**
 * Universal wrapper around [BooleanArrayList], [ByteArrayList], [CharArrayList],
 * [ShortArrayList], [IntArrayList], [LongArrayList], [FloatArrayList], and [DoubleArrayList].
 *
 * While boxing can occur when working with the elements, the list itself is unboxed.
 */
internal class PrimitiveArrayList<T : Any> private constructor(arrayList: List<T>?, state: State?) :
    MutableList<T> {

        companion object {
            fun <T : Any> forTypeOrNull(kType: KType, initCapacity: Int = 0): PrimitiveArrayList<T>? {
                return when (kType) {
                    BOOLEAN_TYPE -> PrimitiveArrayList<Boolean>(BOOLEAN, initCapacity)
                    BYTE_TYPE -> PrimitiveArrayList<Byte>(BYTE, initCapacity)
                    CHAR_TYPE -> PrimitiveArrayList<Char>(CHAR, initCapacity)
                    SHORT_TYPE -> PrimitiveArrayList<Short>(SHORT, initCapacity)
                    INT_TYPE -> PrimitiveArrayList<Int>(INT, initCapacity)
                    LONG_TYPE -> PrimitiveArrayList<Long>(LONG, initCapacity)
                    FLOAT_TYPE -> PrimitiveArrayList<Float>(FLOAT, initCapacity)
                    DOUBLE_TYPE -> PrimitiveArrayList<Double>(DOUBLE, initCapacity)
                    else -> return null
                } as PrimitiveArrayList<T>
            }

            inline fun <reified T : Any> forTypeOrNull(initCapacity: Int = 0): PrimitiveArrayList<T>? =
                forTypeOrNull(typeOf<T>(), initCapacity)

            fun <T : Any> forType(kType: KType, initCapacity: Int = 0): PrimitiveArrayList<T> =
                forTypeOrNull(kType, initCapacity) ?: throw IllegalArgumentException("Unsupported type: $kType")

            inline fun <reified T : Any> forType(initCapacity: Int = 0): PrimitiveArrayList<T> =
                forType(typeOf<T>(), initCapacity)

            operator fun invoke(booleans: BooleanArrayList): PrimitiveArrayList<Boolean> =
                PrimitiveArrayList(booleans, BOOLEAN)

            operator fun invoke(bytes: ByteArrayList): PrimitiveArrayList<Byte> = PrimitiveArrayList(bytes, BYTE)

            operator fun invoke(chars: CharArrayList): PrimitiveArrayList<Char> = PrimitiveArrayList(chars, CHAR)

            operator fun invoke(shorts: ShortArrayList): PrimitiveArrayList<Short> = PrimitiveArrayList(shorts, SHORT)

            operator fun invoke(ints: IntArrayList): PrimitiveArrayList<Int> = PrimitiveArrayList(ints, INT)

            operator fun invoke(longs: LongArrayList): PrimitiveArrayList<Long> = PrimitiveArrayList(longs, LONG)

            operator fun invoke(floats: FloatArrayList): PrimitiveArrayList<Float> = PrimitiveArrayList(floats, FLOAT)

            operator fun invoke(doubles: DoubleArrayList): PrimitiveArrayList<Double> =
                PrimitiveArrayList(doubles, DOUBLE)

            operator fun invoke(booleans: BooleanArray): PrimitiveArrayList<Boolean> =
                PrimitiveArrayList(BooleanArrayList(booleans), BOOLEAN)

            operator fun invoke(bytes: ByteArray): PrimitiveArrayList<Byte> =
                PrimitiveArrayList(ByteArrayList(bytes), BYTE)

            operator fun invoke(chars: CharArray): PrimitiveArrayList<Char> =
                PrimitiveArrayList(CharArrayList(chars), CHAR)

            operator fun invoke(shorts: ShortArray): PrimitiveArrayList<Short> =
                PrimitiveArrayList(ShortArrayList(shorts), SHORT)

            operator fun invoke(ints: IntArray): PrimitiveArrayList<Int> = PrimitiveArrayList(IntArrayList(ints), INT)

            operator fun invoke(longs: LongArray): PrimitiveArrayList<Long> =
                PrimitiveArrayList(LongArrayList(longs), LONG)

            operator fun invoke(floats: FloatArray): PrimitiveArrayList<Float> =
                PrimitiveArrayList(FloatArrayList(floats), FLOAT)

            operator fun invoke(doubles: DoubleArray): PrimitiveArrayList<Double> =
                PrimitiveArrayList(DoubleArrayList(doubles), DOUBLE)

            inline operator fun <reified T : Any> invoke(initCapacity: Int = 0): PrimitiveArrayList<T> =
                forTypeOrNull(initCapacity) ?: PrimitiveArrayList<T>(null, null).also { it.initCapacity = initCapacity }
        }

        var initCapacity = arrayList?.size ?: 0

        constructor(state: State?) : this(
            arrayList = when (state) {
                BOOLEAN -> BooleanArrayList()
                BYTE -> ByteArrayList()
                CHAR -> CharArrayList()
                SHORT -> ShortArrayList()
                INT -> IntArrayList()
                LONG -> LongArrayList()
                FLOAT -> FloatArrayList()
                DOUBLE -> DoubleArrayList()
                null -> null
            } as List<T>?,
            state = state,
        )

        constructor(state: State?, initCapacity: Int) : this(
            arrayList = when (state) {
                BOOLEAN -> BooleanArrayList(initCapacity)
                BYTE -> ByteArrayList(initCapacity)
                CHAR -> CharArrayList(initCapacity)
                SHORT -> ShortArrayList(initCapacity)
                INT -> IntArrayList(initCapacity)
                LONG -> LongArrayList(initCapacity)
                FLOAT -> FloatArrayList(initCapacity)
                DOUBLE -> DoubleArrayList(initCapacity)
                null -> null
            } as List<T>?,
            state = state,
        )

        enum class State {
            BOOLEAN,
            BYTE,
            CHAR,
            SHORT,
            INT,
            LONG,
            FLOAT,
            DOUBLE,
        }

        internal var arrayList: List<T>? = arrayList
            private set

        internal var state = state
            private set

        internal fun initializeArrayList(state: State) {
            try {
                arrayList = when (state) {
                    BOOLEAN -> BooleanArrayList(initCapacity)
                    BYTE -> ByteArrayList(initCapacity)
                    CHAR -> CharArrayList(initCapacity)
                    SHORT -> ShortArrayList(initCapacity)
                    INT -> IntArrayList(initCapacity)
                    LONG -> LongArrayList(initCapacity)
                    FLOAT -> FloatArrayList(initCapacity)
                    DOUBLE -> DoubleArrayList(initCapacity)
                } as List<T>
            } catch (e: Error) {
                throw IllegalStateException("Failed to initialize $state ArrayList of capacity $initCapacity", e)
            }
            this.state = state
        }

        override fun listIterator(): MutableListIterator<T> = listIterator(0)

        override fun listIterator(index: Int): MutableListIterator<T> =
            object : MutableListIterator<T> {
                private var it = arrayList?.listIterator(index)

                override fun add(element: T) {
                    when (state) {
                        BOOLEAN -> (it as BooleanListIterator).add(element as Boolean)

                        BYTE -> (it as ByteListIterator).add(element as Byte)

                        CHAR -> (it as CharListIterator).add(element as Char)

                        SHORT -> (it as ShortListIterator).add(element as Short)

                        INT -> (it as IntListIterator).add(element as Int)

                        LONG -> (it as LongListIterator).add(element as Long)

                        FLOAT -> (it as FloatListIterator).add(element as Float)

                        DOUBLE -> (it as DoubleListIterator).add(element as Double)

                        null -> {
                            when (element) {
                                is Boolean -> initializeArrayList(BOOLEAN)
                                is Byte -> initializeArrayList(BYTE)
                                is Char -> initializeArrayList(CHAR)
                                is Short -> initializeArrayList(SHORT)
                                is Int -> initializeArrayList(INT)
                                is Long -> initializeArrayList(LONG)
                                is Float -> initializeArrayList(FLOAT)
                                is Double -> initializeArrayList(DOUBLE)
                                else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
                            }
                            it = arrayList!!.listIterator(index)
                            add(element)
                        }
                    }
                }

                override fun hasNext(): Boolean = it?.hasNext() ?: false

                override fun hasPrevious(): Boolean = it?.hasPrevious() ?: false

                override fun next(): T = it?.next() ?: error("No next element")

                override fun nextIndex(): Int = it?.nextIndex() ?: -1

                override fun previous(): T = it?.previous() ?: error("No previous element")

                override fun previousIndex(): Int = it?.previousIndex() ?: -1

                @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "KotlinConstantConditions")
                override fun remove() {
                    when (val it = it) {
                        is MutableListIterator<*> -> it.remove()
                        is java.util.Iterator<*> -> it.remove()
                        null -> error("No element to remove")
                        else -> throw UnsupportedOperationException()
                    }
                }

                override fun set(element: T) {
                    when (state) {
                        BOOLEAN -> (it as BooleanListIterator).set(element as Boolean)

                        BYTE -> (it as ByteListIterator).set(element as Byte)

                        CHAR -> (it as CharListIterator).set(element as Char)

                        SHORT -> (it as ShortListIterator).set(element as Short)

                        INT -> (it as IntListIterator).set(element as Int)

                        LONG -> (it as LongListIterator).set(element as Long)

                        FLOAT -> (it as FloatListIterator).set(element as Float)

                        DOUBLE -> (it as DoubleListIterator).set(element as Double)

                        null -> {
                            when (element) {
                                is Boolean -> initializeArrayList(BOOLEAN)
                                is Byte -> initializeArrayList(BYTE)
                                is Char -> initializeArrayList(CHAR)
                                is Short -> initializeArrayList(SHORT)
                                is Int -> initializeArrayList(INT)
                                is Long -> initializeArrayList(LONG)
                                is Float -> initializeArrayList(FLOAT)
                                is Double -> initializeArrayList(DOUBLE)
                                else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
                            }
                            it = arrayList!!.listIterator(index)
                            set(element)
                        }
                    }
                }
            }

        override fun iterator(): MutableIterator<T> = listIterator()

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun lastIndexOf(element: T): Int =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).lastIndexOf(element as Boolean)
                BYTE -> (arrayList as ByteArrayList).lastIndexOf(element as Byte)
                CHAR -> (arrayList as CharArrayList).lastIndexOf(element as Char)
                SHORT -> (arrayList as ShortArrayList).lastIndexOf(element as Short)
                INT -> (arrayList as IntArrayList).lastIndexOf(element as Int)
                LONG -> (arrayList as LongArrayList).lastIndexOf(element as Long)
                FLOAT -> (arrayList as FloatArrayList).lastIndexOf(element as Float)
                DOUBLE -> (arrayList as DoubleArrayList).lastIndexOf(element as Double)
                null -> error("List is not initialized")
            }

        fun lastIndexOf(element: Boolean) =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Byte) =
            when (state) {
                BYTE -> (arrayList as ByteArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Char) =
            when (state) {
                CHAR -> (arrayList as CharArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Short) =
            when (state) {
                SHORT -> (arrayList as ShortArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Int) =
            when (state) {
                INT -> (arrayList as IntArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Long) =
            when (state) {
                LONG -> (arrayList as LongArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Float) =
            when (state) {
                FLOAT -> (arrayList as FloatArrayList).lastIndexOf(element)
                else -> -1
            }

        fun lastIndexOf(element: Double) =
            when (state) {
                DOUBLE -> (arrayList as DoubleArrayList).lastIndexOf(element)
                else -> -1
            }

        fun addBoxed(element: T): Boolean =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).add(element as Boolean)

                BYTE -> (arrayList as ByteArrayList).add(element as Byte)

                CHAR -> (arrayList as CharArrayList).add(element as Char)

                SHORT -> (arrayList as ShortArrayList).add(element as Short)

                INT -> (arrayList as IntArrayList).add(element as Int)

                LONG -> (arrayList as LongArrayList).add(element as Long)

                FLOAT -> (arrayList as FloatArrayList).add(element as Float)

                DOUBLE -> (arrayList as DoubleArrayList).add(element as Double)

                null -> {
                    when (element) {
                        is Boolean -> (this as PrimitiveArrayList<Boolean>).add(element)
                        is Byte -> (this as PrimitiveArrayList<Byte>).add(element)
                        is Char -> (this as PrimitiveArrayList<Char>).add(element)
                        is Short -> (this as PrimitiveArrayList<Short>).add(element)
                        is Int -> (this as PrimitiveArrayList<Int>).add(element)
                        is Long -> (this as PrimitiveArrayList<Long>).add(element)
                        is Float -> (this as PrimitiveArrayList<Float>).add(element)
                        is Double -> (this as PrimitiveArrayList<Double>).add(element)
                        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
                    }
                }
            }

        /** Prefer the primitive overloads! */
        @Suppress("UNCHECKED_CAST")
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun add(element: T): Boolean = addBoxed(element)

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun add(index: Int, element: T) {
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).add(index, element as Boolean)

                BYTE -> (arrayList as ByteArrayList).add(index, element as Byte)

                CHAR -> (arrayList as CharArrayList).add(index, element as Char)

                SHORT -> (arrayList as ShortArrayList).add(index, element as Short)

                INT -> (arrayList as IntArrayList).add(index, element as Int)

                LONG -> (arrayList as LongArrayList).add(index, element as Long)

                FLOAT -> (arrayList as FloatArrayList).add(index, element as Float)

                DOUBLE -> (arrayList as DoubleArrayList).add(index, element as Double)

                null -> {
                    when (element) {
                        is Boolean -> (this as PrimitiveArrayList<Boolean>).add(index, element)
                        is Byte -> (this as PrimitiveArrayList<Byte>).add(index, element)
                        is Char -> (this as PrimitiveArrayList<Char>).add(index, element)
                        is Short -> (this as PrimitiveArrayList<Short>).add(index, element)
                        is Int -> (this as PrimitiveArrayList<Int>).add(index, element)
                        is Long -> (this as PrimitiveArrayList<Long>).add(index, element)
                        is Float -> (this as PrimitiveArrayList<Float>).add(index, element)
                        is Double -> (this as PrimitiveArrayList<Double>).add(index, element)
                        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(index: Int, elements: Collection<T>): Boolean =
            if (elements.isEmpty()) {
                false
            } else {
                when (state) {
                    BOOLEAN -> (arrayList as BooleanArrayList).addAll(index, elements as Collection<Boolean>)

                    BYTE -> (arrayList as ByteArrayList).addAll(index, elements as Collection<Byte>)

                    CHAR -> (arrayList as CharArrayList).addAll(index, elements as Collection<Char>)

                    SHORT -> (arrayList as ShortArrayList).addAll(index, elements as Collection<Short>)

                    INT -> (arrayList as IntArrayList).addAll(index, elements as Collection<Int>)

                    LONG -> (arrayList as LongArrayList).addAll(index, elements as Collection<Long>)

                    FLOAT -> (arrayList as FloatArrayList).addAll(index, elements as Collection<Float>)

                    DOUBLE -> (arrayList as DoubleArrayList).addAll(index, elements as Collection<Double>)

                    null -> {
                        when (elements.first()) {
                            is Boolean -> initializeArrayList(BOOLEAN)

                            is Byte -> initializeArrayList(BYTE)

                            is Char -> initializeArrayList(CHAR)

                            is Short -> initializeArrayList(SHORT)

                            is Int -> initializeArrayList(INT)

                            is Long -> initializeArrayList(LONG)

                            is Float -> initializeArrayList(FLOAT)

                            is Double -> initializeArrayList(DOUBLE)

                            else -> throw IllegalArgumentException(
                                "Unsupported element type: ${elements.first()::class}",
                            )
                        }
                        addAll(index, elements)
                    }
                }
            }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(elements: Collection<T>): Boolean =
            if (elements.isEmpty()) {
                false
            } else {
                when (state) {
                    BOOLEAN -> (arrayList as BooleanArrayList).addAll(elements as Collection<Boolean>)

                    BYTE -> (arrayList as ByteArrayList).addAll(elements as Collection<Byte>)

                    CHAR -> (arrayList as CharArrayList).addAll(elements as Collection<Char>)

                    SHORT -> (arrayList as ShortArrayList).addAll(elements as Collection<Short>)

                    INT -> (arrayList as IntArrayList).addAll(elements as Collection<Int>)

                    LONG -> (arrayList as LongArrayList).addAll(elements as Collection<Long>)

                    FLOAT -> (arrayList as FloatArrayList).addAll(elements as Collection<Float>)

                    DOUBLE -> (arrayList as DoubleArrayList).addAll(elements as Collection<Double>)

                    null -> {
                        when (elements.first()) {
                            is Boolean -> initializeArrayList(BOOLEAN)

                            is Byte -> initializeArrayList(BYTE)

                            is Char -> initializeArrayList(CHAR)

                            is Short -> initializeArrayList(SHORT)

                            is Int -> initializeArrayList(INT)

                            is Long -> initializeArrayList(LONG)

                            is Float -> initializeArrayList(FLOAT)

                            is Double -> initializeArrayList(DOUBLE)

                            else -> throw IllegalArgumentException(
                                "Unsupported element type: ${elements.first()::class}",
                            )
                        }
                        addAll(elements)
                        true
                    }
                }
            }

        /** Prefer the primitive overloads! */
        fun canAdd(element: Any): Boolean =
            when (state) {
                BOOLEAN -> element is Boolean

                BYTE -> element is Byte

                CHAR -> element is Char

                SHORT -> element is Short

                INT -> element is Int

                LONG -> element is Long

                FLOAT -> element is Float

                DOUBLE -> element is Double

                null ->
                    element is Boolean ||
                        element is Byte ||
                        element is Char ||
                        element is Short ||
                        element is Int ||
                        element is Long ||
                        element is Float ||
                        element is Double
            }

        fun canAdd(element: Boolean) = state == BOOLEAN || state == null

        fun canAdd(element: Byte) = state == BYTE || state == null

        fun canAdd(element: Char) = state == CHAR || state == null

        fun canAdd(element: Short) = state == SHORT || state == null

        fun canAdd(element: Int) = state == INT || state == null

        fun canAdd(element: Long) = state == LONG || state == null

        fun canAdd(element: Float) = state == FLOAT || state == null

        fun canAdd(element: Double) = state == DOUBLE || state == null

        override val size: Int
            get() = arrayList?.size ?: 0

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun clear() = (arrayList as java.util.Collection<*>).clear()

        /** Prefer the primitive overloads! */
        @Suppress("UNCHECKED_CAST")
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun get(index: Int): T = getBoxed(index)

        fun getBoxed(index: Int): T =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).getBoolean(index)
                BYTE -> (arrayList as ByteArrayList).getByte(index)
                CHAR -> (arrayList as CharArrayList).getChar(index)
                SHORT -> (arrayList as ShortArrayList).getShort(index)
                INT -> (arrayList as IntArrayList).getInt(index)
                LONG -> (arrayList as LongArrayList).getLong(index)
                FLOAT -> (arrayList as FloatArrayList).getFloat(index)
                DOUBLE -> (arrayList as DoubleArrayList).getDouble(index)
                else -> throw IllegalStateException("Index: $index, Size: $size")
            } as T

        override fun isEmpty(): Boolean = arrayList?.isEmpty() ?: true

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun indexOf(element: T): Int = indexOfBoxed(element)

        fun indexOfBoxed(element: T): Int =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).indexOf(element as Boolean)
                BYTE -> (arrayList as ByteArrayList).indexOf(element as Byte)
                CHAR -> (arrayList as CharArrayList).indexOf(element as Char)
                SHORT -> (arrayList as ShortArrayList).indexOf(element as Short)
                INT -> (arrayList as IntArrayList).indexOf(element as Int)
                LONG -> (arrayList as LongArrayList).indexOf(element as Long)
                FLOAT -> (arrayList as FloatArrayList).indexOf(element as Float)
                DOUBLE -> (arrayList as DoubleArrayList).indexOf(element as Double)
                null -> -1
            }

        @Suppress("UNCHECKED_CAST")
        override fun containsAll(elements: Collection<T>): Boolean =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).containsAll(elements as Collection<Boolean>)
                BYTE -> (arrayList as ByteArrayList).containsAll(elements as Collection<Byte>)
                CHAR -> (arrayList as CharArrayList).containsAll(elements as Collection<Char>)
                SHORT -> (arrayList as ShortArrayList).containsAll(elements as Collection<Short>)
                INT -> (arrayList as IntArrayList).containsAll(elements as Collection<Int>)
                LONG -> (arrayList as LongArrayList).containsAll(elements as Collection<Long>)
                FLOAT -> (arrayList as FloatArrayList).containsAll(elements as Collection<Float>)
                DOUBLE -> (arrayList as DoubleArrayList).containsAll(elements as Collection<Double>)
                null -> elements.isEmpty()
            }

        fun containsBoxed(element: T): Boolean =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).contains(element as Boolean)
                BYTE -> (arrayList as ByteArrayList).contains(element as Byte)
                CHAR -> (arrayList as CharArrayList).contains(element as Char)
                SHORT -> (arrayList as ShortArrayList).contains(element as Short)
                INT -> (arrayList as IntArrayList).contains(element as Int)
                LONG -> (arrayList as LongArrayList).contains(element as Long)
                FLOAT -> (arrayList as FloatArrayList).contains(element as Float)
                DOUBLE -> (arrayList as DoubleArrayList).contains(element as Double)
                null -> false
            }

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun contains(element: T): Boolean = containsBoxed(element)

        fun removeAtBoxed(index: Int): T =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).removeBoolean(index)
                BYTE -> (arrayList as ByteArrayList).removeByte(index)
                CHAR -> (arrayList as CharArrayList).removeChar(index)
                SHORT -> (arrayList as ShortArrayList).removeShort(index)
                INT -> (arrayList as IntArrayList).removeInt(index)
                LONG -> (arrayList as LongArrayList).removeLong(index)
                FLOAT -> (arrayList as FloatArrayList).removeFloat(index)
                DOUBLE -> (arrayList as DoubleArrayList).removeDouble(index)
                else -> throw IllegalStateException("Index: $index, Size: $size")
            } as T

        /** Prefer the primitive overloads! */
        @Suppress("UNCHECKED_CAST")
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun removeAt(index: Int): T = removeAtBoxed(index)

        @Suppress("UNCHECKED_CAST")
        override fun subList(fromIndex: Int, toIndex: Int): PrimitiveArrayList<T> =
            when (state) {
                BOOLEAN -> BooleanArrayList(
                    (arrayList as BooleanArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                BYTE -> ByteArrayList(
                    (arrayList as ByteArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                CHAR -> CharArrayList(
                    (arrayList as CharArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                SHORT -> ShortArrayList(
                    (arrayList as ShortArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                INT -> IntArrayList(
                    (arrayList as IntArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                LONG -> LongArrayList(
                    (arrayList as LongArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                FLOAT -> FloatArrayList(
                    (arrayList as FloatArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                DOUBLE -> DoubleArrayList(
                    (arrayList as DoubleArrayList).subList(fromIndex, toIndex),
                ).asPrimitiveArrayList()

                null -> error("List is not initialized")
            } as PrimitiveArrayList<T>

        fun setBoxed(index: Int, element: T): T =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).set(index, element as Boolean)

                BYTE -> (arrayList as ByteArrayList).set(index, element as Byte)

                CHAR -> (arrayList as CharArrayList).set(index, element as Char)

                SHORT -> (arrayList as ShortArrayList).set(index, element as Short)

                INT -> (arrayList as IntArrayList).set(index, element as Int)

                LONG -> (arrayList as LongArrayList).set(index, element as Long)

                FLOAT -> (arrayList as FloatArrayList).set(index, element as Float)

                DOUBLE -> (arrayList as DoubleArrayList).set(index, element as Double)

                null -> {
                    when (element) {
                        is Boolean -> (this as PrimitiveArrayList<Boolean>).set(index, element)
                        is Byte -> (this as PrimitiveArrayList<Byte>).set(index, element)
                        is Char -> (this as PrimitiveArrayList<Char>).set(index, element)
                        is Short -> (this as PrimitiveArrayList<Short>).set(index, element)
                        is Int -> (this as PrimitiveArrayList<Int>).set(index, element)
                        is Long -> (this as PrimitiveArrayList<Long>).set(index, element)
                        is Float -> (this as PrimitiveArrayList<Float>).set(index, element)
                        is Double -> (this as PrimitiveArrayList<Double>).set(index, element)
                        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
                    }
                }
            } as T

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        @Suppress("UNCHECKED_CAST")
        override fun set(index: Int, element: T): T = setBoxed(index, element)

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun retainAll(elements: Collection<T>): Boolean =
            (arrayList as java.util.Collection<*>?)?.retainAll(elements) ?: false

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun removeAll(elements: Collection<T>): Boolean =
            (arrayList as java.util.Collection<*>?)?.removeAll(elements) ?: false

        fun removeBoxed(element: T): Boolean =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).rem(element as Boolean)
                BYTE -> (arrayList as ByteArrayList).rem(element as Byte)
                CHAR -> (arrayList as CharArrayList).rem(element as Char)
                SHORT -> (arrayList as ShortArrayList).rem(element as Short)
                INT -> (arrayList as IntArrayList).rem(element as Int)
                LONG -> (arrayList as LongArrayList).rem(element as Long)
                FLOAT -> (arrayList as FloatArrayList).rem(element as Float)
                DOUBLE -> (arrayList as DoubleArrayList).rem(element as Double)
                null -> false
            }

        /** Prefer the primitive overloads! */
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun remove(element: T): Boolean = removeBoxed(element)
    }

internal fun BooleanArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Boolean> = PrimitiveArrayList(this)

internal fun ByteArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Byte> = PrimitiveArrayList(this)

internal fun CharArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Char> = PrimitiveArrayList(this)

internal fun ShortArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Short> = PrimitiveArrayList(this)

internal fun IntArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Int> = PrimitiveArrayList(this)

internal fun LongArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Long> = PrimitiveArrayList(this)

internal fun FloatArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Float> = PrimitiveArrayList(this)

internal fun DoubleArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Double> = PrimitiveArrayList(this)

internal fun PrimitiveArrayList<Boolean>.getArrayList(): BooleanArrayList = arrayList as BooleanArrayList

internal fun PrimitiveArrayList<Byte>.getArrayList(): ByteArrayList = arrayList as ByteArrayList

internal fun PrimitiveArrayList<Char>.getArrayList(): CharArrayList = arrayList as CharArrayList

internal fun PrimitiveArrayList<Short>.getArrayList(): ShortArrayList = arrayList as ShortArrayList

internal fun PrimitiveArrayList<Int>.getArrayList(): IntArrayList = arrayList as IntArrayList

internal fun PrimitiveArrayList<Long>.getArrayList(): LongArrayList = arrayList as LongArrayList

internal fun PrimitiveArrayList<Float>.getArrayList(): FloatArrayList = arrayList as FloatArrayList

internal fun PrimitiveArrayList<Double>.getArrayList(): DoubleArrayList = arrayList as DoubleArrayList

// region get

internal operator fun PrimitiveArrayList<Boolean>.get(index: Int): Boolean =
    (arrayList as BooleanArrayList).getBoolean(index)

internal operator fun PrimitiveArrayList<Byte>.get(index: Int): Byte = (arrayList as ByteArrayList).getByte(index)

internal operator fun PrimitiveArrayList<Char>.get(index: Int): Char = (arrayList as CharArrayList).getChar(index)

internal operator fun PrimitiveArrayList<Short>.get(index: Int): Short = (arrayList as ShortArrayList).getShort(index)

internal operator fun PrimitiveArrayList<Int>.get(index: Int): Int = (arrayList as IntArrayList).getInt(index)

internal operator fun PrimitiveArrayList<Long>.get(index: Int): Long = (arrayList as LongArrayList).getLong(index)

internal operator fun PrimitiveArrayList<Float>.get(index: Int): Float = (arrayList as FloatArrayList).getFloat(index)

internal operator fun PrimitiveArrayList<Double>.get(index: Int): Double =
    (arrayList as DoubleArrayList).getDouble(index)

// endregion
// region add

internal fun PrimitiveArrayList<Boolean>.add(element: Boolean): Boolean =
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).add(element)

        null -> {
            initializeArrayList(BOOLEAN)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Byte>.add(element: Byte): Boolean =
    when (state) {
        BYTE -> (arrayList as ByteArrayList).add(element)

        null -> {
            initializeArrayList(BYTE)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Char>.add(element: Char): Boolean =
    when (state) {
        CHAR -> (arrayList as CharArrayList).add(element)

        null -> {
            initializeArrayList(CHAR)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Short>.add(element: Short): Boolean =
    when (state) {
        SHORT -> (arrayList as ShortArrayList).add(element)

        null -> {
            initializeArrayList(SHORT)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Int>.add(element: Int): Boolean =
    when (state) {
        INT -> (arrayList as IntArrayList).add(element)

        null -> {
            initializeArrayList(INT)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Long>.add(element: Long): Boolean =
    when (state) {
        LONG -> (arrayList as LongArrayList).add(element)

        null -> {
            initializeArrayList(LONG)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Float>.add(element: Float): Boolean =
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).add(element)

        null -> {
            initializeArrayList(FLOAT)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal fun PrimitiveArrayList<Double>.add(element: Double): Boolean =
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).add(element)

        null -> {
            initializeArrayList(DOUBLE)
            add(element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }

internal operator fun PrimitiveArrayList<Boolean>.plusAssign(element: Boolean) {
    add(element)
}

internal operator fun PrimitiveArrayList<Byte>.plusAssign(element: Byte) {
    add(element)
}

internal operator fun PrimitiveArrayList<Char>.plusAssign(element: Char) {
    add(element)
}

internal operator fun PrimitiveArrayList<Short>.plusAssign(element: Short) {
    add(element)
}

internal operator fun PrimitiveArrayList<Int>.plusAssign(element: Int) {
    add(element)
}

internal operator fun PrimitiveArrayList<Long>.plusAssign(element: Long) {
    add(element)
}

internal operator fun PrimitiveArrayList<Float>.plusAssign(element: Float) {
    add(element)
}

internal operator fun PrimitiveArrayList<Double>.plusAssign(element: Double) {
    add(element)
}

internal fun PrimitiveArrayList<Boolean>.add(index: Int, element: Boolean) {
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).add(index, element)

        null -> {
            initializeArrayList(BOOLEAN)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Byte>.add(index: Int, element: Byte) {
    when (state) {
        BYTE -> (arrayList as ByteArrayList).add(index, element)

        null -> {
            initializeArrayList(BYTE)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Char>.add(index: Int, element: Char) {
    when (state) {
        CHAR -> (arrayList as CharArrayList).add(index, element)

        null -> {
            initializeArrayList(CHAR)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Short>.add(index: Int, element: Short) {
    when (state) {
        SHORT -> (arrayList as ShortArrayList).add(index, element)

        null -> {
            initializeArrayList(SHORT)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Int>.add(index: Int, element: Int) {
    when (state) {
        INT -> (arrayList as IntArrayList).add(index, element)

        null -> {
            initializeArrayList(INT)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Long>.add(index: Int, element: Long) {
    when (state) {
        LONG -> (arrayList as LongArrayList).add(index, element)

        null -> {
            initializeArrayList(LONG)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Float>.add(index: Int, element: Float) {
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).add(index, element)

        null -> {
            initializeArrayList(FLOAT)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

internal fun PrimitiveArrayList<Double>.add(index: Int, element: Double) {
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).add(index, element)

        null -> {
            initializeArrayList(DOUBLE)
            add(index, element)
        }

        else -> throw IllegalArgumentException("Unsupported element type: ${element::class}")
    }
}

// endregion
// region indexOf

internal fun PrimitiveArrayList<Boolean>.indexOf(element: Boolean): Int =
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Byte>.indexOf(element: Byte): Int =
    when (state) {
        BYTE -> (arrayList as ByteArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Char>.indexOf(element: Char): Int =
    when (state) {
        CHAR -> (arrayList as CharArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Short>.indexOf(element: Short): Int =
    when (state) {
        SHORT -> (arrayList as ShortArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Int>.indexOf(element: Int): Int =
    when (state) {
        INT -> (arrayList as IntArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Long>.indexOf(element: Long): Int =
    when (state) {
        LONG -> (arrayList as LongArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Float>.indexOf(element: Float): Int =
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).indexOf(element)
        else -> -1
    }

internal fun PrimitiveArrayList<Double>.indexOf(element: Double): Int =
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).indexOf(element)
        else -> -1
    }

// endregion
// region contains

internal fun PrimitiveArrayList<Boolean>.contains(element: Boolean): Boolean =
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Byte>.contains(element: Byte): Boolean =
    when (state) {
        BYTE -> (arrayList as ByteArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Char>.contains(element: Char): Boolean =
    when (state) {
        CHAR -> (arrayList as CharArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Short>.contains(element: Short): Boolean =
    when (state) {
        SHORT -> (arrayList as ShortArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Int>.contains(element: Int): Boolean =
    when (state) {
        INT -> (arrayList as IntArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Long>.contains(element: Long): Boolean =
    when (state) {
        LONG -> (arrayList as LongArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Float>.contains(element: Float): Boolean =
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).contains(element)
        else -> false
    }

internal fun PrimitiveArrayList<Double>.contains(element: Double): Boolean =
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).contains(element)
        else -> false
    }

// endregion
// region removeAt

internal fun PrimitiveArrayList<Boolean>.removeAt(index: Int): Boolean =
    (arrayList as BooleanArrayList).removeBoolean(index)

internal fun PrimitiveArrayList<Byte>.removeAt(index: Int): Byte = (arrayList as ByteArrayList).removeByte(index)

internal fun PrimitiveArrayList<Char>.removeAt(index: Int): Char = (arrayList as CharArrayList).removeChar(index)

internal fun PrimitiveArrayList<Short>.removeAt(index: Int): Short = (arrayList as ShortArrayList).removeShort(index)

internal fun PrimitiveArrayList<Int>.removeAt(index: Int): Int = (arrayList as IntArrayList).removeInt(index)

internal fun PrimitiveArrayList<Long>.removeAt(index: Int): Long = (arrayList as LongArrayList).removeLong(index)

internal fun PrimitiveArrayList<Float>.removeAt(index: Int): Float = (arrayList as FloatArrayList).removeFloat(index)

internal fun PrimitiveArrayList<Double>.removeAt(index: Int): Double =
    (arrayList as DoubleArrayList).removeDouble(index)

// endregion
// region set

internal operator fun PrimitiveArrayList<Boolean>.set(index: Int, element: Boolean): Boolean =
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).set(index, element)

        null -> {
            initializeArrayList(BOOLEAN)
            set(index, element)
        }

        else -> false
    }

internal operator fun PrimitiveArrayList<Byte>.set(index: Int, element: Byte): Byte =
    when (state) {
        BYTE -> (arrayList as ByteArrayList).set(index, element)

        null -> {
            initializeArrayList(BYTE)
            set(index, element)
        }

        else -> 0
    }

internal operator fun PrimitiveArrayList<Char>.set(index: Int, element: Char): Char =
    when (state) {
        CHAR -> (arrayList as CharArrayList).set(index, element)

        null -> {
            initializeArrayList(CHAR)
            set(index, element)
        }

        else -> 0.toChar()
    }

internal operator fun PrimitiveArrayList<Short>.set(index: Int, element: Short): Short =
    when (state) {
        SHORT -> (arrayList as ShortArrayList).set(index, element)

        null -> {
            initializeArrayList(SHORT)
            set(index, element)
        }

        else -> 0
    }

internal operator fun PrimitiveArrayList<Int>.set(index: Int, element: Int): Int =
    when (state) {
        INT -> (arrayList as IntArrayList).set(index, element)

        null -> {
            initializeArrayList(INT)
            set(index, element)
        }

        else -> 0
    }

internal operator fun PrimitiveArrayList<Long>.set(index: Int, element: Long): Long =
    when (state) {
        LONG -> (arrayList as LongArrayList).set(index, element)

        null -> {
            initializeArrayList(LONG)
            set(index, element)
        }

        else -> 0
    }

internal operator fun PrimitiveArrayList<Float>.set(index: Int, element: Float): Float =
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).set(index, element)

        null -> {
            initializeArrayList(FLOAT)
            set(index, element)
        }

        else -> 0f
    }

internal operator fun PrimitiveArrayList<Double>.set(index: Int, element: Double): Double =
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).set(index, element)

        null -> {
            initializeArrayList(DOUBLE)
            set(index, element)
        }

        else -> 0.0
    }

// endregion
// region remove

internal fun PrimitiveArrayList<Boolean>.remove(element: Boolean): Boolean =
    when (state) {
        BOOLEAN -> (arrayList as BooleanArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Byte>.remove(element: Byte): Boolean =
    when (state) {
        BYTE -> (arrayList as ByteArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Char>.remove(element: Char): Boolean =
    when (state) {
        CHAR -> (arrayList as CharArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Short>.remove(element: Short): Boolean =
    when (state) {
        SHORT -> (arrayList as ShortArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Int>.remove(element: Int): Boolean =
    when (state) {
        INT -> (arrayList as IntArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Long>.remove(element: Long): Boolean =
    when (state) {
        LONG -> (arrayList as LongArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Float>.remove(element: Float): Boolean =
    when (state) {
        FLOAT -> (arrayList as FloatArrayList).rem(element)
        else -> false
    }

internal fun PrimitiveArrayList<Double>.remove(element: Double): Boolean =
    when (state) {
        DOUBLE -> (arrayList as DoubleArrayList).rem(element)
        else -> false
    }

internal operator fun PrimitiveArrayList<Boolean>.minusAssign(element: Boolean) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Byte>.minusAssign(element: Byte) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Char>.minusAssign(element: Char) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Short>.minusAssign(element: Short) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Int>.minusAssign(element: Int) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Long>.minusAssign(element: Long) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Float>.minusAssign(element: Float) {
    remove(element)
}

internal operator fun PrimitiveArrayList<Double>.minusAssign(element: Double) {
    remove(element)
}

// endregion

public fun main() {
    val a = PrimitiveArrayList(intArrayOf(1, 2, 3))
    a -= 1
}
