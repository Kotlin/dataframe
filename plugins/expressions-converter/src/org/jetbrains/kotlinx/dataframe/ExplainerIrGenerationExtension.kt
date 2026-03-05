package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

public class ExplainerIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val lowering = ExplainerIrTransformer(pluginContext)
        for (file in moduleFragment.files) {
            lowering.lower(file)
        }
    }
}
