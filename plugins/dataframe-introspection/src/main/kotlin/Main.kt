import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.jetbrains.kotlinx.dataframe.io.writeJson
import java.io.File

class MyComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
//        val functions = mutableListOf<FirFunction>()
//        FirExtensionRegistrar.registerExtension(project, MyFirExtensionRegistrar(functions))
//        IrGenerationExtension.registerExtension(project, FunctionSaver(functions))
//        IrGenerationExtension.registerExtension(project, FunctionVisitor())
    }
}

class FunctionVisitor : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val functions = mutableListOf<IrFunction>()
        val classes = mutableListOf<IrClass>()

        val visitor = object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                element.acceptChildrenVoid(this)
            }

            override fun visitFunction(declaration: IrFunction) {
                functions.add(declaration)
            }

            override fun visitClass(declaration: IrClass) {
                classes += declaration
            }
        }
        moduleFragment.files.forEach {
            visitor.visitFile(it)
        }
        val root: File = TODO("setup env variable to point output dir for the plugin")
        functions
            .toDataFrame {
                properties(maxDepth = 2) {
                    preserve(Name::class)
                    preserve(IrType::class)
                    exclude(IrFunction::returnType)
                }
                add("isTopLevel") { it.isTopLevel }
                add("returnType") {
                    (it.returnType as? IrSimpleType)
                }
            }
            .also { File(root, "IrType").writeText(it[IrFunction::returnType].map { it::class }.valueCounts().toCsv()) }
            .remove {
                dfs { it.name in setOf("descriptor", "startOffset", "endOffset", "factory") }
            }
            .update { dfsOf<AnyFrame>() }.with {
                it.remove { dfs { it.name in setOf("descriptor", "startOffset", "endOffset", "factory") } }
            }
            .replace { dfsOf<Name>() }.with { col ->
                col.nameColumnGroup()
            }
//            .replace { dfsOf<IrType?>() }.with { col ->
//                col.map {
////                    it?.classFqName?.asString()
//                    it?.render()
//                }
//            }
            .update { dfsOf<AnyFrame>() }.with {
                it.replace { dfsOf<IrType?>() }.with { col ->
                    col.map {
                        it?.render()
                    }
                }
            }
            .also { File(root, "functions_schema").writeText(it.schema().toString())  }
            .writeJson(File(root, "functions"))


//        classes
//            .toDataFrame {
//                properties(maxDepth = 2) {
//                    preserve(Name::class)
//                    include(IrClass::properties) { it?.toList() }
//                    include(IrClass::functions) { it?.toList() }
//                    include(IrClass::primaryConstructor)
//                }
//            }
//            .remove {
//                dfs { it.name in setOf("descriptor", "startOffset", "endOffset", "factory") }
//            }
//            .update { dfsOf<AnyFrame>() }.with {
//                it.remove { dfs { it.name in setOf("descriptor", "startOffset", "endOffset", "factory") } }
//            }
//            .replace { dfsOf<Name>() }.with {
//                it.nameColumnGroup()
//            }
//            .also { File(root, "classes_schema").writeText(it.schema().toString())  }
//            .writeJson(File(root, "classes"))
    }

    private fun DataColumn<Name>.nameColumnGroup(): ColumnGroup<Any?> {
        val col1 = map { it.identifierOrNullIfSpecial }.rename("identifier")
        val col2 = map { it.asString() }.rename("name")
        val col3 = map { it.isSpecial }.rename("isSpecial")
        return DataColumn.createColumnGroup(name(), dataFrameOf(col1, col2, col3))
    }
}


