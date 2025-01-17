/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.dataframe.plugin.extensions

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.localClassType
import org.jetbrains.kotlinx.dataframe.plugin.impl.api.readJson
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.copyAttributes
import org.jetbrains.kotlin.ir.declarations.createBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrDelegatingConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.IrStarProjectionImpl
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.findAnnotation
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.impl.data.IoSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.data.serialize
import org.jetbrains.kotlinx.dataframe.plugin.utils.Names
import org.jetbrains.org.objectweb.asm.Type
import java.io.File

class IrBodyFiller(
    private val resolutionPath: String?,
    private val schemasDirectory: String?
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        DataFrameFileLowering(pluginContext).lower(moduleFragment)
        if (schemasDirectory != null) {
            val schemas = mutableListOf<IoSchema>()
            moduleFragment.files.forEach {
                it.acceptChildrenVoid(object : IrElementVisitorVoid {
                    override fun visitElement(element: IrElement) {
                        if (element is IrCall) {
                            process(element, schemas)
                        }
                        element.acceptChildrenVoid(this)
                    }
                })
            }

            val dir = File(schemasDirectory)
            dir.mkdirs()
            val file = File(dir, "schemas.json")
            val res = if (file.exists()) {
                val json = file.readText()
                val res = try {
                    Json.decodeFromString<List<IoSchema>>(json)
                } catch (e: Exception) {
                    emptyList()
                }
                res + schemas
            } else {
                schemas
            }

            val text = Json.encodeToString(res)
            file.writeText(text)
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun process(
        element: IrCall,
        schemas: MutableList<IoSchema>
    ) {
        if (element.symbol.owner.name == Name.identifier("readJson")) {
            val path = (element.valueArguments.firstOrNull() as? IrConst)?.value as? String ?: return
            try {
                val df = readJson(resolutionPath, path)
                val json = df.schema().serialize()
                schemas.add(IoSchema(path, json))
            } catch (_: Exception) {

            }
        }
    }
}

private class DataFrameFileLowering(val context: IrPluginContext) : FileLoweringPass, IrElementTransformerVoid() {
    companion object {
        val COLUMNS_CONTAINER_ID =
            CallableId(ClassId(FqName("org.jetbrains.kotlinx.dataframe"), Name.identifier("ColumnsContainer")), Name.identifier("get"))
        val COLUMNS_SCOPE_ID =
            CallableId(ClassId(FqName("org.jetbrains.kotlinx.dataframe"), Name.identifier("ColumnsScope")), Name.identifier("get"))
        val DATA_ROW_ID =
            CallableId(ClassId(FqName("org.jetbrains.kotlinx.dataframe"), Name.identifier("DataRow")), Name.identifier("get"))
    }

    override fun lower(irFile: IrFile) {
        irFile.transformChildren(this, null)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.name.asString() == "generated_for_debugger_fun123") {
            declaration.returnType = declaration.returnType.hideExposedLocalType()
            declaration.parameters = declaration.parameters.mapNotNull {
                if ((it.type.classifierOrNull?.owner as? IrClass)?.isLocal == true) {
                    null
                } else {
                    it.type = it.type.hideExposedLocalType()
                    it
                }
            }
//            declaration.parameters.forEach {
//                it.type = it.type.hideExposedLocalType()
//                if ((it.type.classifierOrNull?.owner as? IrClass)?.isLocal == true) {
//                    it.type = context.irBuiltIns.anyNType
//                }
//            }
            declaration.acceptChildrenVoid(object  : IrElementVisitorVoid {
                override fun visitElement(element: IrElement) {
                    if (element is IrBlock) {
                        element.type = element.type.hideExposedLocalType()
                    }
                    if (element is IrExpression) {
                        element.type = element.type.hideExposedLocalType()
                    }
                    if (element is IrFunction) {
                        element.typeParameters = element.typeParameters.map { it.symbol.owner }
                    }
                    element.acceptChildrenVoid(this)
                }
            })
            val body = declaration.body
            if (body is IrExpressionBody) {
                body.expression.type.hideExposedLocalType()
            }
        }
//        val returnType = declaration.returnType as? IrSimpleType
//        if (returnType != null && returnType.classFqName?.asString() == "org.jetbrains.kotlinx.dataframe.DataFrame") {
//            val typeArgument = returnType.arguments.firstOrNull()?.typeOrNull as? IrSimpleType
//            val owner = typeArgument?.classifierOrNull?.owner
//            if (typeArgument != null && owner is IrClass) {
//                if (owner.isLocal && declaration.name.asString() == "generated_for_debugger_fun") {
//                    declaration.parameters = declaration.parameters.map { it }
//                    declaration.returnType = IrSimpleTypeImpl(returnType.classifier, true, listOf(IrStarProjectionImpl), returnType.annotations)
//                }
//            }
//        }
        return super<IrElementTransformerVoid>.visitSimpleFunction(declaration)
    }

    private fun IrType.hideExposedLocalType(): IrType {
        return if (this is IrSimpleTypeImpl) {
            val irClass = this.classifier.owner as? IrClass
            if (irClass?.isLocal == true) {
                irClass.localClassType = Type.getObjectType("localClassName")
                this
            } else if (classFqName?.asString() == "org.jetbrains.kotlinx.dataframe.DataFrame") {
                val typeArgument = arguments.firstOrNull()?.typeOrNull as? IrSimpleType
                val owner = typeArgument?.classifierOrNull?.owner
                if (owner is IrClass && owner.isLocal) {
                    owner.localClassType = Type.getObjectType("localClassName")
                    IrSimpleTypeImpl(classifier, true, listOf(IrStarProjectionImpl), annotations)
                } else {
                    this
                }
            } else {
                this
            }

        } else {
            this
        }
//        return if (this is IrSimpleType && classFqName?.asString() == "org.jetbrains.kotlinx.dataframe.DataFrame") {
//            val typeArgument = arguments.firstOrNull()?.typeOrNull as? IrSimpleType
//            val owner = typeArgument?.classifierOrNull?.owner
//            if (owner is IrClass && owner.isLocal) {
//                owner.localClassType = Type.getObjectType("localClassName")
//                IrSimpleTypeImpl(classifier, true, listOf(IrStarProjectionImpl), annotations)
//            } else {
//                this
//            }
//        } else {
//            this
//        }
    }

    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val origin = declaration.origin
        if (!(origin is IrDeclarationOrigin.GeneratedByPlugin && origin.pluginKey is TokenGenerator.Key)) return declaration
        declaration.body = generateBodyForDefaultConstructor(declaration)
        return declaration
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun generateBodyForDefaultConstructor(declaration: IrConstructor): IrBody? {
        val irType = declaration.returnType.superTypes()[0]
        val symbol = irType.classOrFail.owner.primaryConstructor?.symbol ?: return null
        val type = declaration.returnType as? IrSimpleType ?: return null
        val delegatingAnyCall = IrDelegatingConstructorCallImpl(
            -1,
            -1,
            irType,
            symbol,
            typeArgumentsCount = 0,
        ).also { it.copyAttributes(declaration.parentAsClass) }

        val initializerCall = IrInstanceInitializerCallImpl(
            -1,
            -1,
            (declaration.parent as? IrClass)?.symbol ?: return null,
            type
        )

        return context.irFactory.createBlockBody(-1, -1, listOf(delegatingAnyCall, initializerCall))
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitProperty(declaration: IrProperty): IrStatement {
        val origin = declaration.origin
        val pluginKey = (origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? DataFramePlugin
        if (pluginKey == null) {
            declaration.transformChildren(this, null)
            return declaration
        }
        val getter = declaration.getter ?: return declaration

        val constructors = context.referenceConstructors(ClassId(FqName("kotlin.jvm"), Name.identifier("JvmName")))
        val jvmName = constructors.single { it.owner.valueParameters.size == 1 }
        val marker =
            ((getter.extensionReceiverParameter!!.type as IrSimpleType).arguments.single() as IrSimpleType).classOrFail.owner
        val jvmNameArg = "${marker.nestedName()}_${declaration.name.identifier}"
        getter.annotations = listOf(
            IrConstructorCallImpl(-1, -1, jvmName.owner.returnType, jvmName, 0, 1)
                .also {
                    it.putValueArgument(0, IrConstImpl.string(-1, -1, context.irBuiltIns.stringType, jvmNameArg))
                }
        )
        val returnType = getter.returnType
        val isDataColumn = returnType.classFqName?.asString().let {
            it == DataColumn::class.qualifiedName!! || it == ColumnGroup::class.qualifiedName!!
        }

        val get = if (isDataColumn) {
            context
                .referenceFunctions(COLUMNS_SCOPE_ID)
                .single {
                    it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == context.irBuiltIns.stringType
                }
        } else {
            context
                .referenceFunctions(DATA_ROW_ID)
                .single {
                    it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == context.irBuiltIns.stringType
                }
        }

        val call = IrCallImpl(-1, -1, context.irBuiltIns.anyNType, get, 0).also {
            val thisSymbol: IrValueSymbol = getter.extensionReceiverParameter?.symbol!!
            it.dispatchReceiver = IrGetValueImpl(-1, -1, thisSymbol)
            val annotation = declaration.annotations.findAnnotation(Names.COLUMN_NAME_ANNOTATION.asSingleFqName())
            val columnName = (annotation?.valueArguments?.get(0) as? IrConst)?.value as? String
            val columName = columnName ?: declaration.name.identifier
            it.putValueArgument(0, IrConstImpl.string(-1, -1, context.irBuiltIns.stringType, columName))
        }

        val typeOp = IrTypeOperatorCallImpl(-1, -1, returnType, IrTypeOperator.CAST, returnType, call)
        val returnExpression = IrReturnImpl(-1, -1, returnType, getter.symbol, typeOp)
        getter.apply {
            body = factory.createBlockBody(-1, -1, listOf(returnExpression))
        }

        return declaration
    }

    private fun IrDeclarationWithName.nestedName() = buildString { computeNestedName(this@nestedName, this) }

    private fun computeNestedName(declaration: IrDeclarationWithName, result: StringBuilder): Boolean {
        when (val parent = declaration.parent) {
            is IrClass -> {
                if (!computeNestedName(parent, result)) return false
            }
            is IrPackageFragment -> {}
            else -> return false
        }
        if (result.isNotEmpty()) result.append('_')
        result.append(declaration.name.asString())
        return true
    }

    // org.jetbrains.kotlin.fir.backend.generators.CallAndReferenceGenerator#applyReceivers
    override fun visitTypeOperator(expression: IrTypeOperatorCall): IrExpression {
        if (isScope(expression.typeOperand)) {
            return expression.replaceWithConstructorCall()
        }
        return super.visitTypeOperator(expression)
    }

    override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
        if (!isScope(expression.type)) {
            return expression
        }
        return expression.replaceWithConstructorCall()
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun isScope(type: IrType): Boolean {
        val origin = (type.classifierOrNull?.owner as? IrClass)?.origin ?: return false
        val fromPlugin = origin is IrDeclarationOrigin.GeneratedByPlugin && origin.pluginKey is DataFramePlugin
        val scopeReference = type.classFqName?.shortName()?.asString()?.startsWith("Scope") ?: false
        return fromPlugin || scopeReference
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrExpression.replaceWithConstructorCall(): IrConstructorCallImpl {
        val constructor = type.getClass()!!.constructors.toList().single()
        return IrConstructorCallImpl(-1, -1, type, constructor.symbol, 0, 0)
    }
}
