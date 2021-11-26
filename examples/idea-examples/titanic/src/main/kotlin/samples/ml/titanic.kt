package samples.ml

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
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

    val (train, test) = df
        .rename("\uFEFFpclass").into("pclass")
        // imputing
        .fillNulls("sibsp", "parch", "age", "fare").cast<Number>().perCol { it.mean() }
        .fillNulls("sex").withValue("female")
        // one hot encoding
        .pivotMatches("pclass", "sex")
        // feature extraction
        .select("survived", "pclass", "sibsp", "parch", "age", "fare", "sex")
        .convert { dfsLeafs() }.toFloat()
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

private fun <T> DataFrame<T>.toOnHeapDataset(labelColumnName: String): OnHeapDataset {
    return OnHeapDataset.create(
        dataframe = this,
        yColumn = column(labelColumnName)
    )
}

private fun OnHeapDataset.Companion.create(dataframe: DataFrame<Any?>, yColumn: ColumnAccessor<Float>): OnHeapDataset {

    val x by column<FloatArray>("X")

    fun extractX(): Array<FloatArray> =
        dataframe.remove(yColumn)
            .merge { dfsOf<Float>() }.by { it.toFloatArray() }.into(x)[x].toTypedArray()

    fun extractY(): FloatArray = dataframe[yColumn].toFloatArray()

    return create(
        ::extractX,
        ::extractY
    )
}
