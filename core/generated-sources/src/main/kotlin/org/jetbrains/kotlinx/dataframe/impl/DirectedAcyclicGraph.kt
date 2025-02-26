package org.jetbrains.kotlinx.dataframe.impl

import kotlin.experimental.ExperimentalTypeInference

/**
 * Represents a directed acyclic graph (DAG) of generic type [T].
 *
 * This class is immutable and guarantees that the graph does not contain any cycles.
 * It provides functionality to find the nearest common ancestor of two vertices
 * in the graph ([findNearestCommonVertex]).
 *
 * Use the [Builder] class or [buildDag] function to create a new instance of this class.
 *
 * @param T The type of items in the graph.
 * @property adjacencyList A map representing directed edges, where the keys are source vertices
 * and the values are sets of destination vertices.
 * @property vertices A set of all vertices in the graph.
 */
internal class DirectedAcyclicGraph<T> private constructor(
    private val adjacencyList: Map<T, Set<T>>,
    private val vertices: Set<T>,
) {
    class Builder<T> {
        private val edges = mutableListOf<Pair<T, T>>()
        private val vertices = mutableSetOf<T>()

        fun addEdge(from: T, to: T): Builder<T> {
            edges.add(from to to)
            vertices.add(from)
            vertices.add(to)
            return this
        }

        fun addEdges(vararg edges: Pair<T, T>): Builder<T> {
            edges.forEach { (from, to) -> addEdge(from, to) }
            return this
        }

        fun build(): DirectedAcyclicGraph<T> {
            val adjacencyList = edges.groupBy({ it.first }, { it.second })
                .mapValues { it.value.toSet() }

            if (hasCycle(adjacencyList)) {
                throw IllegalStateException("Graph contains cycle")
            }

            return DirectedAcyclicGraph(adjacencyList, vertices)
        }

        private fun hasCycle(adjacencyList: Map<T, Set<T>>): Boolean {
            val visited = mutableSetOf<T>()
            val recursionStack = mutableSetOf<T>()

            fun dfs(vertex: T): Boolean {
                if (vertex in recursionStack) return true
                if (vertex in visited) return false

                visited.add(vertex)
                recursionStack.add(vertex)

                adjacencyList[vertex]?.forEach { neighbor ->
                    if (dfs(neighbor)) return true
                }

                recursionStack.remove(vertex)
                return false
            }

            return adjacencyList.keys.any { vertex ->
                if (vertex !in visited && dfs(vertex)) return true
                false
            }
        }
    }

    fun findNearestCommonVertex(vertex1: T, vertex2: T): T? {
        if (vertex1 !in vertices || vertex2 !in vertices) return null
        if (vertex1 == vertex2) return vertex1

        // Get all ancestors for both vertices
        val ancestors1 = getAllAncestors(vertex1)
        val ancestors2 = getAllAncestors(vertex2)

        // If one vertex is an ancestor of another, return that vertex
        if (vertex1 in ancestors2) return vertex1
        if (vertex2 in ancestors1) return vertex2

        // Find common ancestors
        val commonAncestors = ancestors1.intersect(ancestors2)
        if (commonAncestors.isEmpty()) return null

        // Find the nearest common ancestor by checking distance from both vertices
        return commonAncestors.minByOrNull { ancestor ->
            getDistance(ancestor, vertex1) + getDistance(ancestor, vertex2)
        }
    }

    private fun getAllAncestors(vertex: T): Set<T> {
        val ancestors = mutableSetOf<T>()
        val visited = mutableSetOf<T>()

        fun dfs(current: T) {
            if (current in visited) return
            visited.add(current)

            adjacencyList.forEach { (parent, children) ->
                if (current in children) {
                    ancestors.add(parent)
                    dfs(parent)
                }
            }
        }

        dfs(vertex)
        return ancestors
    }

    private fun getDistance(from: T, to: T): Int {
        if (from == to) return 0

        val distances = mutableMapOf<T, Int>()
        val queue = ArrayDeque<T>()

        queue.add(from)
        distances[from] = 0

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentDistance = distances[current] ?: continue

            adjacencyList[current]?.forEach { neighbor ->
                if (neighbor !in distances) {
                    distances[neighbor] = currentDistance + 1
                    queue.add(neighbor)
                    if (neighbor == to) return currentDistance + 1
                }
            }
        }

        return Int.MAX_VALUE
    }

    fun <R> map(conversion: (T) -> R): DirectedAcyclicGraph<R> {
        val cache = mutableMapOf<T, R>()
        val cachedConversion: (T) -> R = { cache.getOrPut(it) { conversion(it) } }

        return Builder<R>().apply {
            for ((from, to) in adjacencyList) {
                for (to in to) {
                    addEdge(from = cachedConversion(from), to = cachedConversion(to))
                }
            }
        }.build()
    }

    companion object {
        fun <T> builder(): Builder<T> = Builder()
    }
}

/**
 * Builds a new [DirectedAcyclicGraph] using the provided [builder] function.
 *
 * @see DirectedAcyclicGraph
 */
@OptIn(ExperimentalTypeInference::class)
internal fun <T> buildDag(
    @BuilderInference builder: DirectedAcyclicGraph.Builder<T>.() -> Unit,
): DirectedAcyclicGraph<T> = DirectedAcyclicGraph.builder<T>().apply(builder).build()

/**
 * Builds a new [DirectedAcyclicGraph] using the provided [edges].
 *
 * @see DirectedAcyclicGraph
 */
internal fun <T> dagOf(vararg edges: Pair<T, T>): DirectedAcyclicGraph<T> = buildDag { addEdges(*edges) }
