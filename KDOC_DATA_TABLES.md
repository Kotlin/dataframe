# Data tables in KDoc examples

Read this only when you are adding before/after data tables to a KDoc example.
For everything else about KDocs, see [KDoc Guidelines](KDOC_GUIDELINES.md).

KDoc renders Markdown tables without borders, so the default is to keep the data out of the KDoc:
show the call, say in one sentence what it does, and link to the operation's page on the
documentation website.

Tables are the exception. Add them only for an uncommon operation whose result a sentence cannot
convey (`xs`, `pivot`), and only when the task asks for them.

Keep the input in one KDoc-snippet that all overloads share:

````kotlin
internal interface ~OperationName~Docs {

    /**
     * The examples below use this [DataFrame]:
     *
     * | name/firstName | age | city   |
     * | :------------- | :-- | :----- |
     * | Alice          | 15  | London |
     * | Charlie        | 40  | Milan  |
     */
    @ExcludeFromSources
    typealias ExampleDataSnippet = Nothing
}
````

Each overload includes it, then the call and its result:

````kotlin
/**
 * ### Example
 *
 * {@include [~OperationName~Docs.ExampleDataSnippet]}
 *
 * (One sentence saying what this call does.)
 *
 * ```
 * df.~operationName~("Charlie")
 * ```
 * | age | city  |
 * | :-- | :---- |
 * | 40  | Milan |
 */
````

- Reuse the dataset and the calls from the operation's page on the documentation website (its
  samples use the `df` from `samples/api/TestBase.kt`), and link to that page, so the KDoc, the
  website and the tests all show one example.
- Write a nested column as `group/column` in the table header.
- Copy every table from a real run of the call, never from reading the implementation.
- Back every table with a test that asserts exactly that result, and mark those tests, so the next
  editor knows the tables depend on them.
