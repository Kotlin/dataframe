package org.jetbrains.kotlinx.dataframe.util

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import java.math.BigDecimal
import kotlin.reflect.typeOf

internal val STRING by lazy { typeOf<String>() }
internal val BOOLEAN by lazy { typeOf<Boolean>() }
internal val BYTE by lazy { typeOf<Byte>() }
internal val SHORT by lazy { typeOf<Short>() }
internal val INT by lazy { typeOf<Int>() }
internal val LONG by lazy { typeOf<Long>() }
internal val FLOAT by lazy { typeOf<Float>() }
internal val DOUBLE by lazy { typeOf<Double>() }
internal val CHAR by lazy { typeOf<Char>() }
internal val UBYTE by lazy { typeOf<UByte>() }
internal val USHORT by lazy { typeOf<UShort>() }
internal val UINT by lazy { typeOf<UInt>() }
internal val ULONG by lazy { typeOf<ULong>() }
internal val ANY by lazy { typeOf<Any>() }
internal val UNIT by lazy { typeOf<Unit>() }
internal val NUMBER by lazy { typeOf<Number>() }
internal val BIG_DECIMAL by lazy { typeOf<BigDecimal>() }
internal val ANY_FRAME by lazy { typeOf<AnyFrame>() }
internal val ANY_ROW by lazy { typeOf<AnyRow>() }
internal val LIST_ANY_FRAME by lazy { typeOf<List<AnyFrame>>() }
internal val LOCAL_DATE by lazy { typeOf<kotlinx.datetime.LocalDate>() }
internal val LOCAL_DATE_TIME by lazy { typeOf<kotlinx.datetime.LocalDateTime>() }
internal val LOCAL_TIME by lazy { typeOf<kotlinx.datetime.LocalTime>() }
internal val INSTANT by lazy { typeOf<kotlinx.datetime.Instant>() }
internal val URL by lazy { typeOf<java.net.URL>() }
internal val IMG by lazy { typeOf<IMG>() }
internal val NOTHING by lazy { typeOf<List<Nothing>>().arguments.first().type!! }

internal val NULLABLE_STRING by lazy { typeOf<String?>() }
internal val NULLABLE_BOOLEAN by lazy { typeOf<Boolean?>() }
internal val NULLABLE_BYTE by lazy { typeOf<Byte?>() }
internal val NULLABLE_SHORT by lazy { typeOf<Short?>() }
internal val NULLABLE_INT by lazy { typeOf<Int?>() }
internal val NULLABLE_LONG by lazy { typeOf<Long?>() }
internal val NULLABLE_FLOAT by lazy { typeOf<Float?>() }
internal val NULLABLE_DOUBLE by lazy { typeOf<Double?>() }
internal val NULLABLE_CHAR by lazy { typeOf<Char?>() }
internal val NULLABLE_UBYTE by lazy { typeOf<UByte?>() }
internal val NULLABLE_USHORT by lazy { typeOf<UShort?>() }
internal val NULLABLE_UINT by lazy { typeOf<UInt?>() }
internal val NULLABLE_ULONG by lazy { typeOf<ULong?>() }
internal val NULLABLE_ANY by lazy { typeOf<Any?>() }
internal val NULLABLE_NUMBER by lazy { typeOf<Number?>() }
internal val NULLABLE_BIG_DECIMAL by lazy { typeOf<BigDecimal?>() }
internal val NULLABLE_ANY_FRAME by lazy { typeOf<AnyFrame?>() }
internal val NULLABLE_ANY_ROW by lazy { typeOf<AnyRow?>() }
internal val NULLABLE_LIST_ANY_FRAME by lazy { typeOf<List<AnyFrame>?>() }
internal val NULLABLE_LOCAL_DATE by lazy { typeOf<kotlinx.datetime.LocalDate?>() }
internal val NULLABLE_LOCAL_DATE_TIME by lazy { typeOf<kotlinx.datetime.LocalDateTime?>() }
internal val NULLABLE_LOCAL_TIME by lazy { typeOf<kotlinx.datetime.LocalTime?>() }
internal val NULLABLE_INSTANT by lazy { typeOf<kotlinx.datetime.Instant?>() }
internal val NULLABLE_URL by lazy { typeOf<java.net.URL?>() }
internal val NULLABLE_IMG by lazy { typeOf<IMG?>() }
internal val NULLABLE_NOTHING by lazy { typeOf<List<Nothing?>>().arguments.first().type!! }
