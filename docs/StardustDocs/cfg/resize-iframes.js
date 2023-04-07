<script>
    window.onload = function () {
        function resize_iframe_out(el) {
            let h = el.contentWindow.document.body.scrollHeight;
            el.height = h === 0 ? 0 : h + 41;
        }

        function doObserveIFrame(el) {
            resize_iframe_out(el)
            let observer = new MutationObserver(function (mutations) {
                // if (mutations[0].addedNodes.length || mutations[0].removedNodes.length) {
                resize_iframe_out(el)
                // }
            });

            observer.observe(el.contentDocument.documentElement, {childList: true, subtree: true, characterData: true, attributes: true});

        }

        function observeIFrame(el) {
            console.log("el.contentWindow: " + el.contentWindow)
            console.log("el.contentWindow.document: " + el.contentWindow.document)
            console.log("el.contentWindow.document.body: " + el.contentWindow.document.body)
            console.log("el.contentWindow.performance: " + el.contentWindow.performance)
            console.log("el.contentWindow.performance.timing.loadEventEnd: " + el.contentWindow.performance.timing.loadEventEnd)

            if (el.contentWindow && el.contentWindow.performance && el.contentWindow.performance.timing.loadEventEnd === 0) {
                console.log("ready path")
                el.addEventListener('load', () => doObserveIFrame(el), true)
            } else {
                console.log("not ready path")
                doObserveIFrame(el)
            }
        }

        let iframes = document.querySelectorAll('iframe');

        for (let i = 0; i < iframes.length; i++) {
            observeIFrame(iframes[i])
        }

        let bodyObserver = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                for (let i = 0; i < mutation.addedNodes.length; i++) {
                    let addedNode = mutation.addedNodes[i];
                    console.log(addedNode.tagName)
                    if (addedNode.tagName === 'IFRAME') {
                        console.log("IFRAME loaded")
                        observeIFrame(addedNode);
                    } else if (addedNode.tagName === 'SECTION') {
                        let iframes = [];

                        function traverse(node) {
                            if (node.tagName === 'IFRAME') {
    observeIFrame(node);
                        }

                        for (let i = 0; i < node.children.length; i++) {
                            traverse(node.children[i]);
                        }
                    }

                        traverse(addedNode);
                    }
                }
            });
        });

        bodyObserver.observe(document.documentElement || document.body, {childList: true, subtree: true, attributes: true});
    }
</script>
