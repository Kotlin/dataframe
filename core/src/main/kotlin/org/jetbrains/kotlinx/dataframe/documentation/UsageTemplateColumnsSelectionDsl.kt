package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupRef
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetRef
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnSetName
import kotlin.reflect.KType

/*
 * This template is to be used in displaying the Usage / DSL grammar
 * of each individual ColumnsSelectionDsl function group, as well as the entire
 * thing itself.
 *
 * See an example of how to use this template at [UsageTemplateColumnsSelectionDsl.UsageTemplateExample]
 */
public interface UsageTemplateColumnsSelectionDsl {

    /**
     * {@comment Definitions part, including column set and column group by default.}
     * {@include [LineBreak]}
     * {@getArg [UsageTemplate.DefinitionsArg]}
     *
     * {@setArg [UsageTemplate.PlainDslPart]
     *  {@include [LineBreak]}
     *  ### In the [ColumnsSelectionDsl][ColumnsSelectionDsl]:
     *
     *  {@include [LineBreak]}
     *  {@getArg [UsageTemplate.PlainDslFunctionsArg]}
     * }{@getArg [UsageTemplate.PlainDslPart]}
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@setArg [UsageTemplate.ColumnSetPart]
     *  {@include [LineBreak]}
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  {@include [LineBreak]}
     *  {@include [ColumnSetRef]}
     *
     *  {@getArg [UsageTemplate.ColumnSetFunctionsArg]}
     * }{@getArg [UsageTemplate.ColumnSetPart]}
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@setArg [UsageTemplate.ColumnGroupPart]
     *  {@include [LineBreak]}
     *  ### On a column group reference:
     *
     *  {@include [LineBreak]}
     *  {@include [ColumnGroupRef]}
     *
     *  {@getArg [UsageTemplate.ColumnGroupFunctionsArg]}
     * }{@getArg [UsageTemplate.ColumnGroupPart]}
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@comment Setting default arguments for the template}
     * {@setArg [UsageTemplate.DefinitionsArg]}
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]}
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]}
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]}
     */
    public interface UsageTemplate {

        // region parts

        /* Can be set to nothing to disable the plain dsl part */
        public interface PlainDslPart

        /* Can be set to nothing to disable the column set part */
        public interface ColumnSetPart

        /* Can be set to nothing to disable the column group part */
        public interface ColumnGroupPart

        // endregion

        // region Template arguments

        /* What to put in definitions part aside from the default part. */
        public interface DefinitionsArg

        /* What to put in the plain dsl part. Does not need indents. */
        public interface PlainDslFunctionsArg

        /* What to put in the column set part. Needs indents. */
        public interface ColumnSetFunctionsArg

        /* What to put in the column group part. Needs indents. */
        public interface ColumnGroupFunctionsArg

        // endregion

        // region Definitions for at the top of the template

        /**
         * `columnGroupReference: `[String][String]` | `[KProperty][kotlin.reflect.KProperty]`<*>`
         *
         * {@include [QuadrupleIndent]}{@include [QuadrupleIndent]}{@include [DoubleIndent]}
         * | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupNoSingleColumnDef

        /** `colSelector: `[ColumnSelector][ColumnSelector] */
        public interface ColumnSelectorDef

        /** `colsSelector: `[ColumnsSelector][ColumnsSelector] */
        public interface ColumnsSelectorDef

        /**
         * `column: `[ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]` | `[String][String]
         *
         * {@include [DoubleIndent]}{@include [HalfIndent]}{@include [QuarterIndent]}
         * `| `[KProperty][kotlin.reflect.KProperty]`<*> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnDef

        /**
         * `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
         *
         * {@include [QuadrupleIndent]}{@include [Indent]}{@include [QuarterIndent]}
         * `| `[KProperty][kotlin.reflect.KProperty]`<* | `[DataRow][DataRow]`<*>>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupDef

        /** `columnNoAccessor: `[String][String]` | `[KProperty][kotlin.reflect.KProperty]`<*> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] */
        public interface ColumnNoAccessorDef

        /** `columnOrSet: `{@include [ColumnRef]}` | `{@include [ColumnSetRef]} */
        public interface ColumnOrColumnSetDef

        /** `columnSet: `[ColumnSet][ColumnSet]`<*>` */
        public interface ColumnSetDef

        /** `columnsResolver: `[ColumnsResolver][ColumnsResolver] */
        public interface ColumnsResolverDef

        /** `condition: `[ColumnFilter][ColumnFilter] */
        public interface ConditionDef

        /** `expression: `{@include [ColumnExpressionLink]} */
        public interface ColumnExpressionDef

        /** `ignoreCase: `[Boolean][Boolean] */
        public interface IgnoreCaseDef

        /** `index: `[Int][Int] */
        public interface IndexDef

        /** `indexRange: `[IntRange][IntRange] */
        public interface IndexRangeDef

        /** `infer: `[Infer][org.jetbrains.kotlinx.dataframe.api.Infer] */
        public interface InferDef

        /** `kind: `[ColumnKind][ColumnKind] */
        public interface ColumnKindDef

        /** `kType: `[KType][KType] */
        public interface KTypeDef

        /** `name: `[String][String] */
        public interface NameDef

        /** `number: `[Int][Int] */
        public interface NumberDef

        /** `regex: `[Regex][Regex] */
        public interface RegexDef

        /**
         * `singleColumn: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>
         */
        public interface SingleColumnDef

        /** `T: Column type` */
        public interface ColumnTypeDef

        /** `text: `[String][String] */
        public interface TextDef

        // endregion

        // region References to the definitions

        /** [columnGroupReference][ColumnGroupNoSingleColumnDef] */
        public interface ColumnGroupNoSingleColumnRef

        /** [colSelector][ColumnSelectorDef] */
        public interface ColumnSelectorRef

        /** [colsSelector][ColumnsSelectorDef] */
        public interface ColumnsSelectorRef

        /** [column][ColumnDef] */
        public interface ColumnRef

        /** [columnGroup][ColumnGroupDef] */
        public interface ColumnGroupRef

        /** [columnNoAccessor][ColumnNoAccessorDef] */
        public interface ColumnNoAccessorRef

        /** [columnOrSet][ColumnOrColumnSetDef] */
        public interface ColumnOrColumnSetRef

        /** [columnSet][ColumnSetDef] */
        public interface ColumnSetRef

        /** [columnsResolver][ColumnsResolverDef] */
        public interface ColumnsResolverRef

        /** [condition][ConditionDef] */
        public interface ConditionRef

        /** [expression][ColumnExpressionDef] */
        public interface ColumnExpressionRef

        /** [ignoreCase][IgnoreCaseDef] */
        public interface IgnoreCaseRef

        /** [index][IndexDef] */
        public interface IndexRef

        /** [indexRange][IndexRangeDef] */
        public interface IndexRangeRef

        /** [infer][InferDef] */
        public interface InferRef

        /** [kind][ColumnKindDef] */
        public interface ColumnKindRef

        /** [kType][KTypeDef] */
        public interface KTypeRef

        /** [name][NameDef] */
        public interface NameRef

        /** [number][NumberDef] */
        public interface NumberRef

        /** [regex][RegexDef] */
        public interface RegexRef

        /** [singleColumn][SingleColumnDef] */
        public interface SingleColumnRef

        /** [T][ColumnTypeDef] */
        public interface ColumnTypeRef

        /** [text][TextDef] */
        public interface TextRef

        // endregion
    }

    /**
     * ## MyFunction Example Usage
     *
     * {@comment First include the template itself.}
     * @include [UsageTemplate]
     *
     * {@comment Then set the definition arguments for each definition that is used below.
     *  Don't forget to add the definitions for ColumnSet and ColumnGroup if you're going to use them.
     *  Also, add LineBreaks in between them.
     * }
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.NumberDef]}
     * }
     *
     * {@comment Then use PlainDslFunctionsArg, ColumnSetFunctionsArg, and ColumnGroupFunctionsArg to fill in
     *  the parts belonging to each of these sections. Don't forget to add indents to the ColumnSet and ColumnGroup
     *  parts. Also note we're using -Ref instead of -Def here to refer to definitions.
     * }
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**`[`{@include [UsageTemplate.NumberRef]}`]`**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**`[`{@include [UsageTemplate.NumberRef]}`]`**`)`**
     * }
     *
     * {@comment Our example function has no Plain DSL part, so we set it to nothing. No need to set PlainDslFunctionsArg.}
     * {@setArg [UsageTemplate.PlainDslPart]}
     */
    public interface UsageTemplateExample {

        /** .[**example**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** .[**colsExample**][ColumnsSelectionDsl.first] */
        public interface ColumnGroupName
    }
}
