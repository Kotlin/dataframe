import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.typeNameOf
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path

val InputType.inputParameterName: String
    get() = when (this) {
        InputType.FILE -> "file"
        InputType.URL -> "url"
        InputType.INPUT_STREAM -> "inputStream"
        InputType.PATH -> "path"
        InputType.FILE_OR_URL -> "fileOrUrl"
    }

fun inputParameterOf(inputType: InputType): ParameterSpec {
    val name = inputType.inputParameterName
    return when (inputType) {
        InputType.FILE ->
            ParameterSpec.Companion.builder(name, typeNameOf<File>())
                .addKdoc(
                    "The file to read. Can also be compressed as `.gz` or `.zip`, see [Compression].",
                )
                .build()

        InputType.URL ->
            ParameterSpec.Companion.builder(name, typeNameOf<URL>())
                .addKdoc(
                    "The URL from which to fetch the data. Can also be compressed as `.gz` or `.zip`, see [Compression].",
                )
                .build()

        InputType.INPUT_STREAM ->
            ParameterSpec.Companion.builder(name, typeNameOf<InputStream>())
                .addKdoc("Represents the file to read.")
                .build()

        InputType.PATH ->
            ParameterSpec.Companion.builder(name, typeNameOf<Path>())
                .addKdoc(
                    "The file path to read. Can also be compressed as `.gz` or `.zip`, see [Compression].",
                )
                .build()

        InputType.FILE_OR_URL ->
            ParameterSpec.Companion.builder(name, typeNameOf<String>())
                .addKdoc(
                    "The file path or URL to read the data from. Can also be compressed as `.gz` or `.zip`, see [Compression].",
                )
                .build()
    }
}
