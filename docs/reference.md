# DataFrame API Reference

**Table of contents**
<!--- TOC -->

* Build
    * from values
    * from object 
* [Read](#read)
    * from CSV
    * from JSON
* Analyze
    * `schema`
    * `summary`
* Access Data
    * by column
    * by row
    * with iterator 
* Modify rows
    * `filter` 
    * `sortBy` / `sortByDesc`
    * `mergeRows`
    * `distinct`
    * `append`
    * `groupBy`
    * `shuffled`
    * `take` / `takeLast`
    * `drop` / `dropLast`
* Modify schema
    * `select`
    * `add`
    * `move`
    * `remove`
    * `cast`
    * `parse`
    * `split`
    * `mergeCols`
    * `group`
    * `ungroup`
    * `flatten`
    * `rename`
    * `replace`
    * `gather`
    * `spread`    
* Update data
    * `update`
    * `fillNulls`
    * `nullToZero`
* Merge several data frames
    * `union`
    * `join`
* Modify column
    * `distinct`
    * `digitize`
    * arithmetic operations
* Column statistics
    * `sum`
    * `min` / `max`
    * `mean`
    * `median`
* Grouped data frame aggregation
    * `spread`
    * `countBy`
* Export
    * `writeCSV`

<!--- END -->

## Read

DataFrame supports CSV and JSON input formats:
```kotlin
DataFrame.readCSV("input.csv")
DataFrame.readJSON("https://covid.ourworldindata.org/data/owid-covid-data.json")
```
General `read` method tries to guess input format based on file extension and content:
```kotlin
DataFrame.read("input.csv")
```