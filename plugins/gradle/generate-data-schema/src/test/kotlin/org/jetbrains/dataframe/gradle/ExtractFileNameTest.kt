package org.jetbrains.dataframe.gradle

import org.junit.Test
import java.io.File
import java.net.URL

class ExtractFileNameTest {
    @Test
    fun `1`() {
        val name = extractFileName("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
        assert(name == "playlistItems")
    }

    @Test
    fun `2`() {
        val name = extractFileName(URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json"))
        assert(name == "playlistItems")
    }

    @Test
    fun `3`() {
        val name = extractFileName("/etc/example/file.json")
        assert(name == "file")
    }

    @Test
    fun `4`() {
        val name = extractFileName(File("/etc/example/file.json"))
        assert(name == "file")
    }

    @Test
    fun `5`() {
        val name = extractFileName("abc")
        assert(name == "abc")
    }
}
