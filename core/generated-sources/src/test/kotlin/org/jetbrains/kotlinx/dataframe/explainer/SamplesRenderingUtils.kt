package org.jetbrains.kotlinx.dataframe.explainer

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration

val SamplesDisplayConfiguration = DisplayConfiguration(enableFallbackStaticTables = false)

val WritersideStyle = DataFrameHtmlData(
    // copy writerside stlyles
    style = """
    body {
        font-family: "JetBrains Mono",SFMono-Regular,Consolas,"Liberation Mono",Menlo,Courier,monospace;
    }       
    
    :root {
        color: #19191C;
        background-color: #fff;
    }
    
    :root[theme="dark"] {
        background-color: #19191C;
        color: #FFFFFFCC
    }
    
    details details {
        margin-left: 20px; 
    }
    
    summary {
        padding: 6px;
    }
    """.trimIndent(),
)

val WritersideFooter: (DataFrame<*>) -> String = { "" }
