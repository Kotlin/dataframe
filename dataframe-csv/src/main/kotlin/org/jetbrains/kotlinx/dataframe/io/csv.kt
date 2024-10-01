package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.File
import java.io.InputStream

@ExperimentalCsv
public class Csv : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): DataFrame<*> {
        TODO("Not yet implemented")
    }

    override fun readDataFrame(file: File, header: List<String>): DataFrame<*> {
        TODO("Not yet implemented")
    }

    override fun acceptsExtension(ext: String): Boolean = ext == "csv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = CSV().testOrder + 1 // make sure the non-experimental implementation is the default

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        TODO("Not yet implemented")
    }
}
