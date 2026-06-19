package org.jetbrains.kotlinx.dataframe.io.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FILE
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.annotation.AnnotationTarget.TYPEALIAS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

// TODO use KDoc-helpers from :core

/**
 * ## Auto-renaming columns in [DataFrame]
 *
 * [DataFrame] can not contain columns with duplicate names.
 * However, sometimes after reading dataframes from sources or
 * after some operations, columns with duplicate names may appear in the result.
 *
 * In such cases, columns with duplicate names are automatically renamed in the resulting [DataFrame]
 * using the pattern `"\$name\$n"`, where `name` is the original column name
 * and `n` is a unique index (1, 2, 3, and so on);
 * the first time the name of the column is encountered, no number is appended.
 *
 * It is recommended to [rename][org.jetbrains.kotlinx.dataframe.api.rename] them
 * to maintain clarity and improve code readability.
 */
internal typealias AutoRenamingColumnsInDataFrame = Nothing

/**
 * Note that if input dataframe contains duplicate column names,
 * they will be [automatically renamed][AutoRenamingColumnsInDataFrame]
 * in the resulting [DataFrame].
 */
@ExcludeFromSources
internal typealias AutoRenameInputSnippet = Nothing

/**
 * Any `Documentable` annotated with this annotation will be excluded from the generated sources by
 * the documentation processor.
 *
 * **NOTE: DO NOT RENAME!**
 */
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPE,
    TYPEALIAS,
    FILE,
)
internal annotation class ExcludeFromSources
