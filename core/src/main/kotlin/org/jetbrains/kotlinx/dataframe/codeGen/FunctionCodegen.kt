package org.jetbrains.kotlinx.dataframe.codeGen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.typeNameOf
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.joinToCamelCaseString
import org.jetbrains.kotlinx.dataframe.impl.schema.intersectSchemas
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

public inline fun <reified T> generateCode(noinline f: () -> DataFrame<T>, sourceSet: File, name: String) {
    return generateCode(f, name, T::class, sourceSet)
}

public inline fun <reified I1, reified T> generateCode(
    noinline f: (I1) -> DataFrame<T>, sourceSet: File, name: String, arguments: List<I1> = emptyList()
) {
    return generateCode(f, name, arguments, typeNameOf<I1>(), T::class, sourceSet)
}

//public fun <I1, I2, T> generateCode(f: Function2<I1, I2, DataFrame<T>>, arguments: List<Pair<I1, I2>>) {
//
//}

@PublishedApi
internal fun <I1, T> generateCode(
    f: (I1) -> DataFrame<T>,
    name: String,
    arguments: List<I1> = emptyList(),
    inputName: TypeName,
    marker: KClass<*>,
    sourceSet: File
) {
    val schema = if (arguments.isNotEmpty()) {
        arguments.map { f(it).schema() }.intersectSchemas()
    } else {
        MarkersExtractor.get(marker).schema
    }
    val packageName = (f as KFunction<*>).javaMethod?.declaringClass?.packageName!!
    val functionName = (f as KFunction<*>).javaMethod?.name!!
    val method = object : DefaultReadDfMethod {
        override val additionalImports: List<String> = emptyList()

        override fun toDeclaration(marker: Marker, visibility: String): String {
            val cl = (f as KFunction<*>).javaMethod?.declaringClass?.name!!
            val format =
                """
                        val f = this::class.java.classLoader.loadClass("$cl").getDeclaredMethod("$functionName", ${inputName}::class.java)
                        f.isAccessible = true
                        return (f.invoke(null, v) as DataFrame<*>).cast()
                        """.trimIndent()

            val typeSpec = TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder("from")
                        .addParameter(
                            ParameterSpec.builder("v", inputName).build()
                        )
                        .returns(DataFrame::class.asTypeName().parameterizedBy(ClassName(packageName, marker.shortName)))
                        .addCode(format, "v")
                        .build()
                ).build()
            return typeSpec.toString()
        }
    }

    val generator = CodeGenerator.create(useFqNames = true).generate(schema, name, true, false, true, visibility = MarkerVisibility.EXPLICIT_PUBLIC,readDfMethod = method)

    println(packageName)
    println(generator.code.toStandaloneSnippet(packageName, emptyList()))
    val destination = sourceSet
    val targetPackage = File(destination, packageName.replace(".", "/"))
    val file = File(targetPackage, "${name.split(DELIMITERS_REGEX).joinToString("")}.Generated.kt")
    file.writeText(generator.code.toStandaloneSnippet(packageName, emptyList()))
}

@PublishedApi
internal fun <T> generateCode(f: () -> DataFrame<T>, name: String, marker: KClass<*>, sourceSet: File) {
    // refine nullability from compile time schema
//    val schema = MarkersExtractor.get(marker).schema
    val schema = f().schema()
    val packageName = (f as KFunction<*>).javaMethod?.declaringClass?.packageName!!
    val functionName = (f as KFunction<*>).javaMethod?.name!!
    val method = object : DefaultReadDfMethod {
        override val additionalImports: List<String> = emptyList()

        override fun toDeclaration(marker: Marker, visibility: String): String {
            val cl = (f as KFunction<*>).javaMethod?.declaringClass?.name!!
            val format =
                """
                        val f = this::class.java.classLoader.loadClass("$cl").getDeclaredMethod("$functionName")
                        f.isAccessible = true
                        return (f.invoke(null) as DataFrame<*>).cast()
                        """.trimIndent()

            val typeSpec = TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder("sample")
                        .returns(DataFrame::class.asTypeName().parameterizedBy(ClassName(packageName, marker.shortName)))
                        .addCode(format, "v")
                        .build()
                ).build()
            return typeSpec.toString()
        }
    }

    val generator = CodeGenerator.create(useFqNames = true).generate(schema, name, true, false, true, visibility = MarkerVisibility.EXPLICIT_PUBLIC,readDfMethod = method)

    println(packageName)
    println(generator.code.toStandaloneSnippet(packageName, emptyList()))
    val destination = sourceSet
    val targetPackage = File(destination, packageName.replace(".", "/"))
    val file = File(targetPackage, "${name.split(DELIMITERS_REGEX).joinToString("")}.Generated.kt")
    file.writeText(generator.code.toStandaloneSnippet(packageName, emptyList()))
}

private fun ff() = dataFrameOf("a")(123)
private fun ff1(s: String) = dataFrameOf("a")(123)

internal fun main() {
    generateCode(::ff, File("core/src/main/kotlin"), "F")
    generateCode(::ff1, File("main"), "F", listOf("a"))
}
