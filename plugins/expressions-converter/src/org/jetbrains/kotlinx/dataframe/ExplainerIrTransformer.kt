package org.jetbrains.kotlinx.dataframe

import java.io.File
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
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
import org.jetbrains.kotlin.ir.declarations.impl.IrFunctionImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrValueParameterImpl
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SetDeclarationsParentVisitor
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

data class ContainingDeclarations(val clazz: IrClass?, val function: IrFunction?, val statementIndex: Int = 0)

class ExplainerIrTransformer(val pluginContext: IrPluginContext) : FileLoweringPass, IrElementTransformer<ContainingDeclarations> {
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
            (body.statements.set(i, (body.statements.get(i) as IrElementBase).transform(this, data.copy(statementIndex = i)) as IrStatement))
        }
        return body
    }

    override fun visitClass(declaration: IrClass, data: ContainingDeclarations): IrStatement {
        return super.visitClass(declaration, data.copy(clazz = declaration))
    }

    override fun visitFunction(declaration: IrFunction, data: ContainingDeclarations): IrStatement {
        val annotated = declaration.annotations.any {
            it.type.classFqName?.shortName()?.identifierOrNullIfSpecial?.equals("TransformDataFrameExpressions") == true
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

    override fun visitExpressionBody(body: IrExpressionBody, data: ContainingDeclarations): IrBody {
        return body
    }

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
        FqName("org.jetbrains.kotlinx.dataframe.DataRow")
    )

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
            var receiver = expression.extensionReceiver
            // expression.extensionReceiver = extension callables,
            // expression.dispatchReceiver = member callables such as "GroupBy.aggregate"
            if (receiver != null) {
                val transformedExtensionReceiver = expression.extensionReceiver?.transform(this, data)
                expression.extensionReceiver = transformedExtensionReceiver
            } else {
                receiver = expression.dispatchReceiver
                val transformedExtensionReceiver = expression.dispatchReceiver?.transform(this, data)
                expression.dispatchReceiver = transformedExtensionReceiver
            }

            return transformDataFrameExpression(expression, expression.symbol.owner.name, receiver = receiver, data)
        }
        return super.visitExpression(expression, data)
    }

    @OptIn(FirIncompatiblePluginAPI::class)
    private fun transformDataFrameExpression(
        expression: IrDeclarationReference,
        ownerName: Name,
        receiver: IrExpression?,
        data: ContainingDeclarations
    ): IrCall {
        val alsoReference = pluginContext.referenceFunctions(FqName("kotlin.also")).single()

        val result = IrCallImpl(-1, -1, expression.type, alsoReference, 1, 1).apply {
            this.extensionReceiver = expression
            putTypeArgument(0, expression.type)

            val symbol = IrSimpleFunctionSymbolImpl()
            val alsoLambda = IrFunctionImpl(
                -1,
                -1,
                IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
                symbol,
                Name.special("<anonymous>"),
                DescriptorVisibilities.LOCAL,
                Modality.FINAL,
                pluginContext.irBuiltIns.unitType,
                isInline = false,
                isExternal = false,
                isTailrec = false,
                isSuspend = false,
                isOperator = false,
                isInfix = false,
                isExpect = false
            ).apply {
                valueParameters = buildList {
                    add(IrValueParameterImpl(
                        -1,
                        -1,
                        IrDeclarationOrigin.DEFINED,
                        IrValueParameterSymbolImpl(),
                        Name.identifier("it"),
                        0,
                        expression.type,
                        null,
                        isCrossinline = false,
                        isNoinline = false,
                        isHidden = false,
                        isAssignable = false
                    ))
                }
                val it = expression
                val itSymbol = valueParameters[0].symbol
                body = pluginContext.irFactory.createBlockBody(-1, -1) {
                    val doAction = pluginContext.referenceFunctions(FqName("org.jetbrains.kotlinx.dataframe.explainer.PluginCallback.doAction")).single()
                    statements += IrCallImpl(-1, -1, doAction.owner.returnType, doAction, 0, 8).apply {
                        val pluginAction =
                            pluginContext.referenceClass(FqName("org.jetbrains.kotlinx.dataframe.explainer.PluginCallback"))!!
                        dispatchReceiver = IrGetObjectValueImpl(-1, -1, pluginAction.defaultType, pluginAction)
                        val source = try {
                            source.substring(it.startOffset, it.endOffset)
                        } catch (e: Exception) {
                            throw Exception("$it ${ownerName.asString()} $source", e)
                        }
                        val expressionId = expressionId(expression)
                        val receiverId = receiver?.let { expressionId(it) }
                        putValueArgument(0, IrConstImpl.string(-1, -1, pluginContext.irBuiltIns.stringType, source))
                        putValueArgument(1, IrConstImpl.string(-1, -1, pluginContext.irBuiltIns.stringType, ownerName.asStringStripSpecialMarkers()))
                        putValueArgument(2, IrGetValueImpl(-1, -1, itSymbol))
                        putValueArgument(3, IrConstImpl.string(-1, -1, pluginContext.irBuiltIns.stringType, expressionId))
                        fun String?.irConstImpl(): IrConstImpl<out String?> {
                            val nullableString = pluginContext.irBuiltIns.stringType.makeNullable()
                            val argument = if (this == null) {
                                IrConstImpl.constNull(-1, -1, nullableString)
                            } else {
                                IrConstImpl.string(-1, -1, nullableString, this)
                            }
                            return argument
                        }

                        val argument = receiverId.irConstImpl()
                        putValueArgument(4, argument)
                        putValueArgument(5, data.clazz?.fqNameWhenAvailable?.asString().irConstImpl())
                        putValueArgument(6, data.function?.name?.asString().irConstImpl())
                        putValueArgument(7, IrConstImpl.int(-1, -1, pluginContext.irBuiltIns.intType, data.statementIndex))
                    }
                }
            }
            val alsoLambdaExpression = IrFunctionExpressionImpl(
                -1,
                -1,
                type = pluginContext.irBuiltIns.functionN(2)
                    .typeWith(listOf(expression.type, pluginContext.irBuiltIns.unitType)),
                alsoLambda,
                IrStatementOrigin.LAMBDA
            )
            putValueArgument(0, alsoLambdaExpression)
        }
        return result
    }

    private fun expressionId(expression: IrExpression): String {
        val line = file.fileEntry.getLineNumber(expression.startOffset)
        val column = file.fileEntry.getColumnNumber(expression.startOffset)
        return "${file.path}:${line + 1}:${column + 1}"
    }
}
