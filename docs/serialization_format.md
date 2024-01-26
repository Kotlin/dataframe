## Serialization format for the Kotlin notebooks plugin
This document is an informal specification of the serialization format used for rendering Kotlin dataframes in the Kotlin notebooks plugin of IntelliJ IDEA.

### Version 2.0.0
### Top level json structure
```json
{
    "$version": "2.0.0",
    "metadata": {
        "columns": [ string, ... ], // column names
        "nrow": int,
        "ncol": int
    },
    "kotlin_dataframe": [ Row, ... ]
}
```
### Row
```json
{ 
    "<value_column_name1>": string|Boolean|Double|Int|Float|Long|Short|Byte|list,,
    "<value_column_name2>": string|Boolean|Double|Int|Float|Long|Short|Byte|list,,
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
        "kind": "ColumnGroup"
    },
    "data": Row
}
```
### NestedFrame
```json
{
    "metadata": {
        "kind": "FrameColumn"
        "nrow": int,
        "ncol": int
    },
    "data": [ Row, ... ]
}
```
