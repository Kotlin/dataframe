import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.typeNameOf
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame

fun FunSpec.Builder.addCommonFunctionAttributes(): FunSpec.Builder {
    returns(typeNameOf<AnyFrame>().copy(annotations = emptyList()))
    addModifiers(KModifier.PUBLIC)
    receiver(typeNameOf<DataFrame.Companion>())
    return this
}
