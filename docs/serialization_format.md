## Serialization format for the Kotlin notebooks plugin
This document is an informal specification of the serialization format used for 
rendering Kotlin dataframes in the Kotlin notebooks plugin of IntelliJ IDEA.

### Version 2.2.0

**1.0.0:**

 * ...

**2.0.0:**
 * ...

**2.1.0:**
 * Added a `types` property to dataframe and row metadata. It contains column
   information for all groups, frames and values.

**2.1.1:**
 * Added a new type of `ValueColumn` value that is DataFrameConvertable type

**2.2.0:**
 * Added the `is_formatted` property in the `metadata` of the dataframe.
   It's a marker that indicates that the dataframe is the result of a `FormattedFrame`
   or it contains a `DataColumn<FormattedFrame<*>>` at any depth.
   This currently means the rendering should be handled by the HTML renderer instead of the "native" one.
   This property may also be used in the future if the "native" renderer ever gains formatting capabilities.

### Top level json structure
```json
{
    "$version": "2.2.0",
    "metadata": {
        "columns": [ string, ... ], // column names
        "types": [ TypeDescriptor, ... ] // type description for each entry in "columns"
        "nrow": int,
        "ncol": int,
        "is_formatted": boolean
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



