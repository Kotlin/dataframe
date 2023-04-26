package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder

fun TestConfigurationBuilder.commonFirWithPluginFrontendConfiguration() {
    defaultDirectives {
//        +ENABLE_PLUGIN_PHASES
//        +FIR_DUMP
    }

    useConfigurators(
        ::ExtensionRegistrarConfigurator,
    )
}
