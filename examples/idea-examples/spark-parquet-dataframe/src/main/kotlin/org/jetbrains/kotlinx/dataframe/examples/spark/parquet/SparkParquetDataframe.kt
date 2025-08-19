package org.jetbrains.kotlinx.dataframe.examples.spark.parquet

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readParquet
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.layers.abLine
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.util.color.Color
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.jvm.java

/**
 * Demonstrates reading CSV with Apache Spark, writing Parquet, and reading Parquet with Kotlin DataFrame via Arrow.
 * Also trains a simple Spark ML regression model and exports a summary as Parquet, then reads it back with Kotlin DataFrame.
 */
fun main() {
    // 1) Start local Spark
    val spark = SparkSession.builder()
        .appName("spark-parquet-dataframe")
        .master("local[*]")
        .config("spark.sql.warehouse.dir", Files.createTempDirectory("spark-warehouse").toString())
        // Completely bypass native Hadoop libraries and winutils
        .config("spark.hadoop.fs.defaultFS", "file:///")
        .config("spark.hadoop.fs.AbstractFileSystem.file.impl", "org.apache.hadoop.fs.local.LocalFs")
        .config("spark.hadoop.fs.file.impl.disable.cache", "true")
        // Disable Hadoop native library requirements and native warnings
        .config("spark.hadoop.hadoop.native.lib", "false")
        .config("spark.hadoop.io.native.lib.available", "false")
        .config(
            "spark.driver.extraJavaOptions",
            "--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED"
        )
        .config(
            "spark.executor.extraJavaOptions",
            "--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED"
        )
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
    // Pass actual part-*.parquet files instead of the directory
    val parquetFiles = listParquetFilesIfAny(parquetDir)
    val kdf = DataFrame.readParquet(*parquetFiles)

    // 4) Print out head() for this Kotlin DataFrame
    println("Kotlin DataFrame (head):")
    kdf.head().print()

    // 5) Train a regression model with Spark MLlib
    // Use numeric features only, drop the categorical 'ocean_proximity'
    val labelCol = "median_house_value"
    val candidateFeatureCols = listOf(
        "longitude", "latitude", "housing_median_age", "total_rooms", "total_bedrooms",
        "population", "households", "median_income"
    )

    val colsArray = (candidateFeatureCols + labelCol).map { col(it) }.toTypedArray()
    val sdfNumeric = sdf.select(*colsArray)
        .na().drop()

    val assembler = VectorAssembler()
        .setInputCols(candidateFeatureCols.toTypedArray())
        .setOutputCol("features")

    // Build Pipeline (VectorAssembler -> LinearRegression) and train/test split WITHOUT prebuilt 'features'
    val lr = LinearRegression()
        .setFeaturesCol("features")
        .setLabelCol(labelCol)
        .setFitIntercept(false)
        .setElasticNetParam(0.5)
        .setMaxIter(10)

    val fullPipeline = org.apache.spark.ml.Pipeline().setStages(arrayOf(assembler, lr))

    val splits = sdfNumeric.randomSplit(doubleArrayOf(0.8, 0.2), 42)
    val train = splits[0]
    val test = splits[1]

    val fullPipelineModel = fullPipeline.fit(train)
    val lrModel = fullPipelineModel.stages()[1] as org.apache.spark.ml.regression.LinearRegressionModel

    val summary = lrModel.summary()
    println("Training RMSE: ${summary.rootMeanSquaredError()}")
    println("Training r2: ${summary.r2()}")

    // 6) Export model information to Parquet (coefficients per feature + intercept row)
    val coeffs = lrModel.coefficients().toArray()
    val rows = candidateFeatureCols.mapIndexed { idx, name -> org.apache.spark.sql.RowFactory.create(name, coeffs[idx]) } +
        listOf(org.apache.spark.sql.RowFactory.create("intercept", lrModel.intercept()))

    val schema = org.apache.spark.sql.types.StructType(
        arrayOf(
            org.apache.spark.sql.types.StructField("term", org.apache.spark.sql.types.DataTypes.StringType, false, org.apache.spark.sql.types.Metadata.empty()),
            org.apache.spark.sql.types.StructField("coefficient", org.apache.spark.sql.types.DataTypes.DoubleType, false, org.apache.spark.sql.types.Metadata.empty())
        )
    )

    val modelDf = spark.createDataFrame(rows, schema)
    val modelParquetDir = parquetDir.resolve("model")
    modelDf.write().mode("overwrite").parquet(modelParquetDir.toString())
    println("Saved model summary Parquet to: $modelParquetDir")

    // 7) Read this model Parquet with Kotlin DataFrame and print
    val modelParquetFiles = listParquetFilesIfAny(modelParquetDir)
    val modelKdf = DataFrame.readParquet(*modelParquetFiles)

    println("Model summary Kotlin DataFrame (head):")
    modelKdf.head().print()

    // 8) Save the entire PipelineModel using the standard Spark ML mechanism
    //    The model is already fitted above; just save it.
    val pipelinePath = parquetDir.resolve("pipeline_model_spark").toString()
    fullPipelineModel.write().overwrite().save(pipelinePath)
    println("Step 8: Saved PipelineModel to: $pipelinePath")

    // 9) Inspect pipeline internals using Kotlin DataFrame from concrete paths (no directory walking)
    // IMPORTANT (why this is not the most convenient way for export/import):
    // - The ML writer saves a directory with mixed JSON (metadata) and Parquet (model data).
    // - Internal folder names for stages include indexes and algorithm/uids (e.g., "0_VectorAssembler_xxx", "1_LinearRegressionModel_xxx"),
    //   which are not guaranteed to be stable across Spark versions.
    // - Reading internals are suitable only for inspection/exploration. For reuse, prefer PipelineModel.load();
    //   for portable/tabular exchange, write an explicit summary DataFrame.
    //
    // Concrete layout this demo relies on:
    //   $pipelinePath/metadata/
    //   $pipelinePath/stages/0_*/metadata/, $pipelinePath/stages/0_*/data/
    //   $pipelinePath/stages/1_*/metadata/, $pipelinePath/stages/1_*/data/

    val pipelineRoot = Paths.get(pipelinePath)
    val stagesDir = pipelineRoot.resolve("stages")
    val stage0Dir = findStageDir(stagesDir, "0_")
    val stage1Dir = findStageDir(stagesDir, "1_")

    // Accumulate Kotlin DataFrames found in step 9 so we can optionally join only existing ones in step 10
    val metaKdfs = mutableListOf<DataFrame<*>>()
    val stageDataKdfs = mutableListOf<DataFrame<*>>()

    // 9.1) Root metadata (JSON) -> read each file one-by-one
    val rootMetaDir = pipelineRoot.resolve("metadata")
    val rootMetaFiles = listTextOrJsonFiles(rootMetaDir)
    for (file in rootMetaFiles) {
        val df = DataFrame.readJson(file.toFile())
        println("Step 9: Pipeline root metadata JSON (${file.fileName}) head:")
        df.head().print()
        metaKdfs += df
    }

    // 9.2) Stage 0 (VectorAssembler) metadata/data
    val stage0MetaDir = stage0Dir.resolve("metadata")
    for (file in listTextOrJsonFiles(stage0MetaDir)) {
        val df = DataFrame.readJson(file.toFile())
        println("Step 9: Stage 0 metadata (${file.fileName}) head:")
        df.head().print()
        metaKdfs += df
    }
    val stage0DataDir = stage0Dir.resolve("data")
    val stage0ParquetFiles = listParquetFilesIfAny(stage0DataDir)
    if (stage0ParquetFiles.isNotEmpty()) {
        val stage0Kdf = DataFrame.readParquet(*stage0ParquetFiles)
        println("Step 9: Stage 0 data (Parquet) head:")
        stage0Kdf.head().print()
        stageDataKdfs += stage0Kdf
    } else {
        println("Step 9: Stage 0 data directory is missing or has no .parquet files, skipping.")
    }

    // 9.3) Stage 1 (LinearRegressionModel) metadata/data
    val stage1MetaDir = stage1Dir.resolve("metadata")
    for (file in listTextOrJsonFiles(stage1MetaDir)) {
        val df = DataFrame.readJson(file.toFile())
        println("Step 9: Stage 1 metadata (${file.fileName}) head:")
        df.head().print()
        metaKdfs += df
    }
    val stage1DataDir = stage1Dir.resolve("data")
    val stage1ParquetFiles = listParquetFilesIfAny(stage1DataDir)
    if (stage1ParquetFiles.isNotEmpty()) {
        val stage1Kdf = DataFrame.readParquet(*stage1ParquetFiles)
        println("Step 9: Stage 1 data (Parquet) head:")
        stage1Kdf.head().print()
        stageDataKdfs += stage1Kdf
    } else {
        println("Step 9: Stage 1 data directory is missing or has no .parquet files, skipping.")
    }

    // 10) Join only existing Kotlin DataFrames and build a plot from the linear model
    // 10.1) Unified metadata from any JSON files we successfully parsed above
    val unifiedMeta = if (metaKdfs.isNotEmpty()) metaKdfs.concat() else null
    if (unifiedMeta != null) {
        println("Step 10: Unified metadata head:")
        unifiedMeta.head().print()
    } else {
        println("Step 10: No metadata DataFrames were found to unify.")
    }

    // 10.2) Unified model data: in this demo we already have a single modelKdf (coefficients + intercept)
    val unifiedModelDf = modelKdf
    println("Step 10: Unified model data (coefficients) head:")
    unifiedModelDf.head().print()

    // 10.3) Build a linear plot: dataset points and model line y = a*x + b for the chosen feature
    // Choose feature 'median_income' vs. label 'median_house_value'
    val pointsDf = kdf.dropNA("median_income", "median_house_value")

    // Extract slope (coefficient for 'median_income') and intercept from modelKdf
    val terms = unifiedModelDf.getColumn("term").cast<String>().toList()
    val coefs = unifiedModelDf.getColumn("coefficient").cast<Double>().toList()
    val slopeIdx = terms.indexOf("median_income")
    val interceptIdx = terms.indexOf("intercept")
    val slopeValue = if (slopeIdx >= 0) coefs[slopeIdx] else 0.0
    val interceptValue = if (interceptIdx >= 0) coefs[interceptIdx] else 0.0

    println("slope: $slopeValue intercept: $interceptValue")

    // Prepare DF for plotting: add constant columns for abLine mapping
    val dfForPlot = pointsDf
        .add("slope_const") { slopeValue }
        .add("intercept_const") { interceptValue }

    // 10.4) Create Kandy plot using abLine (slope/intercept) and export to a .jpg file
    val plot = dfForPlot.plot {
        points {
            x("median_income")
            y("median_house_value")
            // Visual hint: small circles
            color = Color.LIGHT_BLUE
            size = 2.0
        }
        abLine {
            // Use linear model parameters: y = slope * x + intercept
            slope.constant(slopeValue)
            intercept.constant(interceptValue)
            color = Color.RED
            width = 2.0
        }
    }

    val targetDir = Paths.get("").normalize()
    Files.createDirectories(targetDir)
    val plotPath = targetDir.resolve("linear_model_plot.jpg").toString()

    plot.save(plotPath)
    println("Step 10: Saved plot to: $plotPath")

    spark.stop()

}

/**
 * Returns .parquet files if the directory exists and contains any; otherwise returns an empty array.
 * Safe to use for Spark ML stage "data" subfolders that may be absent.
 */
private fun listParquetFilesIfAny(dir: Path): Array<Path> {
    if (!Files.exists(dir) || !Files.isDirectory(dir)) return emptyArray()
    val files: List<Path> = Files.list(dir).use { stream ->
        stream
            .filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".parquet", ignoreCase = true) }
            .collect(Collectors.toList())
    }
    return files.toTypedArray()
}

/**
 * Finds a stage directory inside 'stagesDir' by prefix (e.g., "0_", "1_").
 * No extra checks: assumes such a directory exists.
 */
private fun findStageDir(stagesDir: Path, prefix: String): Path {
    return Files.list(stagesDir).use { s ->
        s.filter { Files.isDirectory(it) && it.fileName.toString().startsWith(prefix) }
            .findFirst().get()
    }
}

private fun listTextOrJsonFiles(dir: Path): List<Path> {
    return Files.list(dir).use { s ->
        s.filter {
            Files.isRegularFile(it) &&
                (it.fileName.toString().endsWith(".json", ignoreCase = true) ||
                    it.fileName.toString().endsWith(".txt", ignoreCase = true))
        }.collect(Collectors.toList())
    }
}
