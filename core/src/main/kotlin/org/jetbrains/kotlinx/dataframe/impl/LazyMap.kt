package org.jetbrains.kotlinx.dataframe.impl

import kotlin.collections.plusAssign

/**
 * Map that resolves values lazily.
 */
internal class LazyMap<K, out V>(val lazyMap: Map<K, Lazy<V>>) : Map<K, V> {

    // Operations that traverse all values will resolve all values anyway
    private val resolvedMap = lazy {
        lazyMap.mapValues { it.value.value }
    }

    override val size: Int get() = lazyMap.size
    override val keys: Set<K> get() = lazyMap.keys

    // resolves all values!
    override val values: Collection<V> get() = resolvedMap.value.values

    // resolves all values!
    override val entries: Set<Map.Entry<K, V>>
        get() = resolvedMap.value.entries

    override fun isEmpty(): Boolean = lazyMap.isEmpty()

    override fun containsKey(key: K): Boolean = lazyMap.containsKey(key)

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
            lazyMap[key]?.value
        }
}

/**
 * Creates a [Map] that resolves values lazily.
 */
internal fun <K, V> lazyMapOf(vararg entries: Pair<K, () -> V>): LazyMap<K, V> =
    LazyMap(mapOf(*entries).mapValues { lazy(it.value) })
