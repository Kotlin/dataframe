@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl.columns

import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import it.unimi.dsi.fastutil.chars.CharArrayList
import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSortedSet
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Using the [ofPrimitiveArray] functions, this can store natively without converting:
 * - [BooleanArray]
 * - [ByteArray]
 * - [ShortArray]
 * - [IntArray]
 * - [LongArray]
 * - [FloatArray]
 * - [DoubleArray]
 * - [CharArray]
 * - [UByteArray]
 * - [UShortArray]
 * - [UIntArray]
 * - [ULongArray]
 *
 * Store with converting to primitive arrays:
 * - [Array][Array]`<`[Boolean?][Boolean]`>`
 * - [Array][Array]`<`[Byte?][Byte]`>`
 * - [Array][Array]`<`[Short?][Short]`>`
 * - [Array][Array]`<`[Int?][Int]`>`
 * - [Array][Array]`<`[Long?][Long]`>`
 * - [Array][Array]`<`[Float?][Float]`>`
 * - [Array][Array]`<`[Double?][Double]`>`
 * - [Array][Array]`<`[Char?][Char]`>`
 * - [Collection][Collection]`<`[Boolean?][Boolean]`>`
 * - [Collection][Collection]`<`[Byte?][Byte]`>`
 * - [Collection][Collection]`<`[Short?][Short]`>`
 * - [Collection][Collection]`<`[Int?][Int]`>`
 * - [Collection][Collection]`<`[Long?][Long]`>`
 * - [Collection][Collection]`<`[Float?][Float]`>`
 * - [Collection][Collection]`<`[Double?][Double]`>`
 * - [Collection][Collection]`<`[Char?][Char]`>`
 *
 * Yes, as you can see, also nullable types are supported. The values are stored in primitive arrays,
 * and a separate set is used to store the indices of the null values.
 *
 * Since, [ColumnDataHolder] can be used as a [List], this is invisible to the user.
 *
 * @param T the type of the elements in the column
 * @param list a [PrimitiveArrayList] or any other [MutableList] that can store the elements
 * @param zeroValue When [list] is a [PrimitiveArrayList], this is the zero value for the primitive type
 * @param nullIndices a set of indices where the null values are stored, only used if [list] is a [PrimitiveArrayList]
 */
internal open class ColumnDataHolderImpl<T>(
    protected var list: MutableList<T> = PrimitiveArrayList<Any>() as MutableList<T>,
    distinct: Lazy<Set<T>>? = null,
    protected var zeroValue: Any? = Undefined,
    protected val nullIndices: IntSortedSet = IntAVLTreeSet(),
) : ColumnDataHolder<T> {

    protected object Undefined

    protected fun IntSortedSet.fastContains(index: Int): Boolean =
        when (size) {
            0 -> false
            1 -> firstInt() == index
            2 -> firstInt() == index || lastInt() == index
            else -> contains(index)
        }

    override val distinct = distinct ?: lazy {
        buildSet {
            addAll(this@ColumnDataHolderImpl)
        }.toMutableSet()
    }

    override val size: Int
        get() = if (zeroValue is Undefined) {
            nullIndices.size
        } else {
            list.size
        }

    override var usesPrimitiveArrayList = list is PrimitiveArrayList<*>

    override fun canAddPrimitively(element: Any?): Boolean =
        when {
            !usesPrimitiveArrayList -> false
            element == null -> true
            list is PrimitiveArrayList<*> -> (list as PrimitiveArrayList<*>).canAdd(element)
            else -> false
        }

    private inline fun addingElement(
        elementIsNull: Boolean,
        listCanAddElement: Boolean,
        addElementToDistinctSet: () -> Unit,
        addElementToList: () -> Unit,
        addZeroValueToList: () -> Unit,
        setZeroValue: () -> Unit,
    ) {
        // check if we need to switch to a boxed mutable list to add this element
        if (usesPrimitiveArrayList &&
            !elementIsNull &&
            !listCanAddElement
        ) {
            switchToBoxedList()
        }

        if (distinct.isInitialized()) {
            addElementToDistinctSet()
        }

        if (!usesPrimitiveArrayList) {
            addElementToList()
        } else if (elementIsNull) {
            nullIndices += size
            if (zeroValue !is Undefined) {
                addZeroValueToList()
            }
        } else {
            // set a new zeroValue if the current one is unset
            if (zeroValue is Undefined) {
                setZeroValue()
                nullIndices.forEach { _ ->
                    addZeroValueToList()
                }
            }

            addElementToList()
        }
    }

    internal fun switchToBoxedList() {
        list = this.toMutableList()
        usesPrimitiveArrayList = false
        nullIndices.clear()
    }

    override fun add(boolean: Boolean) {
        val zeroValue = zeroValueFor(boolean)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(boolean),
            addElementToDistinctSet = { (distinct.value as MutableSet<Boolean>) += boolean },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Boolean>).add(boolean)
                } else {
                    list.add(boolean as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Boolean>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(byte: Byte) {
        val zeroValue = zeroValueFor(byte)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(byte),
            addElementToDistinctSet = { (distinct.value as MutableSet<Byte>) += byte },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Byte>).add(byte)
                } else {
                    list.add(byte as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Byte>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(short: Short) {
        val zeroValue = zeroValueFor(short)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(short),
            addElementToDistinctSet = { (distinct.value as MutableSet<Short>) += short },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Short>).add(short)
                } else {
                    list.add(short as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Short>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(int: Int) {
        val zeroValue = zeroValueFor(int)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(int),
            addElementToDistinctSet = { (distinct.value as MutableSet<Int>) += int },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Int>).add(int)
                } else {
                    list.add(int as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Int>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(long: Long) {
        val zeroValue = zeroValueFor(long)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(long),
            addElementToDistinctSet = { (distinct.value as MutableSet<Long>) += long },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Long>).add(long)
                } else {
                    list.add(long as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Long>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(float: Float) {
        val zeroValue = zeroValueFor(float)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(float),
            addElementToDistinctSet = { (distinct.value as MutableSet<Float>) += float },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Float>).add(float)
                } else {
                    list.add(float as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Float>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(double: Double) {
        val zeroValue = zeroValueFor(double)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(double),
            addElementToDistinctSet = { (distinct.value as MutableSet<Double>) += double },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Double>).add(double)
                } else {
                    list.add(double as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Double>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(char: Char) {
        val zeroValue = zeroValueFor(char)
        addingElement(
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(char),
            addElementToDistinctSet = { (distinct.value as MutableSet<Char>) += char },
            addElementToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Char>).add(char)
                } else {
                    list.add(char as T)
                }
            },
            addZeroValueToList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Char>).add(zeroValue)
                } else {
                    list.add(zeroValue as T)
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun add(element: T) {
        addingElement(
            elementIsNull = element == null,
            listCanAddElement = element != null && (list as? PrimitiveArrayList<*>)?.canAdd(element) ?: true,
            addElementToDistinctSet = { (distinct.value as MutableSet<T>) += element },
            addElementToList = { list.add(element) },
            addZeroValueToList = { list.add(zeroValue as T) },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValueFor(element) },
        )
    }

    private inline fun settingElement(
        index: Int,
        elementIsNull: Boolean,
        listCanAddElement: Boolean,
        updateDistinctSet: () -> Unit,
        setElementInList: () -> Unit,
        setZeroValueInList: () -> Unit,
        setZeroValue: () -> Unit,
    ) {
        // check if we need to switch to a boxed mutable list to add this element
        if (usesPrimitiveArrayList &&
            !elementIsNull &&
            !listCanAddElement
        ) {
            switchToBoxedList()
        }

        if (distinct.isInitialized()) {
            updateDistinctSet()
        }

        if (!usesPrimitiveArrayList) {
            setElementInList()
        } else if (elementIsNull) {
            nullIndices += index
            if (zeroValue is Undefined) {
                setZeroValueInList() // might be out of bounds and crash
            }
        } else {
            // set a new zeroValue if the current one is unset
            if (zeroValue is Undefined) {
                setZeroValue()
                nullIndices.forEach { _ ->
                    setZeroValueInList()
                }
            }

            setElementInList()
            nullIndices -= index
        }
    }

    override fun set(index: Int, value: Boolean) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Boolean>)[index]
                } else {
                    list[index] as Boolean?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Boolean>)[index] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Boolean>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Boolean>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Boolean>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Byte) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Byte>)[index]
                } else {
                    list[index] as Byte?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Byte>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Byte>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Byte>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Byte>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Short) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Short>)[index]
                } else {
                    list[index] as Short?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Short>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Short>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Short>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Short>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Int) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Int>)[index]
                } else {
                    list[index] as Int?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Int>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Int>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Int>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Int>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Long) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Long>)[index]
                } else {
                    list[index] as Long?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Long>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Long>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Long>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Long>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Float) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Float>)[index]
                } else {
                    list[index] as Float?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Float>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Float>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Float>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Float>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Double) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Double>)[index]
                } else {
                    list[index] as Double?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Double>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Double>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Double>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Double>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun set(index: Int, value: Char) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = false,
            listCanAddElement = !usesPrimitiveArrayList || (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Char>)[index]
                } else {
                    list[index] as Char?
                }
                val countOfPrevValue = (0..<size).count {
                    if (usesPrimitiveArrayList) {
                        (list as PrimitiveArrayList<Char>)[it] == prevValue
                    } else {
                        list[it] == prevValue
                    }
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<Char>).remove(prevValue)
                }
            },
            setElementInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Char>)[index] = value
                } else {
                    list[index] = value as T
                }
            },
            setZeroValueInList = {
                if (usesPrimitiveArrayList) {
                    (list as PrimitiveArrayList<Char>)[index] = zeroValue
                } else {
                    list[index] = zeroValue as T
                }
            },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun isNull(index: Int): Boolean =
        if (usesPrimitiveArrayList) {
            nullIndices.fastContains(index)
        } else {
            list[index] == null
        }

    override fun hasNulls(): Boolean =
        if (usesPrimitiveArrayList) {
            nullIndices.isNotEmpty()
        } else {
            list.any { it == null }
        }

    override fun set(index: Int, value: T) {
        val zeroValue = zeroValueFor(value)
        settingElement(
            index = index,
            elementIsNull = value == null,
            listCanAddElement = value != null && (list as PrimitiveArrayList<*>).canAdd(value),
            updateDistinctSet = {
                val prevValue = list[index]
                val countOfPrevValue = (0..<size).count {
                    list[it] == prevValue
                }
                if (countOfPrevValue <= 1 && value != prevValue) {
                    (distinct.value as MutableSet<T>).remove(prevValue)
                }
            },
            setElementInList = { list[index] = value },
            setZeroValueInList = { list[index] = zeroValue as T },
            setZeroValue = { this@ColumnDataHolderImpl.zeroValue = zeroValue },
        )
    }

    override fun isEmpty(): Boolean = size == 0

    override fun indexOf(element: T): Int {
        if (!usesPrimitiveArrayList) return list.indexOf(element)

        if (element == null) return nullIndices.firstInt()
        for (i in list.indices) {
            if (nullIndices.fastContains(i)) continue
            if (list[i] == element) return i
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean = elements.toSet().all { contains(it) }

    override fun toSet(): Set<T> = distinct.value

    override fun get(index: Int): T =
        when {
            usesPrimitiveArrayList && nullIndices.fastContains(index) -> null as T
            zeroValue is Undefined && index < nullIndices.size -> null as T
            else -> list[index]
        }

    override fun get(range: IntRange): List<T> {
        if (!usesPrimitiveArrayList) {
            return list.subList(range.first, range.last + 1)
        }
        if (zeroValue is Undefined && range.first >= 0 && range.last < nullIndices.size) {
            return List(range.last - range.first + 1) { null as T }
        }

        val start = range.first
        val sublist = list.subList(start, range.last + 1).toMutableList()
        for (i in sublist.indices) {
            if (nullIndices.fastContains(start + i)) sublist[i] = null as T
        }
        return sublist
    }

    override fun contains(element: T): Boolean =
        if (usesPrimitiveArrayList && element == null) {
            nullIndices.isNotEmpty()
        } else {
            element in list
        }

    override fun iterator(): Iterator<T> = listIterator()

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> =
        when {
            !usesPrimitiveArrayList -> list.listIterator(index)

            zeroValue is Undefined -> List(nullIndices.size) { null as T }.listIterator(index)

            else -> object : ListIterator<T> {

                val iterator = list.listIterator(index)

                override fun hasNext(): Boolean = iterator.hasNext()

                override fun hasPrevious(): Boolean = iterator.hasNext()

                override fun next(): T {
                    val i = nextIndex()
                    val res = iterator.next()
                    return if (nullIndices.fastContains(i)) null as T else res
                }

                override fun nextIndex(): Int = iterator.nextIndex()

                override fun previous(): T {
                    val i = previousIndex()
                    val res = iterator.previous()
                    return if (nullIndices.fastContains(i)) null as T else res
                }

                override fun previousIndex(): Int = iterator.previousIndex()
            }
        }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = get(fromIndex..<toIndex)

    override fun lastIndexOf(element: T): Int {
        if (element == null) return nullIndices.lastInt()
        for (i in list.indices.reversed()) {
            if (nullIndices.fastContains(i)) continue
            if (list[i] == element) return i
        }
        return -1
    }

//    // TODO optimize
//    override fun equals(other: Any?): Boolean = toList() == other
//
//    // TODO optimize
//    override fun hashCode(): Int = toList().hashCode()

    override fun toString(): String = (this as Iterable<T>).joinToString(prefix = "[", postfix = "]")
}

internal val BOOLEAN = typeOf<Boolean>()
internal val BYTE = typeOf<Byte>()
internal val SHORT = typeOf<Short>()
internal val INT = typeOf<Int>()
internal val LONG = typeOf<Long>()
internal val FLOAT = typeOf<Float>()
internal val DOUBLE = typeOf<Double>()
internal val CHAR = typeOf<Char>()
internal val UBYTE = typeOf<UByte>()
internal val USHORT = typeOf<UShort>()
internal val UINT = typeOf<UInt>()
internal val ULONG = typeOf<ULong>()
internal val ANY = typeOf<Any>()

internal val NULLABLE_BOOLEAN = typeOf<Boolean?>()
internal val NULLABLE_BYTE = typeOf<Byte?>()
internal val NULLABLE_SHORT = typeOf<Short?>()
internal val NULLABLE_INT = typeOf<Int?>()
internal val NULLABLE_LONG = typeOf<Long?>()
internal val NULLABLE_FLOAT = typeOf<Float?>()
internal val NULLABLE_DOUBLE = typeOf<Double?>()
internal val NULLABLE_CHAR = typeOf<Char?>()
internal val NULLABLE_UBYTE = typeOf<UByte?>()
internal val NULLABLE_USHORT = typeOf<UShort?>()
internal val NULLABLE_UINT = typeOf<UInt?>()
internal val NULLABLE_ULONG = typeOf<ULong?>()
internal val NULLABLE_ANY = typeOf<Any?>()

internal fun zeroValueOf(type: KType): Any? =
    when (type) {
        NULLABLE_BOOLEAN, BOOLEAN -> false
        NULLABLE_BYTE, BYTE -> 0.toByte()
        NULLABLE_SHORT, SHORT -> 0.toShort()
        NULLABLE_INT, INT -> 0
        NULLABLE_LONG, LONG -> 0L
        NULLABLE_FLOAT, FLOAT -> 0.0f
        NULLABLE_DOUBLE, DOUBLE -> 0.0
        NULLABLE_CHAR, CHAR -> 0.toChar()
        NULLABLE_UBYTE, UBYTE -> 0.toUByte()
        NULLABLE_USHORT, USHORT -> 0.toUShort()
        NULLABLE_UINT, UINT -> 0.toUInt()
        NULLABLE_ULONG, ULONG -> 0.toULong()
        else -> null
    }

internal fun zeroValueFor(element: Any?): Any? =
    when (element) {
        null -> null
        is Boolean -> false
        is Byte -> 0.toByte()
        is Short -> 0.toShort()
        is Int -> 0
        is Long -> 0L
        is Float -> 0.0f
        is Double -> 0.0
        is Char -> 0.toChar()
        is UByte -> 0.toUByte()
        is UShort -> 0.toUShort()
        is UInt -> 0.toUInt()
        is ULong -> 0.toULong()
        else -> null
    }

internal fun zeroValueFor(element: Boolean): Boolean = false

internal fun zeroValueFor(element: Boolean?): Boolean? = if (element == null) null else false

internal fun zeroValueFor(element: Byte): Byte = 0.toByte()

internal fun zeroValueFor(element: Byte?): Byte? = if (element == null) null else 0.toByte()

internal fun zeroValueFor(element: Short): Short = 0.toShort()

internal fun zeroValueFor(element: Short?): Short? = if (element == null) null else 0.toShort()

internal fun zeroValueFor(element: Int): Int = 0

internal fun zeroValueFor(element: Int?): Int? = if (element == null) null else 0

internal fun zeroValueFor(element: Long): Long = 0L

internal fun zeroValueFor(element: Long?): Long? = if (element == null) null else 0L

internal fun zeroValueFor(element: Float): Float = 0.0f

internal fun zeroValueFor(element: Float?): Float? = if (element == null) null else 0.0f

internal fun zeroValueFor(element: Double): Double = 0.0

internal fun zeroValueFor(element: Double?): Double? = if (element == null) null else 0.0

internal fun zeroValueFor(element: Char): Char = 0.toChar()

internal fun zeroValueFor(element: Char?): Char? = if (element == null) null else 0.toChar()

internal fun zeroValueFor(element: UByte): UByte = 0.toUByte()

internal fun zeroValueFor(element: UByte?): UByte? = if (element == null) null else 0.toUByte()

internal fun zeroValueFor(element: UShort): UShort = 0.toUShort()

internal fun zeroValueFor(element: UShort?): UShort? = if (element == null) null else 0.toUShort()

internal fun zeroValueFor(element: UInt): UInt = 0.toUInt()

internal fun zeroValueFor(element: UInt?): UInt? = if (element == null) null else 0.toUInt()

internal fun zeroValueFor(element: ULong): ULong = 0.toULong()

internal fun zeroValueFor(element: ULong?): ULong? = if (element == null) null else 0.toULong()

private fun <T> Array<T?>.fillNulls(zeroValue: Any, nullIndices: BooleanArray): Array<T> {
    for (i in indices) {
        if (this[i] == null) {
            this[i] = zeroValue as T
            nullIndices[i] = true
        }
    }
    return this as Array<T>
}

private fun <T> MutableList<T?>.fillNulls(zeroValue: Any, nullIndices: BooleanArray): List<T> {
    for (i in indices) {
        if (this[i] == null) {
            this[i] = zeroValue as T
            nullIndices[i] = true
        }
    }
    return this as List<T>
}

private fun BooleanArray.indicesWhereTrue(): IntSortedSet {
    val set = IntAVLTreeSet()
    for (i in indices) {
        if (this[i]) set += i
    }
    return set
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [collection].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.ofCollection(
    collection: Collection<T>,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    if (collection is ColumnDataHolder<*>) return collection as ColumnDataHolder<T>

    try {
        val isNull = BooleanArray(collection.size)
        val newList = when (type) {
            BOOLEAN -> BooleanArrayList((collection as Collection<Boolean>).toBooleanArray()).asPrimitiveArrayList()

            BYTE -> ByteArrayList((collection as Collection<Byte>).toByteArray()).asPrimitiveArrayList()

            SHORT -> ShortArrayList((collection as Collection<Short>).toShortArray()).asPrimitiveArrayList()

            INT -> IntArrayList((collection as Collection<Int>).toIntArray()).asPrimitiveArrayList()

            LONG -> LongArrayList((collection as Collection<Long>).toLongArray()).asPrimitiveArrayList()

            FLOAT -> FloatArrayList((collection as Collection<Float>).toFloatArray()).asPrimitiveArrayList()

            DOUBLE -> DoubleArrayList((collection as Collection<Double>).toDoubleArray()).asPrimitiveArrayList()

            CHAR -> CharArrayList((collection as Collection<Char>).toCharArray()).asPrimitiveArrayList()

//            UBYTE -> (collection as Collection<UByte>).toUByteArray().asList()
//
//            USHORT -> (collection as Collection<UShort>).toUShortArray().asList()
//
//            UINT -> (collection as Collection<UInt>).toUIntArray().asList()
//
//            ULONG -> (collection as Collection<ULong>).toULongArray().asList()

            NULLABLE_BOOLEAN -> BooleanArrayList(
                (collection as Collection<Boolean?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toBooleanArray(),
            ).asPrimitiveArrayList()

            NULLABLE_BYTE -> ByteArrayList(
                (collection as Collection<Byte?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toByteArray(),
            ).asPrimitiveArrayList()

            NULLABLE_SHORT -> ShortArrayList(
                (collection as Collection<Short?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toShortArray(),
            ).asPrimitiveArrayList()

            NULLABLE_INT -> IntArrayList(
                (collection as Collection<Int?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toIntArray(),
            ).asPrimitiveArrayList()

            NULLABLE_LONG -> LongArrayList(
                (collection as Collection<Long?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toLongArray(),
            ).asPrimitiveArrayList()

            NULLABLE_FLOAT -> FloatArrayList(
                (collection as Collection<Float?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toFloatArray(),
            ).asPrimitiveArrayList()

            NULLABLE_DOUBLE -> DoubleArrayList(
                (collection as Collection<Double?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toDoubleArray(),
            ).asPrimitiveArrayList()

            NULLABLE_CHAR -> CharArrayList(
                (collection as Collection<Char?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toCharArray(),
            ).asPrimitiveArrayList()

//            NULLABLE_UBYTE -> (collection as Collection<UByte?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUByteArray()
//                .asList()
//
//            NULLABLE_USHORT -> (collection as Collection<UShort?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUShortArray()
//                .asList()
//
//            NULLABLE_UINT -> (collection as Collection<UInt?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUIntArray()
//                .asList()
//
//            NULLABLE_ULONG -> (collection as Collection<ULong?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toULongArray()
//                .asList()

            else -> {
                for ((i, it) in collection.withIndex()) {
                    if (it == null) isNull[i] = true
                }
                collection.toMutableList()
            }
        } as MutableList<T>

        return ColumnDataHolderImpl(
            list = newList,
            distinct = distinct,
            zeroValue = zeroValueOf(type) as T,
            nullIndices = if (newList is PrimitiveArrayList<*>) isNull.indicesWhereTrue() else IntAVLTreeSet(),
        )
    } catch (e: Exception) {
        throw IllegalArgumentException("Can't create ColumnDataHolder from $collection and type $type", e)
    }
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [array].
 * If [array] is an array of primitives, it will be converted to a primitive array first before being
 * wrapped with [asList].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.ofBoxedArray(
    array: Array<T>,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    try {
        val isNull = BooleanArray(array.size)
        val list = when (type) {
            BOOLEAN -> BooleanArrayList((array as Array<Boolean>).toBooleanArray()).asPrimitiveArrayList()

            BYTE -> ByteArrayList((array as Array<Byte>).toByteArray()).asPrimitiveArrayList()

            SHORT -> ShortArrayList((array as Array<Short>).toShortArray()).asPrimitiveArrayList()

            INT -> IntArrayList((array as Array<Int>).toIntArray()).asPrimitiveArrayList()

            LONG -> LongArrayList((array as Array<Long>).toLongArray()).asPrimitiveArrayList()

            FLOAT -> FloatArrayList((array as Array<Float>).toFloatArray()).asPrimitiveArrayList()

            DOUBLE -> DoubleArrayList((array as Array<Double>).toDoubleArray()).asPrimitiveArrayList()

            CHAR -> CharArrayList((array as Array<Char>).toCharArray()).asPrimitiveArrayList()

//            UBYTE -> (array as Array<UByte>).toUByteArray().asList()
//
//            USHORT -> (array as Array<UShort>).toUShortArray().asList()
//
//            UINT -> (array as Array<UInt>).toUIntArray().asList()
//
//            ULONG -> (array as Array<ULong>).toULongArray().asList()

            NULLABLE_BOOLEAN -> BooleanArrayList(
                (array as Array<Boolean?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toBooleanArray(),
            ).asPrimitiveArrayList()

            NULLABLE_BYTE -> ByteArrayList(
                (array as Array<Byte?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toByteArray(),
            ).asPrimitiveArrayList()

            NULLABLE_SHORT -> ShortArrayList(
                (array as Array<Short?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toShortArray(),
            ).asPrimitiveArrayList()

            NULLABLE_INT -> IntArrayList(
                (array as Array<Int?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toIntArray(),
            ).asPrimitiveArrayList()

            NULLABLE_LONG -> LongArrayList(
                (array as Array<Long?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toLongArray(),
            ).asPrimitiveArrayList()

            NULLABLE_FLOAT -> FloatArrayList(
                (array as Array<Float?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toFloatArray(),
            ).asPrimitiveArrayList()

            NULLABLE_DOUBLE -> DoubleArrayList(
                (array as Array<Double?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toDoubleArray(),
            ).asPrimitiveArrayList()

            NULLABLE_CHAR -> CharArrayList(
                (array as Array<Char?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toCharArray(),
            ).asPrimitiveArrayList()

//            NULLABLE_UBYTE -> (array as Array<UByte?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUByteArray()
//                .asList()
//
//            NULLABLE_USHORT -> (array as Array<UShort?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUShortArray()
//                .asList()
//
//            NULLABLE_UINT -> (array as Array<UInt?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUIntArray()
//                .asList()
//
//            NULLABLE_ULONG -> (array as Array<ULong?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toULongArray()
//                .asList()

            else -> {
                for ((i, it) in array.withIndex()) {
                    if (it == null) isNull[i] = true
                }
                array.toMutableList()
            }
        } as MutableList<T>

        return ColumnDataHolderImpl(
            list = list,
            distinct = distinct,
            zeroValue = zeroValueOf(type),
            nullIndices = if (list is PrimitiveArrayList<*>) isNull.indicesWhereTrue() else IntAVLTreeSet(),
        )
    } catch (e: Exception) {
        throw IllegalArgumentException(
            "Can't create ColumnDataHolder from $array and mismatching type $type",
            e,
        )
    }
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [primitiveArray].
 * [primitiveArray] must be an array of primitives, returns `null` if something goes wrong.
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.ofPrimitiveArray(
    primitiveArray: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    val newList = when {
        type == BOOLEAN && primitiveArray is BooleanArray -> BooleanArrayList(primitiveArray).asPrimitiveArrayList()

        type == BYTE && primitiveArray is ByteArray -> ByteArrayList(primitiveArray).asPrimitiveArrayList()

        type == SHORT && primitiveArray is ShortArray -> ShortArrayList(primitiveArray).asPrimitiveArrayList()

        type == INT && primitiveArray is IntArray -> IntArrayList(primitiveArray).asPrimitiveArrayList()

        type == LONG && primitiveArray is LongArray -> LongArrayList(primitiveArray).asPrimitiveArrayList()

        type == FLOAT && primitiveArray is FloatArray -> FloatArrayList(primitiveArray).asPrimitiveArrayList()

        type == DOUBLE && primitiveArray is DoubleArray -> DoubleArrayList(primitiveArray).asPrimitiveArrayList()

        type == CHAR && primitiveArray is CharArray -> CharArrayList(primitiveArray).asPrimitiveArrayList()

//        type == UBYTE && primitiveArray is UByteArray -> primitiveArray.asList()
//
//        type == USHORT && primitiveArray is UShortArray -> primitiveArray.asList()
//
//        type == UINT && primitiveArray is UIntArray -> primitiveArray.asList()
//
//        type == ULONG && primitiveArray is ULongArray -> primitiveArray.asList()

        !primitiveArray.isPrimitiveArray -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from non primitive array $primitiveArray and type $type",
        )

        else -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from primitive array $primitiveArray and type $type",
        )
    } as MutableList<T>

    return ColumnDataHolderImpl(
        list = newList,
        distinct = distinct,
        zeroValue = zeroValueOf(type),
    )
}

@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    any: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    when {
        any is ColumnDataHolder<*> -> any as ColumnDataHolder<T>

        any.isPrimitiveArray -> ofPrimitiveArray(
            primitiveArray = any,
            type = type,
            distinct = distinct,
        )

        any.isArray -> ofBoxedArray(
            array = any as Array<T>,
            type = type,
            distinct = distinct,
        )

        any is Collection<*> -> ofCollection(
            collection = any as Collection<T>,
            type = type,
            distinct = distinct,
        )

        else -> throw IllegalArgumentException("Can't create ColumnDataHolder from $any and type $type")
    }

internal fun <T> ColumnDataHolder.Companion.empty(initCapacity: Int = 0): ColumnDataHolder<T> =
    ColumnDataHolderImpl(
        list = PrimitiveArrayList<Any>(initCapacity) as MutableList<T>,
    )

internal fun <T> ColumnDataHolder.Companion.emptyForType(
    type: KType,
    initCapacity: Int = 0,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    ColumnDataHolderImpl(
        list = PrimitiveArrayList.forTypeOrNull<Any>(
            kType = type.withNullability(false),
            initCapacity = initCapacity,
        ) as MutableList<T>? ?: mutableListOf(),
        distinct = distinct,
        zeroValue = zeroValueOf(type),
    )
