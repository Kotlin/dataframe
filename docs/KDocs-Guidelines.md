# Documentation Guidelines

This document outlines the guidelines for writing KDocs in the Kotlin DataFrame project.

## The most important advice

Please never write KDocs from scratch without a necessity!
Find existing KDocs for the similar operation or other entity and reuse it.
However, take its specific into account.

And don't be afraid to deviate from the rules or add something new –
the most important thing is to help users to understand the library better!

## KoDEx

We use [KoDEx](https://github.com/Jolanrensen/KoDEx) KDocs preprocessor.
It adds several useful utilities for writing KDocs. 

Please read about 
the [KDocs preprocessing using KoDEx](../KDOC_PREPROCESSING.md) before working with Kotlin DataFrame KDocs.

Install the [KoDEx plugin for IDEA](https://plugins.jetbrains.com/plugin/27473---kodex---kotlin-documentation-extensions)
for correct KDocs display inside the IntelliJ IDEA.

### Reuse Common Parts

One of the best utilities of KoDEx is the ability to reuse common parts of KDocs.
This can be done by using the 
[`@include` tag](https://github.com/Jolanrensen/KoDEx/wiki/Notation#include-including-content-from-other-kdocs),
which allows you to include a KDoc for another documentable element.
This could be a class, an interface, a typealias, and so on.

There are a lot of interfaces in the project that are only used for including their KDocs
to other KDocs. Such interfaces are marked with `@ExcludeFromSources` 
and not included in the sources after the compilation (make sure you are not referencing them directly,
i.e., only use it inside the `@include`). For example, 
[`ColumnPathCreation` interface](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/documentation/ColumnPathCreation.ktc)
has a KDoc which describes column path creation behavior. The whole file is excluded from sources,
but the KDoc is included in other KDocs.
Also, you can use 
[`@set` and `@get` tags](https://github.com/Jolanrensen/KoDEx/wiki/Notation#set-and-get---setting-and-getting-variables)
along with `@include` to change variables value in common parts. This is especially useful for 
writing examples of methods with similar usage but with different names.

## What should be documented?

**All public API should be documented!** Our goal is 100% public functions, classes, and variable KDocs coverage.
Some part of public API is not intended for end users, but can be used in Compiler Plugin or potential
KDF extension libraries. These methods should have a small KDocs as well.

For internal API, please add at least a small note.

## Kotlin DataFrame Operations KDoc Structure

Operation KDocs size and structure completely depend on the operation complexity. 

The best way to write a new KDoc for an operation is to define its kind and 
use the existing KDoc of an operation of the same kind as a template.

There are four kinds; here's a list of them:

1. Simple, Stdlib-like operations that don't have arguments or have simple types (primitives, classes) 
as arguments and return simple value, `DataFrame`, `DataRow` or `DataColumn`.
   * For example, 
[`first` without arguments](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/api/first.kt#L106).
   * KDocs for such operations can be short, especially if it's trivial enough.
2. Operations with [`DataRow` API](https://kotlin.github.io/dataframe/datarow.html). 
   * For example, [`first` with predicate](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/api/first.kt#L139).
   * Remember to describe a mechanism of `DataRow` API usage in the KDoc - it's not obvious to the user.
3. Operations with the [Columns Selection DSL](https://kotlin.github.io/dataframe/columnselectors.html) that return a single and return simple value, `DataFrame`, `DataRow` or `DataColumn`.
   * For example, [`remove`](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/api/remove.kt).
   * Remember to describe a mechanism of Columns Selection DSL. 
   * Add several examples with different columns selection options.
4. Complex operations with a multiple methods chain.
   * For example, [`convert`](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/api/convert.kt).
   * Such operations consist of at least two methods and special resulting classes as the intermediate steps. 
     All of them should be well documented and have cross references to each other.
   * Usually some os the methods have the [Columns Selection DSL](https://kotlin.github.io/dataframe/columnselectors.html); 
     these methods should be documented by the rules above.
   * For a better understanding of the complex operation, we write an [operation grammar](#grammar) using a 
   [special notation](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/documentation/DslGrammar.kt).
   Add a reference to the operation Grammar in each related class and method KDoc.

### General Template
    
The generalized template for all operations:

```kotlin
/**
 * (First line - brief and concise description of the operation)
 * 
 * (Body - second, third, and so on lines - detailed description of the operation,
 * related mechanisms, etc.; optional)
 * 
 * (Documentation website link)
 * 
 * (See also section; optional)
 * 
 * (Columns selection information - for operations with columns selection only)
 * 
 * (Examples section)
 * 
 * (Parameters and return section)
 */
```

Below there are some rules for each section:

#### First line

The first line should be short but at the same time give a clear understanding of the operation.

Should start with a verb. Usually "returns" or "creates" for operations that return a simple value, 
`DataFrame`, `DataColumn` or `DataRow` (excluding methods which are non-final part of complex operations).

#### Body

For the non-trivial operations, write a detailed description of the operation, 
describe method behavior and resulting value.

For complex operations, in the KDoc of the initial method write that this is only the first step of the operation,
and it should be continued with other methods. Also add a note about the [operation grammar](#grammar) in this case.
For example (from the `insert` KDoc):

```
 * This function does not immediately insert the new column but instead specify a column to insert and
 * returns an [InsertClause],
 * which serves as an intermediate step.
 * The [InsertClause] object provides methods to insert a new column using:
 * - [under][InsertClause.under] - inserts a new column under the specified column group.
 * - [after][InsertClause.after] - inserts a new column after the specified column.
 * - [at][InsertClause.at]- inserts a new column at the specified position.
 *
 * Each method returns a new [DataFrame] with the inserted column.
 * 
 * Check out [Grammar].
```

The next methods in the chain may be finalizing or intermediate steps - write about it explicitly.
Remember to add a link to the initial method and [operation grammar](#grammar) in all of them.

If the method uses columns selection, add a note about nested columns and column groups:

```
@include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
```

#### See also section

Add a reference to all related methods. Those can be methods with the similar or opposite behavior.

Example from `DataFrame.first` KDoc:

```
 * See also [firstOrNull][DataFrame.firstOrNull],
 * [last][DataFrame.last],
 * [take][DataFrame.take],
 * [takeWhile][DataFrame.takeWhile],
 * [takeLast][DataFrame.takeLast].
```

* If there is a reverse operation, add a specific note about it.
* If this operation is a shortcut or a special case of another one,
add a note about it. 
* If you think a user can confuse the operation with another one,
write it down exactly like that.

For example, from `group` KDoc:


```
 * Reverse operation: [ungroup].
 *
 * It is a special case of [move] operation.
 *
 * Don't confuse this with [groupBy],
 * which groups the dataframe by the values in the selected columns!
```

#### Documentation website link 

Add a link to the corresponding operation in the 
[documentation website](https://kotlin.github.io/dataframe).

Please add it as an interface KDoc inside 
[DocumentationUrls](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/documentation/DocumentationUrls.kt)
and then use add it using `@include`:

```
 * For more information: {@include [DocumentationUrls.Move]}
```

#### Columns selection information

For any method with columns selection, add a section with information about the columns selection.

Usually, just `@include` [custom SelectingOptions](#selecting-options).

#### Examples section

Write meaningful, easy-to-undertand examples, with detailed comments.

For complex operations, write a **complete** example with all steps.

For methods with Columns Selection DSL, add several examples with different CS DSL selection methods.

Start the section with

```
### Examples
```

#### Parameters and return section

Describe parameters and return of the method using `@param` and `@return` tags. 
Remember about type parameters.

Wrap parameter names into `[]` for better readability.

## Helper KDoc Interfaces Structure

For more information, see [KoDEx Conventions in DataFrame](../KDOC_PREPROCESSING.md#kodex-conventions-in-dataframe).

Sometimes, you do not need helper interfaces with KoDEx temples at all -
for simple operations, it's enough to write a short KDoc.

However, if you want to reuse some common parts of KDocs 
(for example, for different overloads of the same method or very similar methods),
it's better to use some helper interfaces.

Complex operations 

Here's a standard structure for them:

```kotlin
// Main KDoc helper interface

/**
 * (First line)
 * 
 * (Body)
 * 
 * (Documentation website link)
 * 
 * (See also section)
 */
internal interface ~OperationName~Docs {

    // `SelectingColumns` helper KDoc with this operation in examples
    // - for operations with columns selection
    /**
     * @comment Version of [SelectingColumns] with correctly filled in examples
     * @include [SelectingColumns] {@include [Set~OperationName~OperationArg]}
     */
    typealias ~OperationName~Options = Nothing

    // Operation Grammar - for the initial method of the complex operations
    /**
     * ## ~OperationName~ Operation Grammar
     * ...
     */
    interface Grammar
}

// Set operation in [SelectingColumns] examples (in [SelectingColumns.Dsl] and so on)
/** @set [SelectingColumns.OPERATION] [~operationName~][~operation~] */
@ExcludeFromSources
private typealias Set~OperationName~OperationArg = Nothing

// Common KDoc part for different overloads of the same method
/**
 * @include [~OperationName~Docs]
 * ### This ~OperationName~ Overload
 */
@ExcludeFromSources
private interface Common~OperationName~Docs

/**
 * (Include common docs)
 * @include [Common~OperationName~Docs]
 * 
 * (Columns selection information)
 * @include [SelectingColumns.Dsl] {@include [Set~OperationName~OperationArg]}
 * 
 * (Examples section)
 * 
 * (Parameters and return section)
 */
public fun <T, C> DataFrame<T>.operation(columns: ColumnsSelector<T, C>)
```
