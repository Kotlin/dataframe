package org.jetbrains.kotlinx.dataframe.impl

/**
 * Map that resolves values lazily.
 */
internal class LazyMap<K, out V>(private val actualMap: Map<K, Lazy<V>>) : Map<K, V> {

    // Operations that traverse all values will resolve all values anyway
    private val resolvedMap = lazy {
        actualMap.mapValues { it.value.value }
    }

    override val size: Int get() = actualMap.size
    override val keys: Set<K> get() = actualMap.keys

    // resolves all values!
    override val values: Collection<V> get() = resolvedMap.value.values

    // resolves all values!
    override val entries: Set<Map.Entry<K, V>>
        get() = resolvedMap.value.entries

    override fun isEmpty(): Boolean = actualMap.isEmpty()

    override fun containsKey(key: K): Boolean = actualMap.containsKey(key)

    // resolves as little values as possible
    override fun containsValue(value: @UnsafeVariance V): Boolean {
        if (resolvedMap.isInitialized()) {
            return resolvedMap.value.containsValue(value)
        }
        for (key in keys) {
            if (get(key) == value) return true
        }
        return false
    }

    override fun get(key: K): V? =
        if (resolvedMap.isInitialized()) {
            resolvedMap.value[key]
        } else {
            actualMap[key]?.value
        }
}

/**
 * Creates a [Map] that resolves values lazily.
 */
internal fun <K, V> lazyMapOf(vararg entries: Pair<K, () -> V>): LazyMap<K, V> =
    LazyMap(mapOf(*entries).mapValues { lazy(it.value) })
