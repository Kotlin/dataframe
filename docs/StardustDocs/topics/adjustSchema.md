[//]: # (title: Adjust schema)

`DataFrame` interface has type argument `T` that doesn't affect contents of `DataFrame`, but marks `DataFrame` with `DataSchema` that it is supposed to have.
This argument is used to generate [extension properties](extensionPropertiesApi.md) for typed data access. 

There is no guarantee, that this compile-time schema matches actual data stored in `DataFrame` due to dynamic nature of data inside `DataFrame`. However, at some points of code you can know exactly what `DataFrame` schema is expected.
In order match compile-time knowledge with expected real-time `DataFrame` contents you can you one of two functions:
* [`cast`](cast.md) - change type argument of `DataFrame` to the expected schema without changing data in `DataFrame`.
* [`convertTo`](convertTo.md) - convert `DataFrame` contents to the expected schema.
