package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder

fun TestConfigurationBuilder.commonFirWithPluginFrontendConfiguration() {
    useConfigurators(
        ::ExtensionRegistrarConfigurator,
    )
}
