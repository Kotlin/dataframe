package org.jetbrains.kotlinx.dataframe.explainer

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration

val SamplesDisplayConfiguration = DisplayConfiguration(enableFallbackStaticTables = false)

val WritersideStyle = DataFrameHtmlData(
    // copy writerside stlyles
    style =
        """
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
    script =
        """
function sendHeight() {
    let totalHeight = 0;

    const detailsElements = document.querySelectorAll('details');

    detailsElements.forEach(detail => {
       
        const summary = detail.querySelector('summary');
        if (summary) {
            totalHeight += summary.offsetHeight;
        }

      
        if (detail.open) {
            const table = detail.querySelector('table.dataframe');
            if (table) {
                totalHeight += table.offsetHeight;

                const styles = getComputedStyle(table);
                totalHeight += parseFloat(styles.marginTop) + parseFloat(styles.marginBottom) + 10;
            }

            const description = detail.querySelector('.dataframe_description');
            if (description) {
                totalHeight += description.offsetHeight;
            }
        }
    });
    
    totalHeight = Math.ceil(totalHeight + 16);

    window.parent.postMessage({type: 'iframeHeight', height: totalHeight}, '*');
}

function repeatHeightCalculation(maxRetries = 10, interval = 100) {
    let retries = 0;
    const intervalId = setInterval(() => {
        sendHeight();
        retries++;
        if (retries >= maxRetries) clearInterval(intervalId);
    }, interval);
}

window.addEventListener('load', () => {
    repeatHeightCalculation();

    document.querySelectorAll('details').forEach(detail => {
        detail.addEventListener('toggle', () => {
            setTimeout(sendHeight, 30);
        });
    });
});

const allObservedTables = document.querySelectorAll('table.dataframe');
allObservedTables.forEach((table) => {
    const observer = new MutationObserver(sendHeight);
    observer.observe(table, {
        childList: true,
        subtree: true,
        characterData: true,
    });
});
        """.trimIndent()
)

val WritersideFooter: (DataFrame<*>) -> String = { "" }
