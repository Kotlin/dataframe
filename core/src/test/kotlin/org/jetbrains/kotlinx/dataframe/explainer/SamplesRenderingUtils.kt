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

    document.querySelectorAll('body > details, body > br, body > table').forEach(element => {
        if (element.tagName === 'DETAILS') {
            totalHeight += getElementHeight(element.querySelector(':scope > summary'));

            if (element.open) {
                totalHeight += getVisibleContentHeight(element);
            }
        } else if (element.tagName === 'BR') {
            totalHeight += getElementHeight(element);
        } else if (element.tagName === 'TABLE') {
            totalHeight += getElementHeight(element);
        }
    });

    totalHeight += 10;

    window.parent.postMessage({type: 'iframeHeight', height: Math.ceil(totalHeight)}, '*');
}

function getVisibleContentHeight(detailsElement) {
    let height = 0;

    detailsElement.querySelectorAll(':scope > details, :scope > table, :scope > p').forEach(child => {
        if (child.tagName === 'DETAILS') {
            const summary = child.querySelector(':scope > summary');
            height += getElementHeight(summary);

            if (child.open) {
                height += getDirectVisibleContentHeight(child);
            }
        } else if (isElementVisible(child)) {
            height += getElementHeight(child);
        }
    });

    return height;
}

function getDirectVisibleContentHeight(element) {
    let height = 0;
    element.querySelectorAll(':scope > table, :scope > p, :scope > summary').forEach(child => {
        if (isElementVisible(child)) {
            height += getElementHeight(child);
        }
    });
    return height;
}

function getElementHeight(el) {
    const styles = getComputedStyle(el);
    const margin = parseFloat(styles.marginTop) + parseFloat(styles.marginBottom);
    
    // More reliable cross-browser calculation
    const rect = el.getBoundingClientRect();
    return rect.height + margin;
}

function isElementVisible(el) {
    return !!(el.offsetWidth || el.offsetHeight || el.getClientRects().length);
}

function sendInitialHeight() {
    let initialHeight = 0;

    document.querySelectorAll('body > details, body > br, body > table').forEach(element => {
        if (element.tagName === 'DETAILS') {
            initialHeight += getElementHeight(element.querySelector(':scope > summary'));
        } else if (element.tagName === 'BR') {
            initialHeight += getElementHeight(element);
        } else if (element.tagName === `TABLE`) {
            initialHeight += getElementHeight(element);
        }
    });

    initialHeight += 10;

    window.parent.postMessage({type: 'iframeHeight', height: Math.ceil(initialHeight)}, '*');
}

function repeatHeightCalculation(maxRetries = 10, interval = 100) {
    let retries = 0;
    const intervalId = setInterval(() => {
        sendInitialHeight();
        retries++;
        if (retries >= maxRetries) clearInterval(intervalId);
    }, interval);
}

window.addEventListener('load', () => {
    repeatHeightCalculation();


    document.querySelectorAll('details').forEach(detail => {
        detail.addEventListener('toggle', () => {
            setTimeout(sendHeight, 50);
        });
    });

    const observer = new MutationObserver(() => setTimeout(sendHeight, 50));
    observer.observe(document.body, {childList: true, subtree: true, characterData: true});
});
        """.trimIndent(),
)

val WritersideFooter: (DataFrame<*>) -> String = { "" }
