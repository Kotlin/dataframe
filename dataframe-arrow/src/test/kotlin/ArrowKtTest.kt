import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.junit.Test
import java.net.URL

internal class ArrowKtTest {

    fun testResource(resourcePath: String): URL = ArrowKtTest::class.java.classLoader.getResource(resourcePath)!!

    fun testArrowFeather(name: String) = testResource("$name.feather")

    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = DataFrame.readArrowFeather(feather)
        val a by columnOf("one")
        val b by columnOf(2.0)
        val c by listOf(
            mapOf(
                "c1" to Text("inner"),
                "c2" to 4.0,
                "c3" to 50.0
            ) as Map<String, Any?>
        ).toColumn()
        val d by columnOf("four")
        val expected = dataFrameOf(a, b, c, d)
        df shouldBe expected
    }
}
