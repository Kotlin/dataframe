package org.jetbrains.kotlinx.dataframe.examples.spark.parquet

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.readParquet
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.jvm.java

/**
 * Demonstrates reading CSV with Apache Spark, writing Parquet, and reading Parquet with Kotlin DataFrame via Arrow.
 * Also trains a simple Spark ML regression model and exports a summary as Parquet, then reads it back with Kotlin DataFrame.
 */
fun main() {
    // Configure Hadoop environment for cross-platform compatibility
    setupHadoopEnvironment()


    // 1) Start local Spark
    val spark = SparkSession.builder()
        .appName("spark-parquet-dataframe")
        .master("local[*]")
        .config("spark.sql.warehouse.dir", Files.createTempDirectory("spark-warehouse").toString())
        // Completely bypass native Hadoop libraries and winutils
        .config("spark.hadoop.fs.defaultFS", "file:///")
        // Use RawLocalFileSystem to avoid winutils-dependent permission calls on Windows
        .config("spark.hadoop.fs.file.impl", "org.apache.hadoop.fs.RawLocalFileSystem")
        .config("spark.hadoop.fs.AbstractFileSystem.file.impl", "org.apache.hadoop.fs.local.LocalFs")
        .config("spark.hadoop.fs.file.impl.disable.cache", "true")
        .config("spark.sql.execution.arrow.pyspark.enabled", "false")
        // Disable Hadoop native library requirements and native warnings
        .config("spark.hadoop.hadoop.native.lib", "false")
        .config("spark.hadoop.io.native.lib.available", "false")
        // Set file permissions mode to bypass winutils permission setting
        .config("spark.hadoop.fs.permissions.umask-mode", "000")
        .getOrCreate()

    // Make Spark a bit quieter
    spark.sparkContext().setLogLevel("WARN")

    // 1) Read housing.csv (from repo path) with Spark
    val csvResource = object {}::class.java.getResource("/housing.csv")
        ?: throw IllegalStateException("housing.csv not found in classpath resources")
    val csvPath = Paths.get(csvResource.toURI()).toAbsolutePath().toString()

    val sdf = spark.read()
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(csvPath)

    // 2) Print the Spark DataFrame and export to Parquet in a temp directory
    println("Spark DataFrame (head):")
    sdf.show(10, false)

    val parquetDir: Path = Files.createTempDirectory("housing_spark_parquet_")
    val parquetPath = parquetDir.toString()
    sdf.write().mode("overwrite").parquet(parquetPath)
    println("Saved Spark Parquet to: $parquetPath")

    // 3) Read this Parquet with Kotlin DataFrame (Arrow backend)
    val kdf = DataFrame.readParquet(Paths.get(parquetPath))

    // 4) Print out head() for this Kotlin DataFrame
    println("Kotlin DataFrame (head):")
    kdf.head().print()

    // 5) Train a regression model with Spark MLlib
    // Use numeric features only, drop the categorical 'oceanProximity'
    val labelCol = "medianHouseValue"
    val candidateFeatureCols = listOf(
        "longitude", "latitude", "housingMedianAge", "totalRooms", "totalBedrooms",
        "population", "households", "medianIncome"
    )

    val colsArray = (candidateFeatureCols + labelCol).map { col(it) }.toTypedArray()
    val sdfNumeric = sdf.select(*colsArray)
        .na().drop()

    val assembler = VectorAssembler()
        .setInputCols(candidateFeatureCols.toTypedArray())
        .setOutputCol("features")

    val assembled = assembler.transform(sdfNumeric)
        .withColumnRenamed(labelCol, "label")

    val splits = assembled.randomSplit(doubleArrayOf(0.8, 0.2), 42)
    val train = splits[0]
    val test = splits[1]

    val lr = LinearRegression()
        .setFeaturesCol("features")
        .setLabelCol("label")
        .setMaxIter(50)

    val model = lr.fit(train)

    val summary = model.summary()
    println("Training RMSE: ${summary.rootMeanSquaredError()}")
    println("Training r2: ${summary.r2()}")

    // 6) Export model information to Parquet (coefficients per feature + intercept row)
    val coeffs = model.coefficients().toArray()
    val rows = candidateFeatureCols.mapIndexed { idx, name -> org.apache.spark.sql.RowFactory.create(name, coeffs[idx]) } +
        listOf(org.apache.spark.sql.RowFactory.create("intercept", model.intercept()))

    val schema = org.apache.spark.sql.types.StructType(
        arrayOf(
            org.apache.spark.sql.types.StructField("term", org.apache.spark.sql.types.DataTypes.StringType, false, org.apache.spark.sql.types.Metadata.empty()),
            org.apache.spark.sql.types.StructField("coefficient", org.apache.spark.sql.types.DataTypes.DoubleType, false, org.apache.spark.sql.types.Metadata.empty())
        )
    )

    val modelDf = spark.createDataFrame(rows, schema)
    val modelParquetDir = parquetDir.resolve("model").toString()
    modelDf.write().mode("overwrite").parquet(modelParquetDir)
    println("Saved model summary Parquet to: $modelParquetDir")

    // 7) Read this model Parquet with Kotlin DataFrame and print
    val modelKdf = DataFrame.readParquet(Paths.get(modelParquetDir))
    println("Model summary Kotlin DataFrame (head):")
    modelKdf.head().print()

    spark.stop()
}

/**
 * Configures Hadoop environment for cross-platform compatibility without requiring winutils.exe.
 * We force Hadoop to operate purely on the local filesystem and disable native libs.
 */
private fun setupHadoopEnvironment() {
    try {
        // Use a temporary dir as a dummy HADOOP_HOME to satisfy code paths that read it
        val hadoopDir = Files.createTempDirectory("hadoop_home").toFile()
        hadoopDir.deleteOnExit()

        // Create bin directory
        val binDir = File(hadoopDir, "bin")
        binDir.mkdirs()

        val hadoopHome = hadoopDir.absolutePath
        System.setProperty("hadoop.home.dir", hadoopHome)
        System.setProperty("HADOOP_HOME", hadoopHome)

        // Windows-specific: Create a working winutils.exe stub to satisfy permission commands
        if (System.getProperty("os.name").lowercase().contains("windows")) {
            val winutilsFile = File(binDir, "winutils.exe")
            createWindowsWinutilsStub(winutilsFile)
            println("Created Windows winutils stub at: ${winutilsFile.absolutePath}")
        }

        // Cross-platform settings to avoid native requirements and permission checks
        System.setProperty("hadoop.native.lib", "false")
        System.setProperty("java.library.path", "")
        System.setProperty("hadoop.security.authentication", "simple")
        System.setProperty("hadoop.security.authorization", "false")
        System.setProperty("hadoop.fs.permissions.umask-mode", "000")

        println("Hadoop environment configured (winutils-free): $hadoopHome")
    } catch (e: Exception) {
        println("Warning: Could not setup Hadoop environment: ${e.message}")
        val tempDir = System.getProperty("java.io.tmpdir")
        System.setProperty("hadoop.home.dir", tempDir)
        System.setProperty("HADOOP_HOME", tempDir)
        System.setProperty("hadoop.native.lib", "false")
    }
}







/**
 * Creates a minimal Windows executable that exits successfully.
 * This prevents the "not a valid Win32 application" error when Hadoop tries to invoke winutils.
 */
private fun createWindowsWinutilsStub(winutilsFile: File) {
    // Strategy:
    // 1) Try to compile a tiny valid Windows console .exe via PowerShell Add-Type that just exits(0).
    //    This avoids PE-format pitfalls and works on most modern Windows setups with PowerShell available.
    // 2) Verify by attempting to run the generated exe once. If start fails (e.g., error=216), fall back.
    // 3) Fallback: create an empty file (kept for compatibility with previous logic) and warn the user.
    //    With our Hadoop/Spark configs, this may still work in some environments, but it’s not guaranteed.
    try {
        val ps = System.getenv("WINDIR") != null // naive check for Windows environment
        if (ps) {
            val code = "using System; public static class WinUtilsStub { public static void Main(string[] args) { Environment.Exit(0); } }"
            // We escape quotes carefully for PowerShell -Command
            val cmd = arrayOf(
                "powershell",
                "-NoProfile",
                "-ExecutionPolicy",
                "Bypass",
                "-Command",
                "Add-Type -TypeDefinition \"$code\" -OutputAssembly \"${winutilsFile.absolutePath}\" -OutputType ConsoleApplication"
            )
            try {
                winutilsFile.parentFile?.mkdirs()
                val proc = ProcessBuilder(*cmd)
                    .redirectErrorStream(true)
                    .start()
                val out = proc.inputStream.bufferedReader().readText()
                val exit = proc.waitFor()
                if (exit == 0 && winutilsFile.exists()) {
                    // quick self-check: try launching it once with no args
                    try {
                        val test = ProcessBuilder(winutilsFile.absolutePath)
                            .redirectErrorStream(true)
                            .start()
                        test.waitFor()
                        println("Created Windows winutils stub via PowerShell at: ${winutilsFile.absolutePath}")
                        return
                    } catch (pe: Exception) {
                        println("Warning: Launching generated winutils failed (${pe.message}); will use fallback stub.")
                    }
                } else {
                    println("Warning: PowerShell compilation of winutils.exe failed with exit $exit. Output: $out")
                }
            } catch (pe: Exception) {
                println("PowerShell-based winutils build not available: ${pe.message}")
            }
        }
        // Fallback: maintain previous minimal placeholder (non-functional), at least to satisfy existence checks.
        if (!winutilsFile.exists()) {
            winutilsFile.parentFile?.mkdirs()
            winutilsFile.createNewFile()
        }
        winutilsFile.setExecutable(true)
        println("Created placeholder winutils at: ${winutilsFile.absolutePath} (may not be runnable)")
    } catch (e: Exception) {
        println("Could not create winutils stub, using fallback: ${e.message}")
        if (!winutilsFile.exists()) {
            winutilsFile.parentFile?.mkdirs()
            winutilsFile.createNewFile()
        }
        winutilsFile.setExecutable(true)
    }
}
