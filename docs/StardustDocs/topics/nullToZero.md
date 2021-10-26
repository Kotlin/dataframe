[//]: # (title: nullToZero)

Replace `null` values with `0`. Works for `Int`, `Double`, `Long` and `BigDecimal` columns.
```kotlin
df.nullToZero { columns }
```
