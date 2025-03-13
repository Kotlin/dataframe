package org.jetbrains.kotlinx.dataframe.annotations

/**
 * This annotation makes the element `internal` at compile-time using [Restrikt](https://github.com/ZwenDo/Restrikt/tree/master).
 *
 * It is used to hide an element from the public API but to allow access from friend modules without
 * the IDE highlighting it as an error.
 *
 * This can be removed once [KTIJ-31881](https://youtrack.jetbrains.com/issue/KTIJ-31881/Support-Xfriend-path-compiler-argument-in-the-Kotlin-plugin)
 * is fixed.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.FILE,
)
@Retention(AnnotationRetention.BINARY)
internal annotation class FriendlyInternal
