package org.jetbrains.kotlinx.dataframe.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import java.math.BigDecimal
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public object TypeOf {
    public val STRING: KType by lazy { typeOf<String>() }
    public val BOOLEAN: KType by lazy { typeOf<Boolean>() }
    public val BYTE: KType by lazy { typeOf<Byte>() }
    public val SHORT: KType by lazy { typeOf<Short>() }
    public val INT: KType by lazy { typeOf<Int>() }
    public val LONG: KType by lazy { typeOf<Long>() }
    public val FLOAT: KType by lazy { typeOf<Float>() }
    public val DOUBLE: KType by lazy { typeOf<Double>() }
    public val CHAR: KType by lazy { typeOf<Char>() }
    public val UBYTE: KType by lazy { typeOf<UByte>() }
    public val USHORT: KType by lazy { typeOf<UShort>() }
    public val UINT: KType by lazy { typeOf<UInt>() }
    public val ULONG: KType by lazy { typeOf<ULong>() }
    public val ANY: KType by lazy { typeOf<Any>() }
    public val UNIT: KType by lazy { typeOf<Unit>() }
    public val NUMBER: KType by lazy { typeOf<Number>() }
    public val BIG_DECIMAL: KType by lazy { typeOf<BigDecimal>() }
    public val ANY_FRAME: KType by lazy { typeOf<AnyFrame>() }
    public val ANY_ROW: KType by lazy { typeOf<AnyRow>() }
    public val LIST_ANY_FRAME: KType by lazy { typeOf<List<AnyFrame>>() }
    public val LOCAL_DATE: KType by lazy { typeOf<LocalDate>() }
    public val LOCAL_DATE_TIME: KType by lazy { typeOf<LocalDateTime>() }
    public val LOCAL_TIME: KType by lazy { typeOf<LocalTime>() }
    public val INSTANT: KType by lazy { typeOf<Instant>() }
    public val URL: KType by lazy { typeOf<URL>() }
    public val IMG: KType by lazy { typeOf<IMG>() }
    public val NOTHING: KType by lazy { typeOf<List<Nothing>>().arguments.first().type!! }
    public val NULLABLE_STRING: KType by lazy { typeOf<String?>() }
    public val NULLABLE_BOOLEAN: KType by lazy { typeOf<Boolean?>() }
    public val NULLABLE_BYTE: KType by lazy { typeOf<Byte?>() }
    public val NULLABLE_SHORT: KType by lazy { typeOf<Short?>() }
    public val NULLABLE_INT: KType by lazy { typeOf<Int?>() }
    public val NULLABLE_LONG: KType by lazy { typeOf<Long?>() }
    public val NULLABLE_FLOAT: KType by lazy { typeOf<Float?>() }
    public val NULLABLE_DOUBLE: KType by lazy { typeOf<Double?>() }
    public val NULLABLE_CHAR: KType by lazy { typeOf<Char?>() }
    public val NULLABLE_UBYTE: KType by lazy { typeOf<UByte?>() }
    public val NULLABLE_USHORT: KType by lazy { typeOf<UShort?>() }
    public val NULLABLE_UINT: KType by lazy { typeOf<UInt?>() }
    public val NULLABLE_ULONG: KType by lazy { typeOf<ULong?>() }
    public val NULLABLE_ANY: KType by lazy { typeOf<Any?>() }
    public val NULLABLE_NUMBER: KType by lazy { typeOf<Number?>() }
    public val NULLABLE_BIG_DECIMAL: KType by lazy { typeOf<BigDecimal?>() }
    public val NULLABLE_ANY_FRAME: KType by lazy { typeOf<AnyFrame?>() }
    public val NULLABLE_ANY_ROW: KType by lazy { typeOf<AnyRow?>() }
    public val NULLABLE_LIST_ANY_FRAME: KType by lazy { typeOf<List<AnyFrame>?>() }
    public val NULLABLE_LOCAL_DATE: KType by lazy { typeOf<LocalDate?>() }
    public val NULLABLE_LOCAL_DATE_TIME: KType by lazy { typeOf<LocalDateTime?>() }
    public val NULLABLE_LOCAL_TIME: KType by lazy { typeOf<LocalTime?>() }
    public val NULLABLE_INSTANT: KType by lazy { typeOf<Instant?>() }
    public val NULLABLE_URL: KType by lazy { typeOf<URL?>() }
    public val NULLABLE_IMG: KType by lazy { typeOf<IMG?>() }
    public val NULLABLE_NOTHING: KType by lazy { typeOf<List<Nothing?>>().arguments.first().type!! }
}
