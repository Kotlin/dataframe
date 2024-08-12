## Serialization format for the Kotlin notebooks plugin
This document is an informal specification of the serialization format used for 
rendering Kotlin dataframes in the Kotlin notebooks plugin of IntelliJ IDEA.

### Version 2.1.0

**1.0.0:**

 * ...

**2.0.0:**
 * ...

**2.1.0:**
 * Added a `types` property to dataframe and row metadata. It contains column
   information for all groups, frames and values.

**2.1.1:**
 * Added a new type of `ValueColumn` value that is DataFrameConvertable type

### Top level json structure
```json
{
    "$version": "2.1.0",
    "metadata": {
        "columns": [ string, ... ], // column names
        "types": [ TypeDescriptor, ... ] // type description for each entry in "columns"
        "nrow": int,
        "ncol": int
    },
    "kotlin_dataframe": [ Row, ... ]
}
```

### Row
```json
{ 
    "<value_column_name1>": string|Boolean|Double|Int|Float|Long|Short|Byte|list|DataFrameConvertable,
    "<value_column_name2>": string|Boolean|Double|Int|Float|Long|Short|Byte|list|DataFrameConvertable,
    ...
    "<column_group_name1>": ColumnGroup,
    "<column_group_name2>": ColumnGroup,
    ...
    "<frame_column_name1>": NestedFrame,
    "<frame_column_name2>": NestedFrame
}
```

### ColumnGroup
```json
{
    "metadata": {
        "kind": "ColumnGroup",
        "columns": [ string, ... ], // column names in the group
        "types": [ TypeDescriptor, ... ] // type description for each entry in "columns"
    },
    "data": Row
}
```

### NestedFrame
```json
{
    "metadata": {
        "kind": "FrameColumn"
        "columns": [ string, ... ], // column names in the frame
        "types": [ TypeDescriptor, ... ] // type description for each entry in "columns"
        "nrow": int,
        "ncol": int
    },
    "data": [ Row, ... ]
}
```

### TypeDescriptor
```json
{
    "kind": "ValueColumn"|"ColumnGroup"|"FrameColumn"
    "type": FQN + nullability identifier (?), e.g "Kotlin.String?" // Only available if kind == "ValueColumn"
}
```

### DataFrameConvertable
```json
{
    "metadata": {
        "kind": "DataFrameConvertable"
    },
    "data": [ Row, ... ]
}
```



