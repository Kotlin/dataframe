package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.booleans.shouldBeTrue
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.last
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test
import java.io.File
import java.io.InputStream

class OpenApiTests : JupyterReplTestCase() {

    private val openApi = OpenApi()
    private val additionalImports = openApi.createDefaultReadMethod().additionalImports.joinToString("\n")

    private fun execGeneratedCode(code: CodeWithConverter) {
        @Language("kts")
        val res1 = exec(
            """
            $additionalImports
            ${code.declarations}
            """.trimIndent()
        )
    }

    private fun execGeneratedCode(file: File) = execGeneratedCode(code = openApi.readCodeForGeneration(file))
    private fun execGeneratedCode(stream: InputStream) = execGeneratedCode(code = openApi.readCodeForGeneration(stream))
    private fun execGeneratedCode(text: String) = execGeneratedCode(code = openApi.readCodeForGeneration(text))

    // TODO
    private val advancedExample = File("src/test/resources/openapi_advanced_example.yaml")
    private val petstoreJson = File("src/test/resources/petstore.json")
    private val petstoreAdvancedJson = File("src/test/resources/petstore_advanced.json")
    private val petstoreYaml = File("src/test/resources/petstore.yaml")
    private val someAdvancedPets = File("src/test/resources/some_advanced_pets.json").readText()
    private val someAdvancedOrders = File("src/test/resources/some_advanced_orders.json").readText()
    private val someAdvancedFailingOrders = File("src/test/resources/some_advanced_failing_orders.json").readText()
    private val advancedData = File("src/test/resources/openapi_advanced_data.json").readText()

    @Language("json")
    private val somePets = """
        [
            {
              "id": 0,
              "name": "doggie",
              "tag": "Dogs"
            },
            {
              "id": 1,
              "name": "kitty",
              "tag": "Cats"
            },
            {
              "id": 2,
              "name": "puppy"
            }
        ]
    """.trimIndent()

    private val somePetsTripleQuotes = "\"\"\"$somePets\"\"\""

    object SimpleTestPetstore {
        @DataSchema(isOpen = false)
        interface Pet {
            val id: Long
            val name: String
            val tag: String?

            object SAMPLE : Pet {
                override val id = 0L
                override val name = "doggie"
                override val tag = "Dogs"
            }
        }
    }

    @Test
    fun `Simple test Petstore Json`() {
        execGeneratedCode(petstoreJson)

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame

        val res3 = res2.cast<SimpleTestPetstore.Pet>(verify = true)
        res3.schema().equalsByNames(
            other = listOf(SimpleTestPetstore.Pet.SAMPLE).toDataFrame().schema(),
            ignoreNullability = true,
        ).shouldBeTrue()
    }

    @Test
    fun `Simple test Petstore Yaml`() {
        execGeneratedCode(petstoreYaml)

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame

        val res3 = res2.cast<SimpleTestPetstore.Pet>(verify = true)
        res3.schema().equalsByNames(
            other = listOf(SimpleTestPetstore.Pet.SAMPLE).toDataFrame().schema(),
            ignoreNullability = true,
        ).shouldBeTrue()
    }

    //region Advanced test Petstore
    object AdvancedTestPetstore {
        enum class Status {
            placed,
            approved,
            delivered;
        }

        @DataSchema(isOpen = false)
        interface Order {
            val id: kotlin.Long?
            val petId: kotlin.Long?
            val quantity: kotlin.Int?
            val shipDate: kotlinx.datetime.LocalDateTime?
            val status: Status
            val complete: kotlin.Boolean?

            companion object {
                val SAMPLE = listOf(
                    object : Order {
                        override val id = null
                        override val petId = null
                        override val quantity = null
                        override val shipDate = null
                        override val status = Status.placed
                        override val complete = null
                    },
                    object : Order {
                        override val id = 0L
                        override val petId = 0L
                        override val quantity = 0
                        override val shipDate = kotlinx.datetime.LocalDateTime.parse("2021-01-01T00:00:00")
                        override val status = Status.approved
                        override val complete = true
                    }
                ).toDataFrame()
            }
        }

        @DataSchema(isOpen = false)
        interface Address {
            val street: kotlin.String?
            val city: kotlin.String?
            val state: kotlin.String?
            val zip: kotlin.String?
        }

        @DataSchema(isOpen = false)
        interface Customer {
            val id: kotlin.Long?
            val username: kotlin.String?
            val address: org.jetbrains.kotlinx.dataframe.DataFrame<Address>?
        }

        @DataSchema(isOpen = false)
        interface Category {
            val id: kotlin.Long?
            val name: kotlin.String?

            companion object {
                val SAMPLE = listOf(
                    object : Category {
                        override val id = 0L
                        override val name = "Dog"
                    },
                    object : Category {
                        override val id = null
                        override val name = null
                    },
                ).toDataFrame()
            }
        }

        @DataSchema(isOpen = false)
        interface User {
            val id: kotlin.Long?
            val username: kotlin.String?
            val firstName: kotlin.String?
            val lastName: kotlin.String?
            val email: kotlin.String?
            val password: kotlin.String?
            val phone: kotlin.String?
            val userStatus: kotlin.Int?
        }

        @DataSchema(isOpen = false)
        interface Tag {
            val id: kotlin.Long?
            val name: kotlin.String?

            companion object {
                val SAMPLE = listOf(
                    object : Tag {
                        override val id = 0L
                        override val name = "Tag name"
                    },
                    object : Tag {
                        override val id = null
                        override val name = null
                    },
                ).toDataFrame()
            }
        }

        enum class Status1 {
            available,
            pending,
            sold;
        }

        @DataSchema(isOpen = false)
        interface Pet {
            val id: kotlin.Long?
            val name: kotlin.String
            val category: org.jetbrains.kotlinx.dataframe.DataRow<Category>
            val photoUrls: kotlin.collections.List<kotlin.String>
            val tags: org.jetbrains.kotlinx.dataframe.DataFrame<Tag>?
            val status: Status1

            companion object {
                val SAMPLE = listOf(
                    object : Pet {
                        override val id = null
                        override val name = "Toby"
                        override val category = Category.SAMPLE.first()
                        override val photoUrls = listOf("sample.com")
                        override val tags = Tag.SAMPLE
                        override val status = Status1.available
                    },
                    object : Pet {
                        override val id = 0L
                        override val name = "Toby"
                        override val category = Category.SAMPLE.last()
                        override val photoUrls = listOf("sample.com")
                        override val tags = Tag.SAMPLE
                        override val status = Status1.available
                    },
                ).toDataFrame()
            }
        }

        @DataSchema(isOpen = false)
        interface ApiResponse {
            val code: kotlin.Int?
            val type: kotlin.String?
            val message: kotlin.String?
        }
    }
    //endregion

    @Test
    fun `Advanced test Petstore Json`() {
        execGeneratedCode(petstoreAdvancedJson)

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr(\"\"\"$someAdvancedPets\"\"\")") as AnyFrame
        val res2Schema = res2.schema()
        val verifySchema2 = AdvancedTestPetstore.Pet.SAMPLE.schema()
        res2Schema.equalsByNames(
            other = verifySchema2,
            ignoreNullability = false,
        ).shouldBeTrue()

        @Language("kts")
        val res3 = execRaw("Order.readJsonStr(\"\"\"$someAdvancedOrders\"\"\")") as AnyFrame
        val res3Schema = res3.schema()
        val verifySchema3 = AdvancedTestPetstore.Order.SAMPLE.schema()
        res3Schema.equalsByNames(
            other = verifySchema3,
            ignoreNullability = false,
        )

        shouldThrowAny {
            @Language("kts")
            val res4 = execRaw("Order.readJsonStr(\"\"\"$someAdvancedFailingOrders\"\"\")") as AnyFrame
            res4
        }
    }

    object OtherAdvancedTest {

        enum class EyeColor {
            Blue,
            Yellow,
            Brown,
            Green;
        }

        @DataSchema(isOpen = false)
        interface Pet {
            @ColumnName("pet_type")
            val petType: kotlin.String
            val id: kotlin.Any
            val name: kotlin.String
            val tag: kotlin.String?
            val other: kotlin.Any?

            @ColumnName("eye_color")
            val eyeColor: EyeColor?

            companion object {
                val SAMPLE = listOf(
                    Dog.SAMPLE,
                    Cat.SAMPLE,
                ).flatten()
            }
        }

        enum class Breed {
            Dingo,
            Husky,
            Retriever,
            Shepherd;
        }

        @DataSchema(isOpen = false)
        interface Dog : Pet {
            override val tag: kotlin.String
            val bark: kotlin.Boolean?
            val breed: Breed

            companion object {
                val SAMPLE = listOf(
                    object : Dog {
                        override val id = 0L
                        override val name = "Toby"
                        override val other = null
                        override val eyeColor = EyeColor.Blue
                        override val tag = "Tag"
                        override val bark = true
                        override val breed = Breed.Dingo
                        override val petType = "Dog"
                    },
                    object : Dog {
                        override val tag = "Tag"
                        override val bark = null
                        override val breed = Breed.Dingo
                        override val id = "0"
                        override val name = "Toby"
                        override val other = listOf<Any>()
                        override val eyeColor = null
                        override val petType = "Dog"
                    },
                    object : Dog {
                        override val id = "2325"
                        override val name = "dsdgsd"
                        override val other = 234
                        override val eyeColor = null
                        override val tag = "Tag"
                        override val bark = false
                        override val breed = Breed.Dingo
                        override val petType = "Dog"
                    },
                )
            }
        }

        enum class Breed1 {
            Ragdoll,
            Shorthair,
            Persian,
            `Maine Coon`;
        }

        @DataSchema(isOpen = false)
        interface Cat : Pet {
            val hunts: kotlin.Boolean?
            val age: kotlin.Float?
            val breed: Breed1?

            companion object {
                val SAMPLE = listOf(
                    object : Cat {
                        override val id = 0L
                        override val name = "Toby"
                        override val tag = "Tag"
                        override val other = null
                        override val eyeColor = EyeColor.Blue
                        override val hunts = true
                        override val age = 1f
                        override val breed = null
                        override val petType = "Cat"
                    },
                    object : Cat {
                        override val petType = "Cat"
                        override val id = "0"
                        override val name = "Toby"
                        override val tag = null
                        override val other = listOf<Any>()
                        override val eyeColor = null
                        override val hunts = null
                        override val age = null
                        override val breed = Breed1.Ragdoll
                    },
                    object : Cat {
                        override val petType = "Cat"
                        override val hunts = null
                        override val age = 123.534645f
                        override val breed = Breed1.Ragdoll
                        override val id = "2325"
                        override val name = "dsdgsd"
                        override val tag = null
                        override val other = 234
                        override val eyeColor = null
                    },
                )
            }
        }

        @DataSchema(isOpen = false)
        interface Error {
            val code: kotlin.Int
            val message: kotlin.String
        }
    }

    @Test
    fun `Other advanced test`() {
        execGeneratedCode(advancedExample)

        @Language("kts")
        val res1 = execRaw(
            "Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Cat\" }.convertTo<Cat>(ExcessiveColumns.Remove)"
        ) as AnyFrame
        val res1Schema = res1.schema()
        val verifySchema1 = OtherAdvancedTest.Cat.SAMPLE.toDataFrame().schema()

        res1Schema.equalsByNames(
            other = verifySchema1,
            ignoreNullability = false,
        ).shouldBeTrue()

        @Language("kts")
        val res2 = execRaw(
            "Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Dog\" }.convertTo<Dog>(ExcessiveColumns.Remove)"
        ) as AnyFrame
        val res2Schema = res2.schema()
        val verifySchema2 = OtherAdvancedTest.Dog.SAMPLE.toDataFrame().schema()

        res2Schema.equalsByNames(
            other = verifySchema2,
            ignoreNullability = false,
        ).shouldBeTrue()
    }
}

private typealias Pets = List<OpenApiTests.OtherAdvancedTest.Pet>
private typealias AlsoCat = OpenApiTests.OtherAdvancedTest.Cat
private typealias Integer = kotlin.Int

// checks equality of dataframe schemas only by name and Type-name, not exact types
internal fun DataFrameSchema.equalsByNames(
    other: DataFrameSchema,
    ignoreNullability: Boolean,
): Boolean {
    val res = columns.entries.size == other.columns.entries.size &&
        columns.entries.all { (name, columnSchema) ->
            val otherSchema = other.columns[name]
                ?: return@all run {
                    println("Column $name is not found in other schema")
                    false
                }
            if (columnSchema.kind != otherSchema.kind) return@all run {
                println("Column $name has different kinds: ${columnSchema.kind} and ${otherSchema.kind}")
                false
            }

            when (columnSchema) {
                is ColumnSchema.Group ->
                    columnSchema.schema.equalsByNames((otherSchema as ColumnSchema.Group).schema, ignoreNullability)

                is ColumnSchema.Frame ->
                    columnSchema.schema.equalsByNames((otherSchema as ColumnSchema.Frame).schema, ignoreNullability)

                is ColumnSchema.Value -> {
                    val type = columnSchema.type.toString().substringAfterLast(".")
                        .let { if (ignoreNullability) it.removeSuffix("?") else it }

                    val otherType = otherSchema.type.toString().substringAfterLast(".")
                        .let { if (ignoreNullability) it.removeSuffix("?") else it }

                    if (type != otherType) println("Column $name has different types: $type and $otherType")

                    type == otherType
                }

                else -> throw NotImplementedError(columnSchema::class.toString())
            }
        }

    if (!res) {
        println("Difference in schemas: \n$this\n\nand\n\n$other")
    }

    return res
}
