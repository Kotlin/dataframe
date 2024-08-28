package org.jetbrains.kotlinx.dataframe.columns

public open class LazyColumnDataHolder<T>(
    private var sequence: Sequence<T>,
    private val givenCount: Int? = null,
    distinct: Lazy<Set<T>>? = null,
) : ColumnDataHolder<T> {

    private var asList = sequence.asList()

    override fun toSet(): Set<T> = distinct.value

    override fun get(range: IntRange): Sequence<T> =
        sequence {
            for ((i, it) in sequence.withIndex()) {
                if (i > range.last) break
                if (i >= range.first) yield(it)
            }
        }

    override fun add(value: T) {
        sequence += value
        asList = sequence.toList()
        if (distinct.isInitialized()) {
            distinct.value as MutableSet<T> += value
        }
        additionalSize++
    }

    override fun get(index: Int): T = sequence.elementAt(index)

    override val distinct: Lazy<Set<T>> = distinct ?: lazy {
        buildSet {
            addAll(this@LazyColumnDataHolder)
        }.toMutableSet()
    }

    private val count: Int by lazy { givenCount ?: (sequence.count() - additionalSize) }
    private var additionalSize = 0

    override val size: Int
        get() = count + additionalSize

    override fun isEmpty(): Boolean = (givenCount == 0 && additionalSize == 0) || sequence.none()

    override fun iterator(): Iterator<T> = sequence.iterator()

    @Deprecated("Warning, heavy!")
    override fun listIterator(): ListIterator<T> = asList.listIterator()

    @Deprecated("Warning, heavy!")
    override fun listIterator(index: Int): ListIterator<T> = asList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = asList.subList(fromIndex, toIndex)

    override fun lastIndexOf(element: T): Int = sequence.indexOfLast { it == element }

    override fun indexOf(element: T): Int = sequence.indexOf(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        val elementsSet = elements.toMutableSet()
        for (it in sequence) {
            elementsSet.remove(it)
            if (elementsSet.isEmpty()) return true
        }
        return true
    }

    override fun contains(element: T): Boolean = sequence.contains(element)
}

/**
 * Note [sequence] must be traversable multiple times!
 */
@JvmInline
public value class SequenceAsList<T>(public val sequence: Sequence<T>) : List<T> {
    override val size: Int get() = sequence.count()

    override fun contains(element: T): Boolean = sequence.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        val elementsSet = elements.toMutableSet()
        for (it in sequence) {
            elementsSet.remove(it)
            if (elementsSet.isEmpty()) return true
        }
        return true
    }

    override fun get(index: Int): T = sequence.elementAt(index)

    override fun indexOf(element: T): Int = sequence.indexOf(element)

    override fun isEmpty(): Boolean = sequence.none()

    override fun iterator(): Iterator<T> = sequence.iterator()

    override fun lastIndexOf(element: T): Int = sequence.indexOfLast { it == element }

    @Deprecated("Warning, heavy!")
    override fun listIterator(): ListIterator<T> = sequence.toList().listIterator()

    @Deprecated("Warning, heavy!")
    override fun listIterator(index: Int): ListIterator<T> = sequence.toList().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        buildList(toIndex - fromIndex) {
            for ((i, it) in sequence.withIndex()) {
                if (i >= toIndex) break
                if (i >= fromIndex) add(it)
            }
        }
}

public fun <T> Sequence<T>.asList(): List<T> = SequenceAsList(this)

internal fun <T> ColumnDataHolder.Companion.ofSequence(
    sequence: Sequence<T>,
    size: Int? = null,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> = LazyColumnDataHolder(sequence, size, distinct)

public fun <T> Sequence<T>.toColumnDataHolder(size: Int? = null, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    ColumnDataHolder.ofSequence(this, size, distinct)
