package krangl.typed

import io.kotlintest.shouldBe
import org.junit.Test
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.superclasses

class UtilTests {

    @Test
    fun commonParentsTests(){
        commonParents(Int::class, Int::class) shouldBe listOf(Int::class)
        commonParents(Double::class, Int::class) shouldBe listOf(Number::class, Comparable::class)
        commonParents(Int::class, String::class) shouldBe listOf(Serializable::class, Comparable::class)
        commonParents(IllegalArgumentException::class, NotImplementedException::class) shouldBe listOf(RuntimeException::class)
    }

    @Test
    fun commonParentTests(){
        commonParent(Int::class, Int::class) shouldBe Int::class
        commonParent(Double::class, Int::class) shouldBe Number::class
        commonParent(Int::class, String::class) shouldBe Serializable::class
    }
}