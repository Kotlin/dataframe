package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver

/*
 * This template is to be used in displaying the Usage / DSL grammar
 * of each individual ColumnsSelectionDsl function group, as well as the entire
 * thing itself.
 *
 * See an example of how to use this template at [UsageTemplateColumnsSelectionDsl.UsageTemplateExample]
 */
public interface DslGrammarTemplateColumnsSelectionDsl {

    /**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *
     *
     */
    public interface DslGrammarTemplate {

        // region parts

        // endregion

        // region Template arguments

        // endregion

        // region Definitions for at the top of the template

        /**
         * `columnGroupReference: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnGroupNoSingleColumnDef = Nothing

        /** `colSelector: `[<code>`ColumnSelector`</code>][ColumnSelector] */
        public typealias ColumnSelectorDef = Nothing

        /** `colsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector] */
        public typealias ColumnsSelectorDef = Nothing

        /**
         * `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnDef = Nothing

        /**
         * `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnGroupDef = Nothing

        /** `columnNoAccessor: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] */
        public typealias ColumnNoAccessorDef = Nothing

        /** `columnOrSet: `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef] */
        public typealias ColumnOrColumnSetDef = Nothing

        /** `columnSet: `[<code>`ColumnSet`</code>][ColumnSet]`<*>` */
        public typealias ColumnSetDef = Nothing

        /** `columnsResolver: `[<code>`ColumnsResolver`</code>][ColumnsResolver] */
        public typealias ColumnsResolverDef = Nothing

        /** `condition: `[<code>`ColumnFilter`</code>][ColumnFilter] */
        public typealias ConditionDef = Nothing

        /** `expression: `[<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] */
        public typealias ColumnExpressionDef = Nothing

        /** `ignoreCase: `[<code>`Boolean`</code>][Boolean] */
        public typealias IgnoreCaseDef = Nothing

        /** `index: `[<code>`Int`</code>][Int] */
        public typealias IndexDef = Nothing

        /** `indexRange: `[<code>`IntRange`</code>][IntRange] */
        public typealias IndexRangeDef = Nothing

        /** `infer: `[<code>`Infer`</code>][org.jetbrains.kotlinx.dataframe.api.Infer] */
        public typealias InferDef = Nothing

        /** `kind: `[<code>`ColumnKind`</code>][ColumnKind] */
        public typealias ColumnKindDef = Nothing

        /** `kType: `[<code>`KType`</code>][kotlin.reflect.KType] */
        public typealias KTypeDef = Nothing

        /** `name: `[<code>`String`</code>][String] */
        public typealias NameDef = Nothing

        /** `number: `[<code>`Int`</code>][Int] */
        public typealias NumberDef = Nothing

        /** `regex: `[<code>`Regex`</code>][Regex] */
        public typealias RegexDef = Nothing

        /**
         * `singleColumn: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
         */
        public typealias SingleColumnDef = Nothing

        /** `T: Column type` */
        public typealias ColumnTypeDef = Nothing

        /** `text: `[<code>`String`</code>][String] */
        public typealias TextDef = Nothing
        // endregion

        // region References to the definitions

        // endregion
    }
}
