@file:Suppress("ktlint:standard:no-unused-imports")

package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrElementBase
import org.jetbrains.kotlin.ir.IrStatement
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
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
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

data class ContainingDeclarations(val clazz: IrClass?, val function: IrFunction?, val statementIndex: Int = 0)

@OptIn(UnsafeDuringIrConstructionAPI::class)
class ExplainerIrTransformer(val pluginContext: IrPluginContext) :
    IrTransformer<ContainingDeclarations>(),
    FileLoweringPass {
    lateinit var file: IrFile
    lateinit var source: String

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
        for (i in 0 until body.statements.size) {
            @Suppress("UNCHECKED_CAST")
            body.statements.set(
                index = i,
                element = (body.statements[i] as IrElementBase)
                    .transform(
                        transformer = this,
                        data = data.copy(statementIndex = i),
                    ) as IrStatement,
            )
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

    val dataFrameLike = setOf(
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

    val explainerPackage = FqName("org.jetbrains.kotlinx.dataframe.explainer")

    override fun visitGetValue(expression: IrGetValue, data: ContainingDeclarations): IrExpression {
        if (expression.startOffset < 0) return expression
        if (expression.type.classFqName in dataFrameLike) {
            return transformDataFrameExpression(expression, expression.symbol.owner.name, receiver = null, data)
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

            return transformDataFrameExpression(expression, expression.symbol.owner.name, receiver = receiver, data)
        }
        return super.visitExpression(expression, data)
    }

    private fun transformDataFrameExpression(
        expression: IrDeclarationReference,
        ownerName: Name,
        receiver: IrExpression?,
        data: ContainingDeclarations,
    ): IrCall {
        val alsoReference = pluginContext
            .referenceFunctions(
                CallableId(FqName("kotlin"), Name.identifier("also")),
            ).single()

        val result = IrCallImplWithShape(
            startOffset = -1,
            endOffset = -1,
            type = expression.type,
            symbol = alsoReference,
            typeArgumentsCount = 1,
            valueArgumentsCount = 1,
            contextParameterCount = 0,
            hasDispatchReceiver = true,
            hasExtensionReceiver = true,
        ).apply {
            val extensionReceiverIndex =
                this.symbol.owner.parameters.indexOfFirst { it.kind == IrParameterKind.ExtensionReceiver }
            if (extensionReceiverIndex >= 0) {
                this.arguments[extensionReceiverIndex] = expression
            } else {
                this.insertExtensionReceiver(expression)
            }

            typeArguments[0] = expression.type

            val symbol = IrSimpleFunctionSymbolImpl()
            val alsoLambda = pluginContext.irFactory
                .createSimpleFunction(
                    startOffset = -1,
                    endOffset = -1,
                    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
                    symbol = symbol,
                    name = Name.special("<anonymous>"),
                    visibility = DescriptorVisibilities.LOCAL,
                    modality = Modality.FINAL,
                    returnType = pluginContext.irBuiltIns.unitType,
                    isInline = false,
                    isExternal = false,
                    isTailrec = false,
                    isSuspend = false,
                    isOperator = false,
                    isInfix = false,
                    isExpect = false,
                ).apply {
                    // replace all regular value parameters with a single one `it`
                    parameters = parameters.filterNot { it.kind == IrParameterKind.Regular } +
                        pluginContext.irFactory.createValueParameter(
                            startOffset = -1,
                            endOffset = -1,
                            origin = IrDeclarationOrigin.DEFINED,
                            kind = IrParameterKind.Regular,
                            name = Name.identifier("it"),
                            type = expression.type,
                            isAssignable = false,
                            symbol = IrValueParameterSymbolImpl(),
                            varargElementType = null,
                            isCrossinline = false,
                            isNoinline = false,
                            isHidden = false,
                        )

                    val itSymbol = parameters.first { it.kind == IrParameterKind.Regular }.symbol
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
                        add(IrGetValueImpl(-1, -1, itSymbol)) // df: Any
                        add(expressionId.irConstImpl()) // id: String
                        add(receiverId.irConstImpl()) // receiverId: String?
                        add(data.clazz?.fqNameWhenAvailable?.asString().irConstImpl()) // containingClassFqName: String?
                        add(data.function?.name?.asString().irConstImpl()) // containingFunName: String?
                        add(
                            IrConstImpl.int(
                                -1,
                                -1,
                                pluginContext.irBuiltIns.intType,
                                data.statementIndex,
                            ),
                        ) // statementIndex: Int
                    }
                    body = pluginContext.irFactory.createBlockBody(-1, -1).apply {
                        val callableId = CallableId(
                            explainerPackage,
                            FqName("PluginCallbackProxy"),
                            Name.identifier("doAction"),
                        )
                        val doAction = pluginContext.referenceFunctions(callableId).single()
                        statements += IrCallImplWithShape(
                            startOffset = -1,
                            endOffset = -1,
                            type = doAction.owner.returnType,
                            symbol = doAction,
                            typeArgumentsCount = 0,
                            valueArgumentsCount = valueArguments.size,
                            contextParameterCount = 0,
                            hasDispatchReceiver = true,
                            hasExtensionReceiver = false,
                        ).apply {
                            val clazz = ClassId(explainerPackage, Name.identifier("PluginCallbackProxy"))
                            val plugin = pluginContext.referenceClass(clazz)!!
                            dispatchReceiver = IrGetObjectValueImpl(-1, -1, plugin.defaultType, plugin)

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

            val firstValueArgumentIndex = this.symbol.owner.parameters
                .indexOfFirst { it.kind == IrParameterKind.Regular }
                .takeUnless { it < 0 } ?: this.symbol.owner.parameters.size
            this.arguments[firstValueArgumentIndex] = alsoLambdaExpression
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
