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
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
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
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
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
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
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
         * `columnGroupReference: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnGroupNoSingleColumnDef = Nothing

        /** `colSelector: `[`ColumnSelector`][ColumnSelector] */
        public typealias ColumnSelectorDef = Nothing

        /** `colsSelector: `[`ColumnsSelector`][ColumnsSelector] */
        public typealias ColumnsSelectorDef = Nothing

        /**
         * `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnDef = Nothing

        /**
         * `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public typealias ColumnGroupDef = Nothing

        /** `columnNoAccessor: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] */
        public typealias ColumnNoAccessorDef = Nothing

        /** `columnOrSet: `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef] */
        public typealias ColumnOrColumnSetDef = Nothing

        /** `columnSet: `[`ColumnSet`][ColumnSet]`<*>` */
        public typealias ColumnSetDef = Nothing

        /** `columnsResolver: `[`ColumnsResolver`][ColumnsResolver] */
        public typealias ColumnsResolverDef = Nothing

        /** `condition: `[`ColumnFilter`][ColumnFilter] */
        public typealias ConditionDef = Nothing

        /** `expression: `[Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] */
        public typealias ColumnExpressionDef = Nothing

        /** `ignoreCase: `[`Boolean`][Boolean] */
        public typealias IgnoreCaseDef = Nothing

        /** `index: `[`Int`][Int] */
        public typealias IndexDef = Nothing

        /** `indexRange: `[`IntRange`][IntRange] */
        public typealias IndexRangeDef = Nothing

        /** `infer: `[`Infer`][org.jetbrains.kotlinx.dataframe.api.Infer] */
        public typealias InferDef = Nothing

        /** `kind: `[`ColumnKind`][ColumnKind] */
        public typealias ColumnKindDef = Nothing

        /** `kType: `[`KType`][kotlin.reflect.KType] */
        public typealias KTypeDef = Nothing

        /** `name: `[`String`][String] */
        public typealias NameDef = Nothing

        /** `number: `[`Int`][Int] */
        public typealias NumberDef = Nothing

        /** `regex: `[`Regex`][Regex] */
        public typealias RegexDef = Nothing

        /**
         * `singleColumn: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
         */
        public typealias SingleColumnDef = Nothing

        /** `T: Column type` */
        public typealias ColumnTypeDef = Nothing

        /** `text: `[`String`][String] */
        public typealias TextDef = Nothing
        // endregion

        // region References to the definitions

        // endregion
    }
}
