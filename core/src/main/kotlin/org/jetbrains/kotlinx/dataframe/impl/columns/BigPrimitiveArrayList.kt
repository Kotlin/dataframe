package org.jetbrains.kotlinx.dataframe.impl.columns

import it.unimi.dsi.fastutil.BigList
import it.unimi.dsi.fastutil.BigListIterator
import it.unimi.dsi.fastutil.booleans.BooleanBigArrayBigList
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator
import it.unimi.dsi.fastutil.bytes.ByteBigArrayBigList
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator
import it.unimi.dsi.fastutil.chars.CharBigArrayBigList
import it.unimi.dsi.fastutil.chars.CharBigListIterator
import it.unimi.dsi.fastutil.doubles.DoubleBigArrayBigList
import it.unimi.dsi.fastutil.doubles.DoubleBigListIterator
import it.unimi.dsi.fastutil.floats.FloatBigArrayBigList
import it.unimi.dsi.fastutil.floats.FloatBigListIterator
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList
import it.unimi.dsi.fastutil.ints.IntBigListIterator
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList
import it.unimi.dsi.fastutil.longs.LongBigListIterator
import it.unimi.dsi.fastutil.shorts.ShortBigArrayBigList
import it.unimi.dsi.fastutil.shorts.ShortBigListIterator
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.BOOLEAN
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.BYTE
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.CHAR
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.DOUBLE
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.FLOAT
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.INT
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.LONG
import org.jetbrains.kotlinx.dataframe.impl.columns.BigPrimitiveArrayList.State.SHORT
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal class BigPrimitiveArrayList<T : Any> private constructor(arrayList: BigList<T>?, state: State?) : BigList<T> {

    companion object {
        fun <T : Any> forTypeOrNull(kType: KType, initCapacity: Long = 0): BigPrimitiveArrayList<T>? {
            return when (kType) {
                typeOf<Boolean>() -> BigPrimitiveArrayList<Boolean>(BOOLEAN, initCapacity)
                typeOf<Byte>() -> BigPrimitiveArrayList<Byte>(BYTE, initCapacity)
                typeOf<Char>() -> BigPrimitiveArrayList<Char>(CHAR, initCapacity)
                typeOf<Short>() -> BigPrimitiveArrayList<Short>(SHORT, initCapacity)
                typeOf<Int>() -> BigPrimitiveArrayList<Int>(INT, initCapacity)
                typeOf<Long>() -> BigPrimitiveArrayList<Long>(LONG, initCapacity)
                typeOf<Float>() -> BigPrimitiveArrayList<Float>(FLOAT, initCapacity)
                typeOf<Double>() -> BigPrimitiveArrayList<Double>(DOUBLE, initCapacity)
                else -> return null
            } as BigPrimitiveArrayList<T>
        }

        inline fun <reified T : Any> forTypeOrNull(initCapacity: Long = 0): BigPrimitiveArrayList<T>? =
            forTypeOrNull(typeOf<T>(), initCapacity)

        fun <T : Any> forType(kType: KType, initCapacity: Long = 0): BigPrimitiveArrayList<T> =
            forTypeOrNull(kType, initCapacity) ?: throw IllegalArgumentException("Unsupported type: $kType")

        inline fun <reified T : Any> forType(initCapacity: Long = 0): BigPrimitiveArrayList<T> =
            forType(typeOf<T>(), initCapacity)
    }

    var initCapacity = arrayList?.size64() ?: 0L

    constructor() : this(
        arrayList = null,
        state = null,
    )

    constructor(initCapacity: Long) : this(
        arrayList = null,
        state = null,
    ) {
        this.initCapacity = initCapacity
    }

    constructor(state: State?) : this(
        arrayList = when (state) {
            BOOLEAN -> BooleanBigArrayBigList()
            BYTE -> ByteBigArrayBigList()
            CHAR -> CharBigArrayBigList()
            SHORT -> ShortBigArrayBigList()
            INT -> IntBigArrayBigList()
            LONG -> LongBigArrayBigList()
            FLOAT -> FloatBigArrayBigList()
            DOUBLE -> DoubleBigArrayBigList()
            null -> null
        } as BigList<T>?,
        state = state,
    )

    constructor(state: State?, initCapacity: Long) : this(
        arrayList = when (state) {
            BOOLEAN -> BooleanBigArrayBigList(initCapacity)
            BYTE -> ByteBigArrayBigList(initCapacity)
            CHAR -> CharBigArrayBigList(initCapacity)
            SHORT -> ShortBigArrayBigList(initCapacity)
            INT -> IntBigArrayBigList(initCapacity)
            LONG -> LongBigArrayBigList(initCapacity)
            FLOAT -> FloatBigArrayBigList(initCapacity)
            DOUBLE -> DoubleBigArrayBigList(initCapacity)
            null -> null
        } as BigList<T>?,
        state = state,
    )

    constructor(booleans: BooleanBigArrayBigList) : this(
        arrayList = booleans as BigList<T>,
        state = BOOLEAN,
    )

    constructor(bytes: ByteBigArrayBigList) : this(
        arrayList = bytes as BigList<T>,
        state = BYTE,
    )

    constructor(chars: CharBigArrayBigList) : this(
        arrayList = chars as BigList<T>,
        state = CHAR,
    )

    constructor(shorts: ShortBigArrayBigList) : this(
        arrayList = shorts as BigList<T>,
        state = SHORT,
    )

    constructor(ints: IntBigArrayBigList) : this(
        arrayList = ints as BigList<T>,
        state = INT,
    )

    constructor(longs: LongBigArrayBigList) : this(
        arrayList = longs as BigList<T>,
        state = LONG,
    )

    constructor(floats: FloatBigArrayBigList) : this(
        arrayList = floats as BigList<T>,
        state = FLOAT,
    )

    constructor(doubles: DoubleBigArrayBigList) : this(
        arrayList = doubles as BigList<T>,
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

    internal var arrayList: BigList<T>? = arrayList
        private set

    internal var state = state
        private set

    private fun initializeArrayList(state: State) {
        arrayList = when (state) {
            BOOLEAN -> BooleanBigArrayBigList(initCapacity)
            BYTE -> ByteBigArrayBigList(initCapacity)
            CHAR -> CharBigArrayBigList(initCapacity)
            SHORT -> ShortBigArrayBigList(initCapacity)
            INT -> IntBigArrayBigList(initCapacity)
            LONG -> LongBigArrayBigList(initCapacity)
            FLOAT -> FloatBigArrayBigList(initCapacity)
            DOUBLE -> DoubleBigArrayBigList(initCapacity)
        } as BigList<T>
        this.state = state
    }

    override fun listIterator(): BigListIterator<T> = listIterator(0)

    override fun listIterator(index: Long): BigListIterator<T> =
        object : BigListIterator<T> {
            private var it = arrayList?.listIterator(index)

            override fun add(element: T) {
                when (state) {
                    BOOLEAN -> (it as BooleanBigListIterator).add(element as Boolean)

                    BYTE -> (it as ByteBigListIterator).add(element as Byte)

                    CHAR -> (it as CharBigListIterator).add(element as Char)

                    SHORT -> (it as ShortBigListIterator).add(element as Short)

                    INT -> (it as IntBigListIterator).add(element as Int)

                    LONG -> (it as LongBigListIterator).add(element as Long)

                    FLOAT -> (it as FloatBigListIterator).add(element as Float)

                    DOUBLE -> (it as DoubleBigListIterator).add(element as Double)

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

            override fun nextIndex(): Long = it?.nextIndex() ?: -1L

            override fun previous(): T = it?.previous() ?: error("No previous element")

            override fun previousIndex(): Long = it?.previousIndex() ?: -1L

            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "KotlinConstantConditions")
            override fun remove() {
                when (val it = it) {
                    is BigListIterator<*> -> it.remove()
                    is java.util.Iterator<*> -> it.remove()
                    null -> error("No element to remove")
                    else -> throw UnsupportedOperationException()
                }
            }

            override fun set(element: T) {
                when (state) {
                    BOOLEAN -> (it as BooleanBigListIterator).set(element as Boolean)

                    BYTE -> (it as ByteBigListIterator).set(element as Byte)

                    CHAR -> (it as CharBigListIterator).set(element as Char)

                    SHORT -> (it as ShortBigListIterator).set(element as Short)

                    INT -> (it as IntBigListIterator).set(element as Int)

                    LONG -> (it as LongBigListIterator).set(element as Long)

                    FLOAT -> (it as FloatBigListIterator).set(element as Float)

                    DOUBLE -> (it as DoubleBigListIterator).set(element as Double)

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

    override fun lastIndexOf(element: Any?): Long =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).lastIndexOf(element as Boolean)
            BYTE -> (arrayList as ByteBigArrayBigList).lastIndexOf(element as Byte)
            CHAR -> (arrayList as CharBigArrayBigList).lastIndexOf(element as Char)
            SHORT -> (arrayList as ShortBigArrayBigList).lastIndexOf(element as Short)
            INT -> (arrayList as IntBigArrayBigList).lastIndexOf(element as Int)
            LONG -> (arrayList as LongBigArrayBigList).lastIndexOf(element as Long)
            FLOAT -> (arrayList as FloatBigArrayBigList).lastIndexOf(element as Float)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).lastIndexOf(element as Double)
            null -> error("List is not initialized")
        }

    @Suppress("UNCHECKED_CAST")
    override fun add(element: T): Boolean =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).add(element as Boolean)

            BYTE -> (arrayList as ByteBigArrayBigList).add(element as Byte)

            CHAR -> (arrayList as CharBigArrayBigList).add(element as Char)

            SHORT -> (arrayList as ShortBigArrayBigList).add(element as Short)

            INT -> (arrayList as IntBigArrayBigList).add(element as Int)

            LONG -> (arrayList as LongBigArrayBigList).add(element as Long)

            FLOAT -> (arrayList as FloatBigArrayBigList).add(element as Float)

            DOUBLE -> (arrayList as DoubleBigArrayBigList).add(element as Double)

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

    override fun add(index: Long, element: T) {
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).add(index, element as Boolean)

            BYTE -> (arrayList as ByteBigArrayBigList).add(index, element as Byte)

            CHAR -> (arrayList as CharBigArrayBigList).add(index, element as Char)

            SHORT -> (arrayList as ShortBigArrayBigList).add(index, element as Short)

            INT -> (arrayList as IntBigArrayBigList).add(index, element as Int)

            LONG -> (arrayList as LongBigArrayBigList).add(index, element as Long)

            FLOAT -> (arrayList as FloatBigArrayBigList).add(index, element as Float)

            DOUBLE -> (arrayList as DoubleBigArrayBigList).add(index, element as Double)

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
    override fun addAll(index: Long, elements: Collection<T>): Boolean =
        if (elements.isEmpty()) {
            false
        } else {
            when (state) {
                BOOLEAN -> (arrayList as BooleanBigArrayBigList).addAll(index, elements as Collection<Boolean>)

                BYTE -> (arrayList as ByteBigArrayBigList).addAll(index, elements as Collection<Byte>)

                CHAR -> (arrayList as CharBigArrayBigList).addAll(index, elements as Collection<Char>)

                SHORT -> (arrayList as ShortBigArrayBigList).addAll(index, elements as Collection<Short>)

                INT -> (arrayList as IntBigArrayBigList).addAll(index, elements as Collection<Int>)

                LONG -> (arrayList as LongBigArrayBigList).addAll(index, elements as Collection<Long>)

                FLOAT -> (arrayList as FloatBigArrayBigList).addAll(index, elements as Collection<Float>)

                DOUBLE -> (arrayList as DoubleBigArrayBigList).addAll(index, elements as Collection<Double>)

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
                BOOLEAN -> (arrayList as BooleanBigArrayBigList).addAll(elements as Collection<Boolean>)

                BYTE -> (arrayList as ByteBigArrayBigList).addAll(elements as Collection<Byte>)

                CHAR -> (arrayList as CharBigArrayBigList).addAll(elements as Collection<Char>)

                SHORT -> (arrayList as ShortBigArrayBigList).addAll(elements as Collection<Short>)

                INT -> (arrayList as IntBigArrayBigList).addAll(elements as Collection<Int>)

                LONG -> (arrayList as LongBigArrayBigList).addAll(elements as Collection<Long>)

                FLOAT -> (arrayList as FloatBigArrayBigList).addAll(elements as Collection<Float>)

                DOUBLE -> (arrayList as DoubleBigArrayBigList).addAll(elements as Collection<Double>)

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

    override fun size64(): Long = arrayList?.size64() ?: 0L

    override fun size(size: Long) {
        if (state == null) {
            initCapacity = size
        } else {
            arrayList!!.size(size)
        }
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    override fun clear() = (arrayList as java.util.Collection<*>).clear()

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Long): T =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).getBoolean(index)
            BYTE -> (arrayList as ByteBigArrayBigList).getByte(index)
            CHAR -> (arrayList as CharBigArrayBigList).getChar(index)
            SHORT -> (arrayList as ShortBigArrayBigList).getShort(index)
            INT -> (arrayList as IntBigArrayBigList).getInt(index)
            LONG -> (arrayList as LongBigArrayBigList).getLong(index)
            FLOAT -> (arrayList as FloatBigArrayBigList).getFloat(index)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).getDouble(index)
            else -> throw IndexOutOfBoundsException("Index: $index, Size: $size")
        } as T

    override fun isEmpty(): Boolean = arrayList?.isEmpty() ?: true

    override fun indexOf(element: Any?): Long =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).indexOf(element as Boolean)
            BYTE -> (arrayList as ByteBigArrayBigList).indexOf(element as Byte)
            CHAR -> (arrayList as CharBigArrayBigList).indexOf(element as Char)
            SHORT -> (arrayList as ShortBigArrayBigList).indexOf(element as Short)
            INT -> (arrayList as IntBigArrayBigList).indexOf(element as Int)
            LONG -> (arrayList as LongBigArrayBigList).indexOf(element as Long)
            FLOAT -> (arrayList as FloatBigArrayBigList).indexOf(element as Float)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).indexOf(element as Double)
            null -> -1
        }

    @Suppress("UNCHECKED_CAST")
    override fun containsAll(elements: Collection<T>): Boolean =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).containsAll(elements as Collection<Boolean>)
            BYTE -> (arrayList as ByteBigArrayBigList).containsAll(elements as Collection<Byte>)
            CHAR -> (arrayList as CharBigArrayBigList).containsAll(elements as Collection<Char>)
            SHORT -> (arrayList as ShortBigArrayBigList).containsAll(elements as Collection<Short>)
            INT -> (arrayList as IntBigArrayBigList).containsAll(elements as Collection<Int>)
            LONG -> (arrayList as LongBigArrayBigList).containsAll(elements as Collection<Long>)
            FLOAT -> (arrayList as FloatBigArrayBigList).containsAll(elements as Collection<Float>)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).containsAll(elements as Collection<Double>)
            null -> elements.isEmpty()
        }

    override fun contains(element: T): Boolean =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).contains(element as Boolean)
            BYTE -> (arrayList as ByteBigArrayBigList).contains(element as Byte)
            CHAR -> (arrayList as CharBigArrayBigList).contains(element as Char)
            SHORT -> (arrayList as ShortBigArrayBigList).contains(element as Short)
            INT -> (arrayList as IntBigArrayBigList).contains(element as Int)
            LONG -> (arrayList as LongBigArrayBigList).contains(element as Long)
            FLOAT -> (arrayList as FloatBigArrayBigList).contains(element as Float)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).contains(element as Double)
            null -> false
        }

    @Suppress("UNCHECKED_CAST")
    override fun remove(index: Long): T =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).removeBoolean(index)
            BYTE -> (arrayList as ByteBigArrayBigList).removeByte(index)
            CHAR -> (arrayList as CharBigArrayBigList).removeChar(index)
            SHORT -> (arrayList as ShortBigArrayBigList).removeShort(index)
            INT -> (arrayList as IntBigArrayBigList).removeInt(index)
            LONG -> (arrayList as LongBigArrayBigList).removeLong(index)
            FLOAT -> (arrayList as FloatBigArrayBigList).removeFloat(index)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).removeDouble(index)
            null -> error("List is not initialized")
        } as T

    @Suppress("UNCHECKED_CAST")
    override fun subList(fromIndex: Long, toIndex: Long): BigList<T> =
        when (state) {
            BOOLEAN -> BooleanBigArrayBigList(
                (arrayList as BooleanBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            BYTE -> ByteBigArrayBigList(
                (arrayList as ByteBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            CHAR -> CharBigArrayBigList(
                (arrayList as CharBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            SHORT -> ShortBigArrayBigList(
                (arrayList as ShortBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            INT -> IntBigArrayBigList(
                (arrayList as IntBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            LONG -> LongBigArrayBigList(
                (arrayList as LongBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            FLOAT -> FloatBigArrayBigList(
                (arrayList as FloatBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            DOUBLE -> DoubleBigArrayBigList(
                (arrayList as DoubleBigArrayBigList).subList(fromIndex, toIndex),
            ).asBigPrimitiveArrayList()

            null -> error("List is not initialized")
        } as BigPrimitiveArrayList<T>

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Long, element: T): T =
        when (state) {
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).set(index, element as Boolean)

            BYTE -> (arrayList as ByteBigArrayBigList).set(index, element as Byte)

            CHAR -> (arrayList as CharBigArrayBigList).set(index, element as Char)

            SHORT -> (arrayList as ShortBigArrayBigList).set(index, element as Short)

            INT -> (arrayList as IntBigArrayBigList).set(index, element as Int)

            LONG -> (arrayList as LongBigArrayBigList).set(index, element as Long)

            FLOAT -> (arrayList as FloatBigArrayBigList).set(index, element as Float)

            DOUBLE -> (arrayList as DoubleBigArrayBigList).set(index, element as Double)

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
            BOOLEAN -> (arrayList as BooleanBigArrayBigList).rem(element as Boolean)
            BYTE -> (arrayList as ByteBigArrayBigList).rem(element as Byte)
            CHAR -> (arrayList as CharBigArrayBigList).rem(element as Char)
            SHORT -> (arrayList as ShortBigArrayBigList).rem(element as Short)
            INT -> (arrayList as IntBigArrayBigList).rem(element as Int)
            LONG -> (arrayList as LongBigArrayBigList).rem(element as Long)
            FLOAT -> (arrayList as FloatBigArrayBigList).rem(element as Float)
            DOUBLE -> (arrayList as DoubleBigArrayBigList).rem(element as Double)
            null -> false
        }
}

internal fun BooleanBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Boolean> =
    BigPrimitiveArrayList(this)

internal fun ByteBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Byte> = BigPrimitiveArrayList(this)

internal fun CharBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Char> = BigPrimitiveArrayList(this)

internal fun ShortBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Short> = BigPrimitiveArrayList(this)

internal fun IntBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Int> = BigPrimitiveArrayList(this)

internal fun LongBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Long> = BigPrimitiveArrayList(this)

internal fun FloatBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Float> = BigPrimitiveArrayList(this)

internal fun DoubleBigArrayBigList.asBigPrimitiveArrayList(): BigPrimitiveArrayList<Double> =
    BigPrimitiveArrayList(this)

internal fun BigPrimitiveArrayList<Boolean>.asBooleanArrayList(): BooleanBigArrayBigList =
    arrayList as BooleanBigArrayBigList

internal fun BigPrimitiveArrayList<Byte>.asByteArrayList(): ByteBigArrayBigList = arrayList as ByteBigArrayBigList

internal fun BigPrimitiveArrayList<Char>.asCharArrayList(): CharBigArrayBigList = arrayList as CharBigArrayBigList

internal fun BigPrimitiveArrayList<Short>.asShortArrayList(): ShortBigArrayBigList = arrayList as ShortBigArrayBigList

internal fun BigPrimitiveArrayList<Int>.asIntArrayList(): IntBigArrayBigList = arrayList as IntBigArrayBigList

internal fun BigPrimitiveArrayList<Long>.asLongArrayList(): LongBigArrayBigList = arrayList as LongBigArrayBigList

internal fun BigPrimitiveArrayList<Float>.asFloatArrayList(): FloatBigArrayBigList = arrayList as FloatBigArrayBigList

internal fun BigPrimitiveArrayList<Double>.asDoubleArrayList(): DoubleBigArrayBigList =
    arrayList as DoubleBigArrayBigList
