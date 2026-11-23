@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

/** {@comment Shared KDoc fragments for the constructor APIs in `api/constructors.kt`.} */
internal interface ConstructorsDocs {

    /**
     * All named columns must have unique names. For columns with empty names unique column names
     * are generated: "untitled", "untitled1", "untitled2" etc.
     *
     * All columns must have equal sizes.
     *
     * For more information: {@include [DocumentationUrls.DataFrameOf]}
     *
     * @throws DuplicateColumnNamesException if column names are not unique
     * @throws UnequalColumnSizesException if column sizes are not equal
     */
    typealias DataFrameOfContractSnippet = Nothing

    /**
     * The type of each column is inferred from its values, with the reified type argument as an upper bound:
     * with `Number` as the type argument, `listOf(1, 2)` produces a column of type `Int`,
     * and only mixed values such as `listOf(1, 2.5)` produce a column of type `Number`.
     */
    typealias InferredColumnTypeSnippet = Nothing
}
