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
        }

        var initCapacity = arrayList?.size ?: 0

        constructor() : this(
            arrayList = null,
            state = null,
        )

        constructor(initCapacity: Int) : this(
            arrayList = null,
            state = null,
        ) {
            this.initCapacity = initCapacity
        }

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

        constructor(booleans: BooleanArrayList) : this(
            arrayList = booleans as List<T>,
            state = BOOLEAN,
        )

        constructor(bytes: ByteArrayList) : this(
            arrayList = bytes as List<T>,
            state = BYTE,
        )

        constructor(chars: CharArrayList) : this(
            arrayList = chars as List<T>,
            state = CHAR,
        )

        constructor(shorts: ShortArrayList) : this(
            arrayList = shorts as List<T>,
            state = SHORT,
        )

        constructor(ints: IntArrayList) : this(
            arrayList = ints as List<T>,
            state = INT,
        )

        constructor(longs: LongArrayList) : this(
            arrayList = longs as List<T>,
            state = LONG,
        )

        constructor(floats: FloatArrayList) : this(
            arrayList = floats as List<T>,
            state = FLOAT,
        )

        constructor(doubles: DoubleArrayList) : this(
            arrayList = doubles as List<T>,
            state = DOUBLE,
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

        private fun initializeArrayList(state: State) {
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

        @Suppress("UNCHECKED_CAST")
        override fun add(element: T): Boolean =
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
                    add(element)
                }
            }

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
                    add(index, element)
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

        override val size: Int
            get() = arrayList?.size ?: 0

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun clear() = (arrayList as java.util.Collection<*>).clear()

        @Suppress("UNCHECKED_CAST")
        override fun get(index: Int): T =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).getBoolean(index)
                BYTE -> (arrayList as ByteArrayList).getByte(index)
                CHAR -> (arrayList as CharArrayList).getChar(index)
                SHORT -> (arrayList as ShortArrayList).getShort(index)
                INT -> (arrayList as IntArrayList).getInt(index)
                LONG -> (arrayList as LongArrayList).getLong(index)
                FLOAT -> (arrayList as FloatArrayList).getFloat(index)
                DOUBLE -> (arrayList as DoubleArrayList).getDouble(index)
                else -> throw IndexOutOfBoundsException("Index: $index, Size: $size")
            } as T

        override fun isEmpty(): Boolean = arrayList?.isEmpty() ?: true

        override fun indexOf(element: T): Int =
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

        override fun contains(element: T): Boolean =
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

        @Suppress("UNCHECKED_CAST")
        override fun removeAt(index: Int): T =
            when (state) {
                BOOLEAN -> (arrayList as BooleanArrayList).removeBoolean(index)
                BYTE -> (arrayList as ByteArrayList).removeByte(index)
                CHAR -> (arrayList as CharArrayList).removeChar(index)
                SHORT -> (arrayList as ShortArrayList).removeShort(index)
                INT -> (arrayList as IntArrayList).removeInt(index)
                LONG -> (arrayList as LongArrayList).removeLong(index)
                FLOAT -> (arrayList as FloatArrayList).removeFloat(index)
                DOUBLE -> (arrayList as DoubleArrayList).removeDouble(index)
                null -> error("List is not initialized")
            } as T

        @Suppress("UNCHECKED_CAST")
        override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
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

        @Suppress("UNCHECKED_CAST")
        override fun set(index: Int, element: T): T =
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
                    set(index, element)
                }
            } as T

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun retainAll(elements: Collection<T>): Boolean =
            (arrayList as java.util.Collection<*>?)?.retainAll(elements) ?: false

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        override fun removeAll(elements: Collection<T>): Boolean =
            (arrayList as java.util.Collection<*>?)?.removeAll(elements) ?: false

        override fun remove(element: T): Boolean =
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
    }

internal fun BooleanArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Boolean> = PrimitiveArrayList(this)

internal fun ByteArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Byte> = PrimitiveArrayList(this)

internal fun CharArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Char> = PrimitiveArrayList(this)

internal fun ShortArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Short> = PrimitiveArrayList(this)

internal fun IntArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Int> = PrimitiveArrayList(this)

internal fun LongArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Long> = PrimitiveArrayList(this)

internal fun FloatArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Float> = PrimitiveArrayList(this)

internal fun DoubleArrayList.asPrimitiveArrayList(): PrimitiveArrayList<Double> = PrimitiveArrayList(this)

internal fun PrimitiveArrayList<Boolean>.asBooleanArrayList(): BooleanArrayList = arrayList as BooleanArrayList

internal fun PrimitiveArrayList<Byte>.asByteArrayList(): ByteArrayList = arrayList as ByteArrayList

internal fun PrimitiveArrayList<Char>.asCharArrayList(): CharArrayList = arrayList as CharArrayList

internal fun PrimitiveArrayList<Short>.asShortArrayList(): ShortArrayList = arrayList as ShortArrayList

internal fun PrimitiveArrayList<Int>.asIntArrayList(): IntArrayList = arrayList as IntArrayList

internal fun PrimitiveArrayList<Long>.asLongArrayList(): LongArrayList = arrayList as LongArrayList

internal fun PrimitiveArrayList<Float>.asFloatArrayList(): FloatArrayList = arrayList as FloatArrayList

internal fun PrimitiveArrayList<Double>.asDoubleArrayList(): DoubleArrayList = arrayList as DoubleArrayList
