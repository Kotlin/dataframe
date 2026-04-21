package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SetDeclarationsParentVisitor
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File

public data class ContainingDeclarations(val clazz: IrClass?, val function: IrFunction?, val statementIndex: Int = 0)

@OptIn(UnsafeDuringIrConstructionAPI::class)
public class ExplainerIrTransformer(public val pluginContext: IrPluginContext) :
    IrTransformer<ContainingDeclarations>(),
    FileLoweringPass {
    public lateinit var file: IrFile
    public lateinit var source: String

    internal val declarationFinder = pluginContext.finderForBuiltins()

    public val explainerPackage: FqName = FqName("org.jetbrains.kotlinx.dataframe.explainer")

    internal val doAction by lazy {
        val callableId = CallableId(
            explainerPackage,
            FqName("PluginCallbackProxy"),
            Name.identifier("doAction"),
        )
        declarationFinder.findFunctions(callableId).single()
    }

    internal val alsoReference by lazy {
        declarationFinder
            .findFunctions(
                CallableId(FqName("kotlin"), Name.identifier("also")),
            ).single()
    }

    internal val plugin by lazy {
        val clazz = ClassId(explainerPackage, Name.identifier("PluginCallbackProxy"))
        declarationFinder.findClass(clazz)!!
    }

    override fun lower(irFile: IrFile) {
        var file: File
        file = File("testData/box/${irFile.path}")
        if (!file.exists()) {
            file = File(irFile.path)
        }
        this.file = irFile
        source = file.readText()
        irFile.transformChildren(this, ContainingDeclarations(null, null))
        irFile.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitFunction(declaration: IrFunction): IrStatement {
                declaration.acceptChildren(SetDeclarationsParentVisitor, declaration)
                return super.visitFunction(declaration)
            }
        })
    }

    override fun visitBlockBody(body: IrBlockBody, data: ContainingDeclarations): IrBody {
        for (i in body.statements.indices) {
            body.statements[i] = body.statements[i].transformStatement(this, data.copy(statementIndex = i))
        }
        return body
    }

    override fun visitClass(declaration: IrClass, data: ContainingDeclarations): IrStatement =
        super.visitClass(declaration, data.copy(clazz = declaration))

    override fun visitFunction(declaration: IrFunction, data: ContainingDeclarations): IrStatement {
        val annotated = declaration.annotations.any {
            it.type.classFqName
                ?.shortName()
                ?.identifierOrNullIfSpecial
                ?.equals("TransformDataFrameExpressions") == true
        }
        return if (annotated) {
            super.visitFunction(declaration, data.copy(function = declaration))
        } else {
            declaration
        }
    }

    override fun visitElement(element: IrElement, data: ContainingDeclarations): IrElement {
        element.transformChildren(this, data)
        return element
    }

    override fun visitField(declaration: IrField, data: ContainingDeclarations): IrStatement {
        if (declaration.isLocal) {
            declaration.transformChildren(this, data)
        }
        return declaration
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: ContainingDeclarations): IrBody = body

    override fun visitGetValue(expression: IrGetValue, data: ContainingDeclarations): IrExpression {
        if (expression.startOffset < 0) return expression
        if (expression.type.classFqName in dataFrameLike) {
            val builder = pluginContext.irBuiltIns.createIrBuilder(expression.symbol)
            return builder.transformDataFrameExpression(expression, expression.symbol.owner.name, receiver = null, data)
        }
        return super.visitExpression(expression, data)
    }

    // also, what if expression type is not DataFrame, but Unit? and receiver expression is DataFrame at some point
    override fun visitCall(expression: IrCall, data: ContainingDeclarations): IrExpression {
        if (expression.startOffset < 0) return expression
        if (expression.type.classFqName in dataFrameLike) {
            if (expression.symbol.owner.name == Name.identifier("component1")) return expression
            val extensionReceiverIndex =
                expression.symbol.owner.parameters.indexOfFirst { it.kind == IrParameterKind.ExtensionReceiver }
            var receiver: IrExpression?
            // expression.arguments[extensionReceiverIndex] = extension callables,
            // expression.dispatchReceiver = member callables such as "GroupBy.aggregate"
            if (extensionReceiverIndex >= 0) {
                receiver = expression.arguments[extensionReceiverIndex]!!
                val transformedExtensionReceiver = receiver.transform(this, data)
                expression.arguments[extensionReceiverIndex] = transformedExtensionReceiver
            } else {
                receiver = expression.dispatchReceiver
                val transformedExtensionReceiver = expression.dispatchReceiver?.transform(this, data)
                expression.dispatchReceiver = transformedExtensionReceiver
            }
            val builder = pluginContext.irBuiltIns.createIrBuilder(expression.symbol)
            return builder.transformDataFrameExpression(
                expression = expression,
                ownerName = expression.symbol.owner.name,
                receiver = receiver,
                data = data,
            )
        }
        return super.visitExpression(expression, data)
    }

    private fun IrBuilderWithScope.transformDataFrameExpression(
        expression: IrDeclarationReference,
        ownerName: Name,
        receiver: IrExpression?,
        data: ContainingDeclarations,
    ): IrCall {
        val result = irCall(alsoReference).also {
            it.typeArguments[0] = expression.type
            it.arguments[0] = expression

            val alsoLambda = pluginContext.irFactory.buildFun {
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                name = Name.special("<anonymous>")
                visibility = DescriptorVisibilities.LOCAL
                returnType = pluginContext.irBuiltIns.unitType
            }.apply {
                val itParam = addValueParameter("it", expression.type)
                val source = try {
                    source.substring(expression.startOffset, expression.endOffset)
                } catch (e: Exception) {
                    throw Exception("$expression ${ownerName.asString()} $source", e)
                }
                val expressionId = expressionId(expression)
                val receiverId = receiver?.let { expressionId(it) }
                val valueArguments = buildList<IrExpression?> {
                    add(source.irConstImpl()) // source: String
                    add(ownerName.asStringStripSpecialMarkers().irConstImpl()) // name: String
                    add(irGet(itParam)) // df: Any
                    add(expressionId.irConstImpl()) // id: String
                    add(receiverId.irConstImpl()) // receiverId: String?
                    add(data.clazz?.fqNameWhenAvailable?.asString().irConstImpl()) // containingClassFqName: String?
                    add(data.function?.name?.asString().irConstImpl()) // containingFunName: String?
                    add(irInt(data.statementIndex)) // statementIndex: Int
                }
                body = irBlockBody {
                    +irCall(doAction).apply {
                        dispatchReceiver = irGetObject(plugin)
                        val firstValueArgumentIndex = 1 // skipping dispatch receiver
                        valueArguments.forEachIndexed { i, argument ->
                            this.arguments[firstValueArgumentIndex + i] = argument
                        }
                    }
                }
            }
            val alsoLambdaExpression = IrFunctionExpressionImpl(
                startOffset = -1,
                endOffset = -1,
                type = pluginContext.irBuiltIns
                    .functionN(2)
                    .typeWith(listOf(expression.type, pluginContext.irBuiltIns.unitType)),
                function = alsoLambda,
                origin = IrStatementOrigin.LAMBDA,
            )

            it.arguments[1] = alsoLambdaExpression
        }
        return result
    }

    private fun String?.irConstImpl(): IrConstImpl {
        val nullableString = pluginContext.irBuiltIns.stringType.makeNullable()
        val argument = if (this == null) {
            IrConstImpl.constNull(-1, -1, nullableString)
        } else {
            IrConstImpl.string(-1, -1, nullableString, this)
        }
        return argument
    }

    private fun expressionId(expression: IrExpression): String {
        val line = file.fileEntry.getLineNumber(expression.startOffset)
        val column = file.fileEntry.getColumnNumber(expression.startOffset)
        return "${file.path}:${line + 1}:${column + 1}"
    }
}

internal fun <D> IrStatement.transformStatement(transformer: IrTransformer<D>, data: D): IrStatement =
    transform(transformer, data) as IrStatement

internal val dataFrameLike: Set<FqName> = setOf(
    FqName("org.jetbrains.kotlinx.dataframe.api.Pivot"),
    FqName("org.jetbrains.kotlinx.dataframe.api.ReducedPivot"),
    FqName("org.jetbrains.kotlinx.dataframe.api.PivotGroupBy"),
    FqName("org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy"),
    FqName("org.jetbrains.kotlinx.dataframe.api.SplitWithTransform"),
    FqName("org.jetbrains.kotlinx.dataframe.api.Merge"),
    FqName("org.jetbrains.kotlinx.dataframe.api.Split"),
    FqName("org.jetbrains.kotlinx.dataframe.api.Gather"),
    FqName("org.jetbrains.kotlinx.dataframe.api.Update"),
    FqName("org.jetbrains.kotlinx.dataframe.api.Convert"),
    FqName("org.jetbrains.kotlinx.dataframe.api.FormattedFrame"),
    FqName("org.jetbrains.kotlinx.dataframe.api.GroupBy"),
    FqName("org.jetbrains.kotlinx.dataframe.DataFrame"),
    FqName("org.jetbrains.kotlinx.dataframe.DataRow"),
)
