package org.jetbrains.kotlinx.dataframe.jupyter

import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path

public class ImportDataSchema(public val url: URL) {
    public constructor(path: String) : this(URI(path).toURL())
    public constructor(path: Path) : this(path.toUri().toURL())
    public constructor(file: File) : this(file.toURI().toURL())
}

public fun importDataSchema(url: URL): ImportDataSchema = ImportDataSchema(url)

public fun importDataSchema(path: String): ImportDataSchema = ImportDataSchema(path)

public fun importDataSchema(path: Path): ImportDataSchema = ImportDataSchema(path)

public fun importDataSchema(file: File): ImportDataSchema = ImportDataSchema(file)
