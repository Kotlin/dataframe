package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupRef
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetRef
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnSetName
import kotlin.reflect.KProperty
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
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the plain DSL:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  
     *
     *
     *
     *
     *
     *
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

        /** `columnSet: `[ColumnSet][ColumnSet]`<*>` */
        public interface ColumnSetDef

        /**
         * `columnGroup: `[SingleColumn][SingleColumn]`<`[DataRow][DataRow]`<*>> | `[String][String]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         * `| `[KProperty][KProperty]`<*>` | `[ColumnPath][ColumnPath]
         */
        public interface ColumnGroupDef

        /**
         * `singleColumn: `[SingleColumn][SingleColumn]`<`[DataRow][DataRow]`<*>>
         */
        public interface SingleColumnDef

        /**
         * `columnGroupReference: `[String][String]` | `[KProperty][KProperty]`<*>`
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         * | `[ColumnPath][ColumnPath]
         */
        public interface ColumnGroupNoSingleColumnDef

        /**
         * `column: `[ColumnAccessor][ColumnAccessor]` | `[String][String]
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         * `| `[KProperty][KProperty]`<*> | `[ColumnPath][ColumnPath]
         */
        public interface ColumnDef

        /** `T: Column type` */
        public interface ColumnTypeDef

        /** `index: `[Int][Int] */
        public interface IndexDef

        /** `indexRange: `[IntRange][IntRange] */
        public interface IndexRangeDef

        /** `condition: `[ColumnFilter][ColumnFilter] */
        public interface ConditionDef

        /** `kind: `[ColumnKind][ColumnKind] */
        public interface ColumnKindDef

        /** `colSelector: `[ColumnSelector][ColumnSelector] */
        public interface ColumnSelectorDef

        /** `number: `[Int][Int] */
        public interface NumberDef

        /** `text: `[String][String] */
        public interface TextDef

        /** `ignoreCase: `[Boolean][Boolean] */
        public interface IgnoreCaseDef

        /** `regex: `[Regex][Regex] */
        public interface RegexDef

        /** `kType: `[KType][KType] */
        public interface KTypeDef

        // endregion

        // region References to the definitions

        /** [columnSet][ColumnSetDef] */
        public interface ColumnSetRef

        /** [columnGroup][ColumnGroupDef] */
        public interface ColumnGroupRef

        /** [singleColumn][SingleColumnDef] */
        public interface SingleColumnRef

        /** [columnGroupReference][ColumnGroupNoSingleColumnDef] */
        public interface ColumnGroupNoSingleColumnRef

        /** [condition][ConditionDef] */
        public interface ConditionRef

        /** [column][ColumnDef] */
        public interface ColumnRef

        /** [index][IndexDef] */
        public interface IndexRef

        /** [indexRange][IndexRangeDef] */
        public interface IndexRangeRef

        /** [T][ColumnTypeDef] */
        public interface ColumnTypeRef

        /** [kind][ColumnKindDef] */
        public interface ColumnKindRef

        /** [colSelector][ColumnSelectorDef] */
        public interface ColumnSelectorRef

        /** [number][NumberDef] */
        public interface NumberRef

        /** [text][TextDef] */
        public interface TextRef

        /** [ignoreCase][IgnoreCaseDef] */
        public interface IgnoreCaseRef

        /** [regex][RegexDef] */
        public interface RegexRef

        /** [kType][KTypeDef] */
        public interface KTypeRef

        // endregion
    }

    /**
     * ## MyFunction Example Usage
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][KProperty]`<*>` | `[ColumnPath][ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `number: `[Int][Int]
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**example**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]**`(`**`[`[number][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NumberDef]`]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsExample**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]**`(`**`[`[number][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NumberDef]`]`**`)`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface UsageTemplateExample {

        /** .[**example**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** .[**colsExample**][ColumnsSelectionDsl.first] */
        public interface ColumnGroupName
    }
}
