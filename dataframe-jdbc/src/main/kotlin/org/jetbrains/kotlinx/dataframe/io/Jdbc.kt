package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.File
import java.io.InputStream
import java.nio.file.Path

// TODO: https://github.com/Kotlin/dataframe/issues/450
public class Jdbc :
    SupportedCodeGenerationFormat,
    SupportedDataFrameFormat {
    public override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readJDBC(stream)

    public override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readJDBC(file)

    public override fun readDataFrame(path: Path, header: List<String>): AnyFrame = DataFrame.readJDBC(path.toFile())

    override fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        generateHelperCompanionObject: Boolean,
    ): Code {
        TODO("Not yet implemented")
    }

    override fun readCodeForGeneration(file: File, name: String, generateHelperCompanionObject: Boolean): Code {
        TODO("Not yet implemented")
    }

    override fun acceptsExtension(ext: String): Boolean = ext == "jdbc"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 40000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadJdbcMethod(pathRepresentation)
}

private fun DataFrame.Companion.readJDBC(stream: File): DataFrame<*> {
    TODO("Not yet implemented")
}

private fun DataFrame.Companion.readJDBC(stream: InputStream): DataFrame<*> {
    TODO("Not yet implemented")
}

internal class DefaultReadJdbcMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, READ_JDBC)

private const val READ_JDBC = "readJDBC"
