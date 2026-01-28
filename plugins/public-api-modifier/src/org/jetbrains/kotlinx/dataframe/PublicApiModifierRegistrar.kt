package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.copy
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.name.FqName

@OptIn(ExperimentalCompilerApi::class)
class PublicApiModifierRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override val pluginId: String = "org.jetbrains.kotlinx.dataframe.PublicApiModifier"

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(Extensions())
    }
}

class Extensions : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::StatusTransformer
    }
}

class StatusTransformer(session: FirSession) : FirStatusTransformerExtension(session) {
    companion object {
        val apiOverload = FqName("org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload")
    }

    override fun needTransformStatus(declaration: FirDeclaration): Boolean =
        declaration.annotations.any {
            it.fqName(session) == apiOverload
        }

    override fun transformStatus(
        status: FirDeclarationStatus,
        function: FirSimpleFunction,
        containingClass: FirClassLikeSymbol<*>?,
        isLocal: Boolean,
    ): FirDeclarationStatus = status.copy(visibility = Visibilities.Internal)
}
