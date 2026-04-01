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

///**
// * Merges two maps of Iterables, appending the values from the lists in the other map
// * to the lists in the first map.
// */
//internal infix fun <K, V> Map<K, Iterable<V>>.merge(other: Map<K, Iterable<V>>): Map<K, List<V>> =
//    buildMap<K, MutableList<V>> {
//        this@merge.forEach { (key, values) ->
//            this[key] = values.toMutableList()
//        }
//        other.forEach { (key, values) ->
//            this.getOrPut(key) { mutableListOf() } += values
//        }
//    }
//
///**
// * Same as [Map.merge], however, it retains the laziness of the values in the first map.
// */
//internal infix fun <K, V> LazyMap<K, Iterable<V>>.merge(other: Map<K, Iterable<V>>): LazyMap<K, List<V>> =
//    LazyMap(
//        buildMap<K, Lazy<List<V>>> {
//            this@merge.lazyMap.forEach { (key, values) ->
//                this[key] = lazy { values.value.toList() }
//            }
//            other.forEach { (key, values) ->
//                if (key in this) {
//                    this[key] = this[key]!! + values
//                } else {
//                    this[key] = lazy { values.toList() }
//                }
//            }
//        },
//    )
//
///**
// * Same as [Map.merge], however, it retains the laziness of the values in the second map.
// */
//internal infix fun <K, V> Map<K, Iterable<V>>.merge(other: LazyMap<K, Iterable<V>>): LazyMap<K, List<V>> =
//    LazyMap(
//        buildMap<K, Lazy<List<V>>> {
//            this@merge.forEach { (key, values) ->
//                this[key] = lazy { values.toList() }
//            }
//            other.lazyMap.forEach { (key, values) ->
//                if (key in this) {
//                    this[key] = this[key]!! + values
//                } else {
//                    this[key] = lazy { values.value.toList() }
//                }
//            }
//        },
//    )
//
///**
// * Same as [Map.merge], however, it retains the laziness of the values in both maps.
// */
//internal infix fun <K, V> LazyMap<K, Iterable<V>>.merge(other: LazyMap<K, Iterable<V>>): LazyMap<K, List<V>> =
//    LazyMap(
//        buildMap<K, Lazy<List<V>>> {
//            this@merge.lazyMap.forEach { (key, values) ->
//                this[key] = lazy { values.value.toList() }
//            }
//            other.lazyMap.forEach { (key, values) ->
//                if (key in this) {
//                    this[key] = this[key]!! + values
//                } else {
//                    this[key] = lazy { values.value.toList() }
//                }
//            }
//        },
//    )
//
//internal operator fun <T> Lazy<Iterable<T>>.plus(other: Lazy<Iterable<T>>): Lazy<List<T>> =
//    lazy { this.value + other.value }
//
//internal operator fun <T> Lazy<Iterable<T>>.plus(other: Iterable<T>): Lazy<List<T>> = lazy { this.value + other }
//
//internal operator fun <T> Iterable<T>.plus(other: Lazy<Iterable<T>>): Lazy<List<T>> = lazy { this + other.value }
