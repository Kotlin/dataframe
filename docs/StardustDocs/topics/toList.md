[//]: # (title: toList)

Converts `DataFrame` into a `List` of data class instances by current `DataFrame` type argument.

```
toList()
```

Type of data class is defined by current type argument of `DataFrame`. If this type argument is not data class, exception will be thrown.

Data class properties are matched with `DataFrame` columns by name. If property type differs from column type [type conversion](convert.md) will be performed. If no automatic type conversion was found, exception will be thrown. 

To export `DataFrame` into specific type of data class, use `toListOf`:

## toListOf

Converts `DataFrame` into a `List` of instances of given data class.

```
toListOf<T>()
```
