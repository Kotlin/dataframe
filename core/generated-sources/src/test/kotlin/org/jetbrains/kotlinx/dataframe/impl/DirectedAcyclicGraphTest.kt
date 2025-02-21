package org.jetbrains.kotlinx.dataframe.impl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.Test

class DirectedAcyclicGraphTest {

    @Test
    fun `basic graph building`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("B", "C")
        }

        graph.findNearestCommonVertex("B", "C") shouldBe "B"
        graph.findNearestCommonVertex("A", "C") shouldBe "A"
    }

    @Test
    fun `cycle detection`() {
        shouldThrow<IllegalStateException> {
            buildDag {
                addEdge("A", "B")
                addEdge("B", "C")
                addEdge("C", "A")
            }
        }
    }

    @Test
    fun `nearest common vertex - same vertex`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("B", "C")
        }

        graph.findNearestCommonVertex("B", "B") shouldBe "B"
    }

    @Test
    fun `nearest common vertex - one is ancestor`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("B", "C")
            addEdge("B", "D")
        }

        graph.findNearestCommonVertex("B", "D") shouldBe "B"
        graph.findNearestCommonVertex("D", "B") shouldBe "B"
    }

    @Test
    fun `nearest common vertex - common ancestor exists`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("A", "C")
            addEdge("B", "D")
            addEdge("C", "E")
        }

        graph.findNearestCommonVertex("D", "E") shouldBe "A"
    }

    @Test
    fun `nearest common vertex - no common ancestor`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("C", "D")
        }

        graph.findNearestCommonVertex("B", "D").shouldBeNull()
    }

    @Test
    fun `nearest common vertex - complex case`() {
        val graph = buildDag {
            addEdge("A", "B")
            addEdge("B", "C")
            addEdge("A", "D")
            addEdge("D", "E")
            addEdge("B", "E")
        }

        // B is closer to E than A
        graph.findNearestCommonVertex("C", "E") shouldBe "B"
    }
}
