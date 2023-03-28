[//]: # (title: Adjust schema)

[`DataFrame`](DataFrame.md) interface has type argument `T` that doesn't affect contents of [`DataFrame`](DataFrame.md), 
but marks [`DataFrame`](DataFrame.md) with a type that represents data schema that this [`DataFrame`](DataFrame.md) is supposed to have.
This argument is used to generate [extension properties](extensionPropertiesApi.md) for typed data access.

Another place where this argument has a special role is in [interop with data classes](collectionsInterop.md#interop-with-data-classes):
* `List<T>` -> `DataFrame<T>`: [toDataFrame](createDataFrame.md#todataframe)
* `DataFrame<T>` -> `List<T>`: [toList](toList.md)

Actual data in [`DataFrame`](DataFrame.md) may diverge from compile-time schema marker `T` due to dynamic nature of data inside [`DataFrame`](DataFrame.md). 
However, at some points of code you may know exactly what [`DataFrame`](DataFrame.md) schema is expected.
To match your knowledge with expected real-time [`DataFrame`](DataFrame.md) contents you can use one of two functions:
* [`cast`](cast.md) — change type argument of [`DataFrame`](DataFrame.md) to the expected schema without changing data in [`DataFrame`](DataFrame.md).
* [`convertTo`](convertTo.md) — convert [`DataFrame`](DataFrame.md) contents to match the expected schema.

