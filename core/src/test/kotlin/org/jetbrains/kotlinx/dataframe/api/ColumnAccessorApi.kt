package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.junit.Test

/**
 * Pins the deprecation of `ColumnAccessor.nullable()`:
 *
 * - the `@Deprecated` annotation itself: the `DEPRECATED_ACCESS_API` message and `DeprecationLevel.ERROR`,
 * with no `ReplaceWith`. The Kotlin file-facade class is synthetic and cannot be named from Kotlin source,
 * so the annotation is reached through reflection (the `indices.kt` binary-compatibility precedent).
 *
 * - that the call never touched any data: it returns the same accessor object. A `by column()` delegate
 * property would create a fresh accessor on every read, so the accessor is built once.
 */
class ColumnAccessorApiTests {

    @Test
    fun `nullable is deprecated as Access API at ERROR level`() {
        val nullable = Class.forName("org.jetbrains.kotlinx.dataframe.api.ColumnAccessorApiKt")
            .getDeclaredMethod("nullable", ColumnAccessor::class.java)

        val deprecated = nullable.getAnnotation(Deprecated::class.java).shouldNotBeNull()
        deprecated.level shouldBe DeprecationLevel.ERROR
        deprecated.message shouldBe DEPRECATED_ACCESS_API
        deprecated.replaceWith.expression shouldBe ""
    }

    @Suppress("DEPRECATION_ERROR")
    @Test
    fun `nullable returns the same accessor without touching any data`() {
        val accessor = "score".toColumnOf<Double>()
        (accessor.nullable() === accessor) shouldBe true
    }
}
