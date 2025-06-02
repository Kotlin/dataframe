@file:ExcludeFromSources

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
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@get [DslGrammarTemplate.DEFINITIONS_PART]
     *  {@include [LineBreak]}
     *  ### Definitions:
     *  {@get [DslGrammarTemplate.DEFINITIONS]}
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@get [DslGrammarTemplate.PLAIN_DSL_PART]
     *  {@include [LineBreak]}
     *  ### What can be called directly in the {@include [ColumnsSelectionDslLink]}:
     *
     *  {@include [LineBreak]}
     *  {@get [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]}
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@get [DslGrammarTemplate.COLUMN_SET_PART]
     *  {@include [LineBreak]}
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  {@include [LineBreak]}
     *  {@include [ColumnSetRef]}
     *
     *  {@get [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]}
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@get [DslGrammarTemplate.COLUMN_GROUP_PART]
     *  {@include [LineBreak]}
     *  ### What can be called on a [Column Group (reference)][DslGrammarTemplate.ColumnGroupDef]:
     *
     *  {@include [LineBreak]}
     *  {@include [ColumnGroupRef]}
     *
     *  {@get [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]}
     * }
     */
    @Suppress("ClassName")
    public interface DslGrammarTemplate {

        // region parts

        // Can be set to nothing to disable the definitions part
        public interface DEFINITIONS_PART

        // Can be set to nothing to disable the plain dsl part
        public interface PLAIN_DSL_PART

        // Can be set to nothing to disable the column set part
        public interface COLUMN_SET_PART

        // Can be set to nothing to disable the column group part
        public interface COLUMN_GROUP_PART

        // endregion

        // region Template arguments

        // What to put in definitions part aside from the default part.
        public interface DEFINITIONS

        // What to put in the plain dsl part. Does not need indents.
        public interface PLAIN_DSL_FUNCTIONS

        // What to put in the column set part. Needs indents.
        public interface COLUMN_SET_FUNCTIONS

        // What to put in the column group part. Needs indents.
        public interface COLUMN_GROUP_FUNCTIONS

        // endregion

        // region Definitions for at the top of the template

        /**
         * `columnGroupReference: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupNoSingleColumnDef

        /** `colSelector: `[`ColumnSelector`][ColumnSelector] */
        public interface ColumnSelectorDef

        /** `colsSelector: `[`ColumnsSelector`][ColumnsSelector] */
        public interface ColumnsSelectorDef

        /**
         * `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnDef

        /**
         * `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupDef

        /** `columnNoAccessor: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] */
        public interface ColumnNoAccessorDef

        /** `columnOrSet: `{@include [ColumnRef]}`  |  `{@include [ColumnSetRef]} */
        public interface ColumnOrColumnSetDef

        /** `columnSet: `[`ColumnSet`][ColumnSet]`<*>` */
        public interface ColumnSetDef

        /** `columnsResolver: `[`ColumnsResolver`][ColumnsResolver] */
        public interface ColumnsResolverDef

        /** `condition: `[`ColumnFilter`][ColumnFilter] */
        public interface ConditionDef

        /** `expression: `{@include [ColumnExpressionLink]} */
        public interface ColumnExpressionDef

        /** `ignoreCase: `[`Boolean`][Boolean] */
        public interface IgnoreCaseDef

        /** `index: `[`Int`][Int] */
        public interface IndexDef

        /** `indexRange: `[`IntRange`][IntRange] */
        public interface IndexRangeDef

        /** `infer: `[`Infer`][org.jetbrains.kotlinx.dataframe.api.Infer] */
        public interface InferDef

        /** `kind: `[`ColumnKind`][ColumnKind] */
        public interface ColumnKindDef

        /** `kType: `[`KType`][kotlin.reflect.KType] */
        public interface KTypeDef

        /** `name: `[`String`][String] */
        public interface NameDef

        /** `number: `[`Int`][Int] */
        public interface NumberDef

        /** `regex: `[`Regex`][Regex] */
        public interface RegexDef

        /**
         * `singleColumn: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
         */
        public interface SingleColumnDef

        /** `T: Column type` */
        public interface ColumnTypeDef

        /** `text: `[`String`][String] */
        public interface TextDef

        // endregion

        // region References to the definitions

        /** [`columnGroupReference`][ColumnGroupNoSingleColumnDef] */
        public interface ColumnGroupNoSingleColumnRef

        /** [`colSelector`][ColumnSelectorDef] */
        public interface ColumnSelectorRef

        /** [`colsSelector`][ColumnsSelectorDef] */
        public interface ColumnsSelectorRef

        /** [`column`][ColumnDef] */
        public interface ColumnRef

        /** [`columnGroup`][ColumnGroupDef] */
        public interface ColumnGroupRef

        /** [`columnNoAccessor`][ColumnNoAccessorDef] */
        public interface ColumnNoAccessorRef

        /** [`columnOrSet`][ColumnOrColumnSetDef] */
        public interface ColumnOrColumnSetRef

        /** [`columnSet`][ColumnSetDef] */
        public interface ColumnSetRef

        /** [`columnsResolver`][ColumnsResolverDef] */
        public interface ColumnsResolverRef

        /** [`condition`][ConditionDef] */
        public interface ConditionRef

        /** [`expression`][ColumnExpressionDef] */
        public interface ColumnExpressionRef

        /** [`ignoreCase`][IgnoreCaseDef] */
        public interface IgnoreCaseRef

        /** [`index`][IndexDef] */
        public interface IndexRef

        /** [`indexRange`][IndexRangeDef] */
        public interface IndexRangeRef

        /** [`infer`][InferDef] */
        public interface InferRef

        /** [`kind`][ColumnKindDef] */
        public interface ColumnKindRef

        /** [`kType`][KTypeDef] */
        public interface KTypeRef

        /** [`name`][NameDef] */
        public interface NameRef

        /** [`number`][NumberDef] */
        public interface NumberRef

        /** [`regex`][RegexDef] */
        public interface RegexRef

        /** [`singleColumn`][SingleColumnDef] */
        public interface SingleColumnRef

        /** [`T`][ColumnTypeDef] */
        public interface ColumnTypeRef

        /** [`text`][TextDef] */
        public interface TextRef

        // endregion
    }

    /**
     * ## MyFunction Example Usage
     *
     * {@comment First include the template itself.}
     * @include [DslGrammarTemplate]
     *
     * {@comment Then set the definition arguments for each definition that is used below.
     *  Don't forget to add the definitions for ColumnSet and ColumnGroup if you're going to use them.
     *  Also, add LineBreaks in between them.
     * }
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.NumberDef]}
     * }
     *
     * {@comment Then use PlainDslFunctionsArg, ColumnSetFunctionsArg, and ColumnGroupFunctionsArg to fill in
     *  the parts belonging to each of these sections. Don't forget to add indents to the ColumnSet and ColumnGroup
     *  parts. Also note we're using -Ref instead of -Def here to refer to definitions.
     * }
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**`[`{@include [DslGrammarTemplate.NumberRef]}`]`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**`[`{@include [DslGrammarTemplate.NumberRef]}`]`**`)`**
     * }
     *
     * {@comment Our example function has no Plain DSL part, so we set it to nothing. No need to set PlainDslFunctionsArg.}
     * {@set [DslGrammarTemplate.PLAIN_DSL_PART]}
     */
    public interface UsageTemplateExample {

        /** __`.`__[**`example`**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** __`.`__[**`colsExample`**][ColumnsSelectionDsl.first] */
        public interface ColumnGroupName
    }
}
