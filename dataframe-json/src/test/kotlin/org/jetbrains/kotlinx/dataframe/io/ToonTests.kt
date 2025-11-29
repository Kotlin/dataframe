package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.impl.io.ToonArray
import org.jetbrains.kotlinx.dataframe.impl.io.ToonDelimiter
import org.jetbrains.kotlinx.dataframe.impl.io.ToonObject
import org.jetbrains.kotlinx.dataframe.impl.io.ToonPrimitive
import org.jetbrains.kotlinx.dataframe.impl.io.encodeToToonImpl
import org.jetbrains.kotlinx.dataframe.impl.io.render
import org.junit.jupiter.api.Test

class ToonTests {

    /**
     * ```json
     * {
     *   "context": {
     *     "task": "Our favorite hikes together",
     *     "location": "Boulder",
     *     "season": "spring_2025"
     *   },
     *   "friends": ["ana", "luis", "sam"],
     *   "friends2": ["ana", "luis", ["sam"], {"a": 1, "b": 3}, [{
     *   "id": 1,
     *   "name": "Blue Lake Trail",
     *   "distanceKm": 7.5,
     *   "elevationGain": 320,
     *   "companion": "ana",
     *   "wasSunny": true
     * },
     * {
     *   "id": 2,
     *   "name": "Ridge Overlook",
     *   "distanceKm": 9.2,
     *   "elevationGain": 540,
     *   "companion": "luis",
     *   "wasSunny": false
     * },
     * {
     *   "id": 3,
     *   "name": "Wildflower Loop",
     *   "distanceKm": 5.1,
     *   "elevationGain": 180,
     *   "companion": "sam",
     *   "wasSunny": true
     * }]],
     *   "hikes": [
     *     {
     *       "id": 1,
     *       "name": "Blue Lake Trail",
     *       "distanceKm": 7.5,
     *       "elevationGain": 320,
     *       "companion": "ana",
     *       "wasSunny": true
     *     },
     *     {
     *       "id": 2,
     *       "name": "Ridge Overlook",
     *       "distanceKm": 9.2,
     *       "elevationGain": 540,
     *       "companion": "luis",
     *       "wasSunny": false
     *     },
     *     {
     *       "id": 3,
     *       "name": "Wildflower Loop",
     *       "distanceKm": 5.1,
     *       "elevationGain": 180,
     *       "companion": "sam",
     *       "wasSunny": true
     *     }
     *   ]
     * }
     * ```
     */
    @Test
    fun `Some big JSON`() {
        val obj = ToonObject(
            mapOf(
                "context" to ToonObject(
                    mapOf(
                        "task" to ToonPrimitive("Our favorite hikes together"),
                        "location" to ToonPrimitive("Boulder"),
                        "season" to ToonPrimitive("spring_2025"),
                    ),
                ),
                "friends" to ToonArray(listOf(ToonPrimitive("ana"), ToonPrimitive("luis"), ToonPrimitive("sam"))),
                "friends2" to ToonArray(
                    listOf(
                        ToonPrimitive("ana"),
                        ToonPrimitive("luis"),
                        ToonArray(listOf(ToonPrimitive("sam"))),
                        ToonObject(mapOf("a" to ToonPrimitive(1), "b" to ToonPrimitive(3))),
                        ToonArray(
                            listOf(
                                ToonObject(
                                    mapOf(
                                        "test" to ToonPrimitive(1),
                                        "nested" to ToonPrimitive(3),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                "hikes" to ToonArray(
                    listOf(
                        ToonObject(
                            mapOf(
                                "id" to ToonPrimitive(1),
                                "name" to ToonPrimitive("Blue Lake Trail"),
                                "distanceKm" to ToonPrimitive(7.5),
                                "elevationGain" to ToonPrimitive(320),
                                "companion" to ToonPrimitive("ana"),
                                "wasSunny" to ToonPrimitive(true),
                            ),
                        ),
                        ToonObject(
                            mapOf(
                                "id" to ToonPrimitive(2),
                                "name" to ToonPrimitive("Ridge Overlook"),
                                "distanceKm" to ToonPrimitive(9.2),
                                "elevationGain" to ToonPrimitive(540),
                                "companion" to ToonPrimitive("luis"),
                                "wasSunny" to ToonPrimitive(false),
                            ),
                        ),
                        ToonObject(
                            mapOf(
                                "id" to ToonPrimitive(3),
                                "name" to ToonPrimitive("Wildflower Loop"),
                                "distanceKm" to ToonPrimitive(5.1),
                                "elevationGain" to ToonPrimitive(180),
                                "companion" to ToonPrimitive("sam"),
                                "wasSunny" to ToonPrimitive(true),
                            ),
                        ),
                    ),
                ),
            ),
        )

        println(
            obj.render(),
        )
    }

    @Test
    fun `other TOON encoders fail not mine`() {
        // [
        //   1,
        //   [1],
        //   { "a": 1 },
        //   [{ "b": 2 }],
        //   { "c": [{ "d": 3 }, { "d": 4 }], "d": 7 }
        // ]
        ToonArray(
            listOf(
                ToonPrimitive(1),
                ToonArray(listOf(ToonPrimitive(1))),
                ToonObject(mapOf("a" to ToonPrimitive(1))),
                ToonArray(listOf(ToonObject(mapOf("b" to ToonPrimitive(2))))),
                ToonObject(
                    mapOf(
                        "c" to ToonArray(
                            listOf(
                                ToonObject(mapOf("d" to ToonPrimitive(3), "e" to ToonPrimitive(5))),
                                ToonObject(mapOf("d" to ToonPrimitive(4), "e" to ToonPrimitive(6))),
                            ),
                        ),
                        "d" to ToonPrimitive(7),
                    ),
                ),
            ),
        ).let {
            println(it.render())
        }
    }

    @Test
    fun `flat df`() {
        @Suppress("ktlint:standard:argument-list-wrapping")
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "123", "Wolf", 20, null, 55, false,
            "Cha:rlie", "Byrd", 30, "Moscow", 90, true,
        )

        println(
            encodeToToonImpl(df).render(delimiter = ToonDelimiter.PIPE),
        )
    }

    @Test
    fun `grouped df`() {
        @Suppress("ktlint:standard:argument-list-wrapping")
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, null, 55, false,
            "Charlie", "Byrd", 30, "Moscow", 90, true,
        ).group("firstName", "lastName").into("name")

        println(
            encodeToToonImpl(df).render(),
        )
    }

    @Test
    fun `nested df`() {
        @Suppress("ktlint:standard:argument-list-wrapping")
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, null, 55, false,
        ).groupBy("city").toDataFrame()

        println(
            encodeToToonImpl(df).render(),
        )
    }
}
