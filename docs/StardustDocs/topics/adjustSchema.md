[//]: # (title: Adjust schema)

`DataFrame` interface has type argument `T` that doesn't affect contents of `DataFrame`, but marks `DataFrame` with a type that represents data schema that this `DataFrame` is supposed to have.
This argument is used to generate [extension properties](extensionPropertiesApi.md) for typed data access. 

Actual data in `DataFrame` may diverge from compile-time schema marker `T` due to dynamic nature of data inside `DataFrame`. However, at some points of code you may know exactly what `DataFrame` schema is expected.
In order match compile-time knowledge with expected real-time `DataFrame` contents you can use of two functions:
* [`cast`](cast.md) - change type argument of `DataFrame` to the expected schema without changing data in `DataFrame`.
* [`convertTo`](convertTo.md) - convert `DataFrame` contents to match the expected schema.
