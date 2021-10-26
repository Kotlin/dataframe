[//]: # (title: convert)

Changes the type of columns. Supports automatic type conversions between value types `Int`, `String`, `Double`, `Long`, `Short`, `Float`,`BigDecimal`, 'LocalDateTime', 'LocalDate', 'LocalTime'
```kotlin
df.convert { age }.to<Double>()
df.convert { age }.with { it.toString() }
```
Helper functions for value types are also available
```kotlin
df.convert { age }.toFloat()
df.convert { all() }.toStr()
df.convert { timestamp }.toDateTime()
``` 
