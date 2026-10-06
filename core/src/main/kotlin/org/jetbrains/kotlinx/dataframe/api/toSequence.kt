package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.toSequenceImpl
import kotlin.reflect.typeOf

/**
 * The columns are matched and converted when this function is called,
 * so the exceptions listed below are thrown right away.
 * The [T\] instances are created only when the sequence is iterated,
 * one per row that is read, and each iteration creates them again.
 */
@ExcludeFromSources
private typealias ToSequenceIterationDocs = Nothing

// region DataFrame

/**
 * Converts this [DataFrame] into a [Sequence] of [T] instances,
 * where [T] is the type argument of this [DataFrame].
 *
 * @include [CommonToListDocs.FromTypeArgument]
 *
 * @include [ToSequenceIterationDocs]
 *
 * @include [CommonToListDocs]
 *
 * ### Example
 * ```kotlin
 * data class Input(val a: Int, val b: Int)
 *
 * val df = dataFrameOf("a", "b")(1, 2, 3, 4)
 *
 * // Only the first row becomes an `Input`
 * df.cast<Input>().toSequence().first() // Input(a=1, b=2)
 * ```
 *
 * @param [T] The type argument of this [DataFrame]. It has to be a data class.
 * @return A [Sequence] with one [T] instance per row of this [DataFrame].
 * @set [CommonToListDocs.RESULT] sequence
 * {@set [CommonToListDocs.URL] {@include [DocumentationUrls.ToSequence]}}
 * @set [CommonToListDocs.OF_FUNCTION] [toSequenceOf][DataFrame.toSequenceOf]
 * @set [CommonToListDocs.SIBLINGS] [toList][DataFrame.toList] and [toListOf][DataFrame.toListOf] —
 * the same conversion into a [List].
 */
public inline fun <reified T> DataFrame<T>.toSequence(): Sequence<T> = toSequenceImpl(typeOf<T>()) as Sequence<T>

/**
 * Converts this [DataFrame] into a [Sequence] of [T] instances,
 * where [T] is given explicitly as the type argument of this call.
 *
 * @include [CommonToListDocs.FromExplicitType]
 *
 * @include [ToSequenceIterationDocs]
 *
 * @include [CommonToListDocs]
 *
 * ### Example
 * ```kotlin
 * val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
 *     .group("name", "lastName").into("fullName")
 *
 * data class FullName(val name: String, val lastName: String)
 *
 * data class Person(val fullName: FullName, val age: Int)
 *
 * // The "fullName" column group becomes a nested `FullName`
 * df.toSequenceOf<Person>().first() // Person(fullName=FullName(name=John, lastName=Doe), age=21)
 * ```
 *
 * @param [T] The data class to create for each row.
 * @return A [Sequence] with one [T] instance per row of this [DataFrame].
 * @set [CommonToListDocs.RESULT] sequence
 * {@set [CommonToListDocs.URL] {@include [DocumentationUrls.ToSequence]}}
 * @set [CommonToListDocs.SIBLINGS] [toList][DataFrame.toList] and [toListOf][DataFrame.toListOf] —
 * the same conversion into a [List].
 */
public inline fun <reified T> DataFrame<*>.toSequenceOf(): Sequence<T> = toSequenceImpl(typeOf<T>()) as Sequence<T>

// endregion
