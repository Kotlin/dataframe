@file:ExcludeFromSources
@file:Suppress("ClassName")

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException

/**
 * Shared KDoc fragments for the constructor APIs in `api/constructors.kt`.
 */
internal interface ConstructorsDocs {

    /**
     * The referenced column doesn't have to exist in a [DataFrame] yet:
     * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
     *
     * This creates a reference, not a column: to create a column from values, see `columnOf`.
     *
     * References can also be created with the `by` property delegate, which takes the column name
     * from the property name, or with [named][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
     * which sets the name at the call site.
     *
     * For more information: {@include [DocumentationUrls.ColumnSelectors]}
     */
    typealias ColumnReferenceUsageSnippet = Nothing

    /**
     * All named columns must have unique names. For columns with empty names unique column names
     * are generated: "untitled", "untitled1", "untitled2" etc.
     *
     * All columns must have equal sizes.
     *
     * For more information: {@include [DocumentationUrls.DataFrameOf]}
     *
     * @throws [DuplicateColumnNamesException] if column names are not unique
     * @throws [UnequalColumnSizesException] if column sizes are not equal
     */
    typealias DataFrameOfContractSnippet = Nothing
}
