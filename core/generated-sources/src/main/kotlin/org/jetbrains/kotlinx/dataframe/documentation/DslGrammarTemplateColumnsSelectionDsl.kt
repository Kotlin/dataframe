package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupRef
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetRef
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.UsageTemplateExample.ColumnSetName

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
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
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
     *
     *
     *
     *
     *
     */
    public interface DslGrammarTemplate {

        // region parts

        /* Can be set to nothing to disable the definitions part */
        public interface DefinitionsPart

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
         * `columnGroupReference: `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupNoSingleColumnDef

        /** `colSelector: `[`ColumnSelector`][ColumnSelector] */
        public interface ColumnSelectorDef

        /** `colsSelector: `[`ColumnsSelector`][ColumnsSelector] */
        public interface ColumnsSelectorDef

        /**
         * `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  | `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnDef

        /**
         * `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<* | `[`DataRow`][DataRow]`<*>> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
         */
        public interface ColumnGroupDef

        /** `columnNoAccessor: `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] */
        public interface ColumnNoAccessorDef

        /** `columnOrSet: `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef] */
        public interface ColumnOrColumnSetDef

        /** `columnSet: `[`ColumnSet`][ColumnSet]`<*>` */
        public interface ColumnSetDef

        /** `columnsResolver: `[`ColumnsResolver`][ColumnsResolver] */
        public interface ColumnsResolverDef

        /** `condition: `[`ColumnFilter`][ColumnFilter] */
        public interface ConditionDef

        /** `expression: `[Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] */
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
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<* | `[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `number: `[`Int`][Int]
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`example`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]**`(`**`[`[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]`]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsExample`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]**`(`**`[`[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]`]`**`)`**
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

        /** __`.`__[**`example`**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** __`.`__[**`colsExample`**][ColumnsSelectionDsl.first] */
        public interface ColumnGroupName
    }
}
