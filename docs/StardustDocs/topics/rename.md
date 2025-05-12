[//]: # (title: rename)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Renames one or several columns without changing its location in [`DataFrame`](DataFrame.md).

```kotlin
df.rename { columns }.into(name)
df.rename { columns }.into { nameExpression }

nameExpression = (DataColumn) -> String
```

<!---FUN rename-->
<tabs>
<tab title="Properties">

```kotlin
df.rename { name }.into("fullName")
```

</tab>
<tab title="Strings">

```kotlin
df.rename("name").into("fullName")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.rename.html" width="100%"/>
<!---END-->


<!---FUN renameExpression-->
<tabs>
<tab title="Properties">

```kotlin
df.rename { age }.into {
    val mean = it.data.mean()
    "age [mean = $mean]"
}
```

</tab>
<tab title="Strings">

```kotlin
df.rename("age").into {
    val mean = it.data.cast<Int>().mean()
    "age [mean = $mean]"
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.renameExpression.html" width="100%"/>
<!---END-->

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.RenameToCamelCase-->

## renameToCamelCase

A special case of `rename` that renames all column names in a DataFrame to "camelCase" format.

This function standardizes column names by converting them from various naming styles—such as "snake_case",
"PascalCase", "kebab-case", or "space delimited formats" into a consistent "camelCase" form.

The transformation follows Kotlin naming conventions for variables and properties, making the resulting
column names idiomatic and easier to use in Kotlin code, which is especially useful
in [extension properties usage](extensionPropertiesApi.md).

Use it right after loading raw data to normalize column names into a consistent format.
This is especially helpful when preparing the data for further transformations, column access,
or integration with Kotlin APIs—making the DataFrame easier and more predictable to work with.

### Examples

<!---FUN notebook_test_rename_3-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_rename_3.html" width="100%" height="500px"></inline-frame>

Rename selected columns to "camelCase":

<!---FUN notebook_test_rename_4-->

```kotlin
df.rename { ColumnA and `COLUMN-C` }.toCamelCase()
```

<!---END-->

<inline-frame src="./resources/notebook_test_rename_4.html" width="100%" height="500px"></inline-frame>

Rename all columns (including nested) to "camelCase":

<!---FUN notebook_test_rename_5-->

```kotlin
df.renameToCamelCase()
```

<!---END-->

<inline-frame src="./resources/notebook_test_rename_5.html" width="100%" height="500px"></inline-frame>

### Transformation Rules and Examples

* Delimiters (underscores, dashes, spaces, etc.) are removed.
* The first word is kept lowercase; all subsequent words are capitalized.
* An underscore is inserted between consecutive numbers to improve readability.
* If a name contains no letters or digits, it remains unchanged.

| Original                         | camelCase                    |
|----------------------------------|------------------------------|
| hello_world                      | helloWorld                   |
| HelloWorld                       | helloWorld                   |
| json.parser.Config               | jsonParserConfig             |
| my.var_name test                 | myVarNameTest                |
| thirdColumn                      | thirdColumn                  |
| someHTMLParser                   | someHtmlParser               |
| RESTApi                          | restApi                      |
| OAuth2Token                      | oAuth2Token                  |
| GraphQLQuery                     | graphQlQuery                 |
| TCP_3_PROTOCOL                   | tcp3Protocol                 |
| 123hello_world456                | 123HelloWorld456             |
| API_Response_2023                | apiResponse2023              |
| UPPER_case-LOWER                 | upperCaseLower               |
| 12parse34CamelCase               | 12Parse34CamelCase           |
| snake_case_example               | snakeCaseExample             |
| dot.separated.words              | dotSeparatedWords            |
| kebab-case-example               | kebabCaseExample             |
| MIXED_Case_with_123Numbers       | mixedCaseWith123Numbers      |
| ___!!!___                        | ___!!!___                    |
| 1000.2000.3000                   | 1000_2000_3000               |
| UPPERCASE                        | uppercase                    |
| alreadyCamelCased                | alreadyCamelCased            |
| justNumbers123                   | justNumbers123               |
| Just_Special$Chars!!             | justSpecialChars             |
| singleword                       | singleword                   |
| word_with_underscores_and-dashes | wordWithUnderscoresAndDashes |
| 10-20-aa                         | 10_20Aa                      |


