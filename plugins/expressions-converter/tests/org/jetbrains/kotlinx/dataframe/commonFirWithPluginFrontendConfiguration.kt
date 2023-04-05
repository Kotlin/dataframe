package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.FIR_DUMP


fun TestConfigurationBuilder.commonFirWithPluginFrontendConfiguration() {

    defaultDirectives {
//        +ENABLE_PLUGIN_PHASES
//        +FIR_DUMP
    }

    useConfigurators(
        ::ExtensionRegistrarConfigurator,
    )
}
