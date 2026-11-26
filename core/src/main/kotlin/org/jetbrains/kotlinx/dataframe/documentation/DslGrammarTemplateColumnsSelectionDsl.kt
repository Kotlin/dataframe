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
    public interface DslGrammarTemplate {

        // region parts

        // Can be set to nothing to disable the definitions part
        @ExcludeFromSources
        public typealias DEFINITIONS_PART = Nothing

        // Can be set to nothing to disable the plain dsl part
        @ExcludeFromSources
        public typealias PLAIN_DSL_PART = Nothing

        // Can be set to nothing to disable the column set part
        @ExcludeFromSources
        public typealias COLUMN_SET_PART = Nothing

        // Can be set to nothing to disable the column group part
        @ExcludeFromSources
        public typealias COLUMN_GROUP_PART = Nothing
        // endregion

        // region Template arguments

        // What to put in definitions part aside from the default part.
        @ExcludeFromSources
        public typealias DEFINITIONS = Nothing

        // What to put in the plain dsl part. Does not need indents.
        @ExcludeFromSources
        public typealias PLAIN_DSL_FUNCTIONS = Nothing

        // What to put in the column set part. Needs indents.
        @ExcludeFromSources
        public typealias COLUMN_SET_FUNCTIONS = Nothing

        // What to put in the column group part. Needs indents.
        @ExcludeFromSources
        public typealias COLUMN_GROUP_FUNCTIONS = Nothing
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

        /** `columnOrSet: `{@include [ColumnRef]}`  |  `{@include [ColumnSetRef]} */
        public typealias ColumnOrColumnSetDef = Nothing

        /** `columnSet: `[`ColumnSet`][ColumnSet]`<*>` */
        public typealias ColumnSetDef = Nothing

        /** `columnsResolver: `[`ColumnsResolver`][ColumnsResolver] */
        public typealias ColumnsResolverDef = Nothing

        /** `condition: `[`ColumnFilter`][ColumnFilter] */
        public typealias ConditionDef = Nothing

        /** `expression: `{@include [ColumnExpressionLink]} */
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

        /** [`columnGroupReference`][ColumnGroupNoSingleColumnDef] */
        @ExcludeFromSources
        public typealias ColumnGroupNoSingleColumnRef = Nothing

        /** [`colSelector`][ColumnSelectorDef] */
        @ExcludeFromSources
        public typealias ColumnSelectorRef = Nothing

        /** [`colsSelector`][ColumnsSelectorDef] */
        @ExcludeFromSources
        public typealias ColumnsSelectorRef = Nothing

        /** [`column`][ColumnDef] */
        @ExcludeFromSources
        public typealias ColumnRef = Nothing

        /** [`columnGroup`][ColumnGroupDef] */
        @ExcludeFromSources
        public typealias ColumnGroupRef = Nothing

        /** [`columnNoAccessor`][ColumnNoAccessorDef] */
        @ExcludeFromSources
        public typealias ColumnNoAccessorRef = Nothing

        /** [`columnOrSet`][ColumnOrColumnSetDef] */
        @ExcludeFromSources
        public typealias ColumnOrColumnSetRef = Nothing

        /** [`columnSet`][ColumnSetDef] */
        @ExcludeFromSources
        public typealias ColumnSetRef = Nothing

        /** [`columnsResolver`][ColumnsResolverDef] */
        @ExcludeFromSources
        public typealias ColumnsResolverRef = Nothing

        /** [`condition`][ConditionDef] */
        @ExcludeFromSources
        public typealias ConditionRef = Nothing

        /** [`expression`][ColumnExpressionDef] */
        @ExcludeFromSources
        public typealias ColumnExpressionRef = Nothing

        /** [`ignoreCase`][IgnoreCaseDef] */
        @ExcludeFromSources
        public typealias IgnoreCaseRef = Nothing

        /** [`index`][IndexDef] */
        @ExcludeFromSources
        public typealias IndexRef = Nothing

        /** [`indexRange`][IndexRangeDef] */
        @ExcludeFromSources
        public typealias IndexRangeRef = Nothing

        /** [`infer`][InferDef] */
        @ExcludeFromSources
        public typealias InferRef = Nothing

        /** [`kind`][ColumnKindDef] */
        @ExcludeFromSources
        public typealias ColumnKindRef = Nothing

        /** [`kType`][KTypeDef] */
        @ExcludeFromSources
        public typealias KTypeRef = Nothing

        /** [`name`][NameDef] */
        @ExcludeFromSources
        public typealias NameRef = Nothing

        /** [`number`][NumberDef] */
        @ExcludeFromSources
        public typealias NumberRef = Nothing

        /** [`regex`][RegexDef] */
        @ExcludeFromSources
        public typealias RegexRef = Nothing

        /** [`singleColumn`][SingleColumnDef] */
        @ExcludeFromSources
        public typealias SingleColumnRef = Nothing

        /** [`T`][ColumnTypeDef] */
        @ExcludeFromSources
        public typealias ColumnTypeRef = Nothing

        /** [`text`][TextDef] */
        @ExcludeFromSources
        public typealias TextRef = Nothing
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
    @ExcludeFromSources
    public interface UsageTemplateExample {

        /** __`.`__[**`example`**][ColumnsSelectionDsl.first] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`colsExample`**][ColumnsSelectionDsl.first] */
        public typealias ColumnGroupName = Nothing
    }
}
