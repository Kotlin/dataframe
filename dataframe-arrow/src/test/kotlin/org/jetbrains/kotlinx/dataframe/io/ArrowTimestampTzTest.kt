package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import org.apache.arrow.vector.TimeStampMicroTZVector
import org.apache.arrow.vector.TimeStampMicroVector
import org.apache.arrow.vector.TimeStampMilliTZVector
import org.apache.arrow.vector.TimeStampNanoTZVector
import org.apache.arrow.vector.TimeStampSecTZVector
import org.apache.arrow.vector.TimeStampVector
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import java.net.URL
import kotlin.io.path.toPath
import kotlin.reflect.typeOf
import kotlin.time.Instant

/**
 * Reading Arrow/Parquet timestamps that carry a timezone — `ArrowType.Timestamp(unit, tz)`, which is what
 * Parquet's `isAdjustedToUTC = true` becomes ([issue #926](https://github.com/Kotlin/dataframe/issues/926)).
 *
 * Such a value is an offset from the Unix epoch already normalized to UTC, so it identifies a single instant
 * and is read as an [Instant]. A zone-less timestamp is a calendar/clock reading that identifies no instant,
 * and stays a [LocalDateTime] — see
 * [Parquet logical types](https://github.com/apache/parquet-format/blob/master/LogicalTypes.md#timestamp).
 *
 * Arrow is wider than Parquet here: it has a `SECOND` unit and allows any zone string, neither of which any
 * Parquet file can produce. So the unit/zone matrix is crafted in code and round-tripped through both IPC and
 * Feather, while the committed Parquet fixtures cover interop with what PyArrow/Polars actually write.
 */
internal class ArrowTimestampTzTest {

    private fun testResource(resourcePath: String): URL =
        ArrowTimestampTzTest::class.java.classLoader.getResource(resourcePath)!!

    // region crafted Arrow files: the unit x zone matrix Parquet cannot express

    /** Nanosecond-precision instants: one ordinary, one null, one **before** the epoch. */
    private val instants = listOf(
        Instant.parse("2024-01-01T12:00:00.123456789Z"),
        null,
        Instant.parse("1962-06-05T04:03:02.123456789Z"),
    )

    private val truncatedToMicros = listOf(
        Instant.parse("2024-01-01T12:00:00.123456Z"),
        null,
        Instant.parse("1962-06-05T04:03:02.123456Z"),
    )

    private val truncatedToMillis = listOf(
        Instant.parse("2024-01-01T12:00:00.123Z"),
        null,
        Instant.parse("1962-06-05T04:03:02.123Z"),
    )

    private val truncatedToSeconds = listOf(
        Instant.parse("2024-01-01T12:00:00Z"),
        null,
        Instant.parse("1962-06-05T04:03:02Z"),
    )

    private fun Instant.epochNanos(): Long = epochSeconds * 1_000_000_000L + nanosecondsOfSecond

    /**
     * One column per timestamp unit, all tagged [zone], all describing [instants]. Values are stored floored to
     * the column's unit, which is what a producer writing a coarser column does.
     */
    private fun unitMatrixBytes(feather: Boolean, zone: String = "UTC"): ByteArray {
        fun ts(name: String, unit: TimeUnit) = Field(name, FieldType.nullable(ArrowType.Timestamp(unit, zone)), null)

        val fields = arrayOf(
            ts("ts_nano_tz", TimeUnit.NANOSECOND),
            ts("ts_micro_tz", TimeUnit.MICROSECOND),
            ts("ts_milli_tz", TimeUnit.MILLISECOND),
            ts("ts_sec_tz", TimeUnit.SECOND),
        )
        return arrowBytes(*fields, feather = feather) { root ->
            val nano = root.getVector("ts_nano_tz") as TimeStampNanoTZVector
            val micro = root.getVector("ts_micro_tz") as TimeStampMicroTZVector
            val milli = root.getVector("ts_milli_tz") as TimeStampMilliTZVector
            val sec = root.getVector("ts_sec_tz") as TimeStampSecTZVector
            listOf<TimeStampVector>(nano, micro, milli, sec).forEach { it.allocateNew(instants.size) }

            instants.forEachIndexed { i, instant ->
                if (instant == null) {
                    listOf<TimeStampVector>(nano, micro, milli, sec).forEach { it.setNull(i) }
                } else {
                    val epochNanos = instant.epochNanos()
                    nano[i] = epochNanos
                    micro[i] = epochNanos.floorDiv(1_000L)
                    milli[i] = epochNanos.floorDiv(1_000_000L)
                    sec[i] = epochNanos.floorDiv(1_000_000_000L)
                }
            }
            root.setRowCount(instants.size)
        }
    }

    /** Three microsecond columns holding the very same epoch offsets, tagged with three different zones. */
    private fun zoneMatrixBytes(feather: Boolean): ByteArray {
        fun ts(name: String, zone: String) =
            Field(name, FieldType.nullable(ArrowType.Timestamp(TimeUnit.MICROSECOND, zone)), null)

        val fields = arrayOf(
            ts("in_utc", "UTC"),
            ts("in_berlin", "Europe/Berlin"),
            ts("in_offset", "+05:30"),
        )
        return arrowBytes(*fields, feather = feather) { root ->
            val vectors = fields.map { root.getVector(it.name) as TimeStampMicroTZVector }
            vectors.forEach { it.allocateNew(instants.size) }

            instants.forEachIndexed { i, instant ->
                val micros = instant?.epochNanos()?.floorDiv(1_000L)
                vectors.forEach { vector ->
                    if (micros == null) vector.setNull(i) else vector[i] = micros
                }
            }
            root.setRowCount(instants.size)
        }
    }

    private fun assertUnitMatrix(df: AnyFrame) {
        df.columnNames() shouldBe listOf("ts_nano_tz", "ts_micro_tz", "ts_milli_tz", "ts_sec_tz")

        df.columnTypes() shouldBe List(4) { typeOf<Instant?>() }

        df["ts_nano_tz"].values().toList() shouldBe instants
        df["ts_micro_tz"].values().toList() shouldBe truncatedToMicros
        df["ts_milli_tz"].values().toList() shouldBe truncatedToMillis
        df["ts_sec_tz"].values().toList() shouldBe truncatedToSeconds
    }

    @Test
    fun `arrow timestamps with a timezone are read as instants in every unit`() {
        assertUnitMatrix(DataFrame.readArrowFeather(unitMatrixBytes(feather = true)))
        assertUnitMatrix(DataFrame.readArrowIPC(unitMatrixBytes(feather = false)))
    }

    @Test
    fun `nullability options are honoured for timestamps with a timezone`() {
        NullabilityOptions.entries.forEach { nullability ->
            val df = DataFrame.readArrowFeather(unitMatrixBytes(feather = true), nullability)
            df.columnTypes() shouldBe List(4) { typeOf<Instant?>() }
        }
    }

    @Test
    fun `the zone name does not change the instant`() {
        listOf(
            DataFrame.readArrowFeather(zoneMatrixBytes(feather = true)),
            DataFrame.readArrowIPC(zoneMatrixBytes(feather = false)),
        ).forEach { df ->
            df.columnTypes() shouldBe List(3) { typeOf<Instant?>() }

            // The zone is display metadata: the same epoch offsets must read back as the same instants.
            df["in_utc"].values().toList() shouldBe truncatedToMicros
            df["in_berlin"].values().toList() shouldBe truncatedToMicros
            df["in_offset"].values().toList() shouldBe truncatedToMicros
        }
    }

    @Test
    fun `arrow timestamps without a timezone stay local date-times`() {
        fun ts(name: String, zone: String?) =
            Field(name, FieldType.notNullable(ArrowType.Timestamp(TimeUnit.MICROSECOND, zone)), null)

        val bytes = arrowBytes(ts("zoned", "UTC"), ts("zoneless", null), feather = true) { root ->
            val zoned = root.getVector("zoned") as TimeStampMicroTZVector
            val zoneless = root.getVector("zoneless") as TimeStampMicroVector
            zoned.allocateNew(1)
            zoneless.allocateNew(1)
            val micros = Instant.parse("2024-01-01T12:00:00.123456Z").epochNanos().floorDiv(1_000L)
            zoned[0] = micros
            zoneless[0] = micros
            root.setRowCount(1)
        }

        val df = DataFrame.readArrowFeather(bytes)

        df["zoned"].type() shouldBe typeOf<Instant>()
        df["zoneless"].type() shouldBe typeOf<LocalDateTime>()

        df["zoned"].values().toList() shouldBe listOf(Instant.parse("2024-01-01T12:00:00.123456Z"))
        df["zoneless"].values().toList() shouldBe listOf(LocalDateTime(2024, 1, 1, 12, 0, 0, 123_456_000))
    }

    // endregion

    // region writing

    @Test
    fun `instant column round-trips through arrow`() {
        val frame = dataFrameOf(
            DataColumn.createValueColumn("moment", truncatedToMicros, typeOf<Instant?>()),
        )

        listOf(
            DataFrame.readArrowFeather(frame.saveArrowFeatherToByteArray()),
            DataFrame.readArrowIPC(frame.saveArrowIPCToByteArray()),
        ).forEach { df ->
            df["moment"].type() shouldBe typeOf<Instant?>()
            df["moment"].values().toList() shouldBe truncatedToMicros
        }
    }

    @Test
    fun `explicit timestamp target schema writes both flavours`() {
        val localDateTimes = listOf(
            LocalDateTime(2024, 1, 1, 12, 0, 0, 123_000_000),
            null,
            LocalDateTime(1962, 6, 5, 4, 3, 2, 123_000_000),
        )
        val targetSchema = Schema(
            listOf(
                Field("local", FieldType.nullable(ArrowType.Timestamp(TimeUnit.MILLISECOND, null)), null),
                Field("moment", FieldType.nullable(ArrowType.Timestamp(TimeUnit.MICROSECOND, "UTC")), null),
            ),
        )
        val frame = dataFrameOf(
            DataColumn.createValueColumn("local", localDateTimes, typeOf<LocalDateTime?>()),
            DataColumn.createValueColumn("moment", truncatedToMicros, typeOf<Instant?>()),
        )

        val bytes = frame.arrowWriter(targetSchema).use { it.saveArrowFeatherToByteArray() }
        val df = DataFrame.readArrowFeather(bytes)

        df["local"].type() shouldBe typeOf<LocalDateTime?>()
        df["moment"].type() shouldBe typeOf<Instant?>()

        df["local"].values().toList() shouldBe localDateTimes
        df["moment"].values().toList() shouldBe truncatedToMicros
    }

    // endregion

    // region committed fixtures written by Polars/PyArrow

    /**
     * The issue #926 column set, asserted identically whether it came from the Parquet or the Feather fixture:
     * Parquet stores the zone only as the `isAdjustedToUTC` flag while Feather keeps the Arrow schema verbatim,
     * yet both describe the same instants, so both must read back the same way.
     */
    private fun assertIssue926Columns(df: AnyFrame) {
        df.columnNames() shouldBe
            listOf(
                "timestamp_utc",
                "timestamp_local",
                "timestamp_brussels",
                "timestamp_nanos",
                "timestamp_millis",
            )
        df.rowsCount() shouldBe 3

        // Only the `isAdjustedToUTC = false` column is a local date-time; everything else is an instant.
        df["timestamp_utc"].type() shouldBe typeOf<Instant?>()
        df["timestamp_local"].type() shouldBe typeOf<LocalDateTime?>()
        df["timestamp_brussels"].type() shouldBe typeOf<Instant?>()
        df["timestamp_nanos"].type() shouldBe typeOf<Instant?>()
        df["timestamp_millis"].type() shouldBe typeOf<Instant?>()

        df["timestamp_utc"].values().toList() shouldBe truncatedToMicros

        df["timestamp_local"].values().toList() shouldBe
            listOf(
                LocalDateTime(2024, 1, 1, 12, 0, 0, 123_456_000),
                null,
                LocalDateTime(1962, 6, 5, 4, 3, 2, 123_456_000),
            )

        // Written as 12:00:00.123456 in Brussels (UTC+1 on both dates) and normalized on write, so the original
        // zone is gone and only the instant survives — exactly what `isAdjustedToUTC = true` promises.
        df["timestamp_brussels"].values().toList() shouldBe
            listOf(
                Instant.parse("2024-01-01T11:00:00.123456Z"),
                null,
                Instant.parse("1962-06-05T03:03:02.123456Z"),
            )

        // All nine fractional digits survive.
        df["timestamp_nanos"].values().toList() shouldBe instants

        df["timestamp_millis"].values().toList() shouldBe truncatedToMillis
    }

    /**
     * The Arrow-format twin of [parquet timestamps with isAdjustedToUTC are read as instants]. Kept separate
     * because reading Parquet needs the `arrow-dataset` JNI library, which is not available everywhere (Android,
     * for one) — this test still covers PyArrow interop there.
     */
    @Test
    fun `feather timestamps with a timezone are read as instants`() {
        assertIssue926Columns(DataFrame.readArrowFeather(testResource("timestamps_utc_and_local.feather")))
    }

    @Test
    fun `parquet timestamps with isAdjustedToUTC are read as instants`() {
        assertIssue926Columns(DataFrame.readParquet(testResource("timestamps_utc_and_local.parquet").toURI().toPath()))
    }

    @Test
    fun `parquet timestamp keeps the instant when the arrow schema names a non-UTC zone`() {
        val path = testResource("timestamps_zoned.parquet").toURI().toPath()

        val df = DataFrame.readParquet(path)

        df["timestamp_berlin"].type() shouldBe typeOf<Instant?>()
        df["timestamp_berlin"].values().toList() shouldBe truncatedToMicros
    }

    // endregion
}
