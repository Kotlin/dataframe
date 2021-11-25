package samples.ml

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.initializer.HeNormal
import org.jetbrains.kotlinx.dl.api.core.initializer.Zeros
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.dataset.OnHeapDataset
import java.util.*
import kotlin.math.roundToInt

private const val SEED = 12L
private const val TEST_BATCH_SIZE = 100
private const val EPOCHS = 50
private const val TRAINING_BATCH_SIZE = 50

private val model = Sequential.of(
    Input(9),
    Dense(50, Activations.Relu, kernelInitializer = HeNormal(SEED), biasInitializer = Zeros()),
    Dense(50, Activations.Relu, kernelInitializer = HeNormal(SEED), biasInitializer = Zeros()),
    Dense(2, Activations.Linear, kernelInitializer = HeNormal(SEED), biasInitializer = Zeros())
)

fun main() {
    Locale.setDefault(Locale.FRANCE)
    val df =
        DataFrame.readCSV(fileOrUrl = "examples/idea-examples/titanic/src/main/resources/titanic.csv", delimiter = ';')

    // Calculating imputing values

    val sibspAvg = df.mean("sibsp").roundToInt()
    val parchAvg = df.mean("parch").roundToInt()
    val ageAvg = df.mean("age")
    val fareAvg = df.mean("fare")


    val (train, test) = df
        .rename("\uFEFFpclass").into("pclass")
        // imputing
        .fillNulls("sibsp").with { sibspAvg }
        .fillNulls("parch").with { parchAvg }
        .fillNulls("age").with { ageAvg }
        .fillNulls("fare").with { fareAvg }
        .fillNulls("sex").with { "female" }
        // one hot encoding
        .oneHotEncoding("pclass", "sex")
        // feature extraction
        .select("survived", "pclass_1", "pclass_2", "pclass_3", "sibsp", "parch", "age", "fare", "sex_1", "sex_2")
        .convert("survived", "pclass_1", "pclass_2", "pclass_3", "sibsp", "parch", "age", "fare", "sex_1", "sex_2")
        .toFloat()
        .shuffle()
        .toOnHeapDataset(labelColumnName = "survived")
        .split(0.7)

    model.use {
        it.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )

        it.summary()
        it.fit(dataset = train, epochs = EPOCHS, batchSize = TRAINING_BATCH_SIZE)

        val accuracy = model.evaluate(dataset = test, batchSize = TEST_BATCH_SIZE).metrics[Metrics.ACCURACY]

        println("Accuracy: $accuracy")
    }
}

private fun <T> DataFrame<T>.oneHotEncoding(
    vararg columnNames: String,
    removeSourceColumn: Boolean = true
): DataFrame<T> {
    val result = columnNames.fold(this) { acc, name ->
        acc[name].distinct().values().foldIndexed(acc) { index, subAcc, value ->
            println("$value to index: $index")
            subAcc.add("${name}_${index + 1}") { if (it[name] == value) 1 else 0 }
        }
    }

    return if (removeSourceColumn) result.remove(*columnNames) else result
}

private fun <T> DataFrame<T>.toOnHeapDataset(labelColumnName: String): OnHeapDataset {
    return OnHeapDataset.create(
        dataframe = this,
        yColumn = labelColumnName
    )
}

private fun OnHeapDataset.Companion.create(dataframe: DataFrame<Any?>, yColumn: String): OnHeapDataset {
    fun extractX(): Array<FloatArray> =
        dataframe.remove(yColumn)
            .merge { colsOf<Number>() }.by { it.map { it.toFloat() }.toFloatArray() }.into("X")
            .get { "X"<FloatArray>() }.toList().toTypedArray()

    fun extractY(): FloatArray =
        dataframe.get { yColumn<Float>() }.toList().toFloatArray()

    return create(
        ::extractX,
        ::extractY
    )
}
