package org.jetbrains.kotlinx.dataframe.documentation

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

/**
 * Any `Documentable` annotated with this annotation will be exported to HTML by the documentation
 * processor.
 *
 * You can use @exportAsHtmlStart and @exportAsHtmlEnd to specify a range of the doc to
 * export to HTML.
 *
 * **NOTE: DO NOT RENAME!**
 *
 * @param theme Whether to include a simple theme in the HTML file. Default is `true`.
 * @param stripReferences Whether to strip `[references]` from the HTML file. Default is `true`.
 *  This is useful when you want to include the HTML file in a website, where the references are not
 *  needed or would break.
 * @param includeResizeScript Whether to include a script that helps height recalculation inside iFrames.
 *  This is useful inside WriterSide. Default is `true`.
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
internal annotation class ExportAsHtml(
    val theme: Boolean = true,
    val stripReferences: Boolean = true,
    val includeResizeScript: Boolean = true,
)
