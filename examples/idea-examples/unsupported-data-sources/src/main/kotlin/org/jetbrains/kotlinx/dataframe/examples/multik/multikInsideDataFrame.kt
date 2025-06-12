package org.jetbrains.kotlinx.dataframe.examples.multik

import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.mapToFrame
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.rand
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array
import org.jetbrains.kotlinx.multik.ndarray.data.D4Array
import java.time.Month.JULY

/**
 * DataFrames can store anything inside, including Multik ndarrays.
 * This can be useful for storing matrices for easier access later or to simply organize data read from other files.
 * For example, MRI data is often stored as 3D arrays and sometimes even 4D arrays.
 */
fun main() {
    // imaginary list of patient data
    @Suppress("ktlint:standard:argument-list-wrapping")
    val metadata = listOf(
        MriMetadata(10012L, 25, "Healthy", LocalDate(2023, 1, 1)),
        MriMetadata(10013L, 45, "Tuberculosis", LocalDate(2023, 2, 15)),
        MriMetadata(10014L, 32, "Healthy", LocalDate(2023, 3, 22)),
        MriMetadata(10015L, 58, "Pneumonia", LocalDate(2023, 4, 8)),
        MriMetadata(10016L, 29, "Tuberculosis", LocalDate(2023, 5, 30)),
        MriMetadata(10017L, 42, "Healthy", LocalDate(2023, 6, 15)),
        MriMetadata(10018L, 37, "Healthy", LocalDate(2023, 7, 1)),
        MriMetadata(10019L, 55, "Healthy", LocalDate(2023, 8, 15)),
        MriMetadata(10020L, 28, "Healthy", LocalDate(2023, 9, 1)),
        MriMetadata(10021L, 44, "Healthy", LocalDate(2023, 10, 15)),
        MriMetadata(10022L, 31, "Healthy", LocalDate(2023, 11, 1)),
    ).toDataFrame()

    // "reading" the results from "files"
    val results = metadata.mapToFrame {
        +patientId
        +age
        +diagnosis
        +scanDate
        "t1WeightedMri" from { readT1WeightedMri(patientId) }
        "fMriBoldSeries" from { readFMRiBoldSeries(patientId) }
    }.cast<MriResults>(verify = true)
        .append()

    results.print(borders = true)

    // now when we want to check and visualize the T1-weighted MRI scan
    // for that one healthy patient in July, we can do:
    val scan = results
        .single { scanDate.month == JULY && diagnosis == "Healthy" }
        .t1WeightedMri

    // easy :)
    visualize(scan)
}

@DataSchema
data class MriMetadata(
    /** Unique patient ID. */
    val patientId: Long,
    /** Patient age. */
    val age: Int,
    /** Clinical diagnosis (e.g. "Healthy", "Tuberculosis") */
    val diagnosis: String,
    /** Date of the scan */
    val scanDate: LocalDate,
)

@DataSchema
data class MriResults(
    /** Unique patient ID. */
    val patientId: Long,
    /** Patient age. */
    val age: Int,
    /** Clinical diagnosis (e.g. "Healthy", "Tuberculosis") */
    val diagnosis: String,
    /** Date of the scan */
    val scanDate: LocalDate,
    /**
     * T1-weighted anatomical MRI scan.
     *
     * Dimensions: (256 x 256 x 180)
     * - 256 width x 256 height
     * - 180 slices
     */
    val t1WeightedMri: D3Array<Float>,
    /**
     * Blood oxygenation level-dependent (BOLD) time series from an fMRI scan.
     *
     * Dimensions: (64 x 64 x 30 x 200)
     * - 64 width x 64 height
     * - 30 slices
     * - 200 timepoints
     */
    val fMriBoldSeries: D4Array<Float>,
)

fun readT1WeightedMri(id: Long): D3Array<Float> {
    // This should in practice, of course, read the actual data, but for this example we just return a dummy array
    return mk.rand(256, 256, 180)
}

fun readFMRiBoldSeries(id: Long): D4Array<Float> {
    // This should in practice, of course, read the actual data, but for this example we just return a dummy array
    return mk.rand(64, 64, 30, 200)
}

fun visualize(scan: D3Array<Float>) {
    // This would then actually visualize the scan
}
