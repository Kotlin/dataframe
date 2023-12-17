[//]: # (title: NaN and NA)

Using the Kotlin DataFrame library, you might come across the terms `NaN` and `NA`. 
This page explains what they mean and how to work with them.

## NaN

`Float` or `Double` values can be represented as `NaN`,
in cases where a mathematical operation is undefined, such as for dividing by zero. The
result of such an operation can only be described as "**N**ot **a** **N**umber".

This is different from `null`, which means that a value is missing and, in Kotlin, can only occur
for `Float?` and `Double?` types.

You can use [fillNaNs](fill.md#fillnans) to replace `NaNs` in certain columns with a given value or expression
or [dropNaNs](drop.md#dropnans) to drop rows with `NaNs` in them.

## NA

`NA` in Dataframe can be seen as: [`NaN`](#nan) or `null`. Which is another way to say that the value
is "**N**ot **A**vailable".

You can use [fillNA](fill.md#fillna) to replace `NAs` in certain columns with a given value or expression
or [dropNA](drop.md#dropna) to drop rows with `NAs` in them.
