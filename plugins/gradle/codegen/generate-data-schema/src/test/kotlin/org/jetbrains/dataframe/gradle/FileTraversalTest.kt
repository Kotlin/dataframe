package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class FileTraversalTest {

    lateinit var temp: File
    @Before
    fun init() {
        temp = Files.createTempDirectory("temp").toFile()
    }

    @Test
    fun directoryWithFileIsNotMiddlePackage() {
        val leaf = File(temp, "a/b/c").also { it.mkdirs() }
        val file = File(leaf, "test.txt").also { it.createNewFile() }
        leaf.isMiddlePackage() shouldBe false
        file.isMiddlePackage() shouldBe false
        listOf(leaf, file).filterNot { it.isMiddlePackage() }.first() shouldBe leaf
    }

    @Test
    fun emptySubdirectory() {
        val leaf = File(temp, "a/b/c").also { it.mkdirs() }
        temp.findDeepestCommonSubdirectory() shouldBe leaf
    }

    @Test
    fun subdirectoryWithFile() {
        val leaf = File(temp, "a/b/c").also { it.mkdirs() }
        File(leaf, "test.txt").also { it.createNewFile() }
        temp.findDeepestCommonSubdirectory() shouldBe leaf
    }


    @Test
    fun forkAtDepth0() {
        File(temp, "a/b/c").also { it.mkdirs() }
        File(temp, "b/c/d").also { it.mkdirs() }
        temp.findDeepestCommonSubdirectory() shouldBe temp
    }

    @Test
    fun forkAtDepth1() {
        val a = File(temp, "a").also { it.mkdirs() }
        File(a, "b/c").also { it.mkdirs() }
        File(a, "c/d").also { it.mkdirs() }
        temp.findDeepestCommonSubdirectory() shouldBe a
    }


    @Test
    fun noSubdirectories() {
        temp.findDeepestCommonSubdirectory() shouldBe temp
    }

}
