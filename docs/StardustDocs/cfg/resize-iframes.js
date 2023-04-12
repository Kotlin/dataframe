<script>
    window.onload = function () {
        function updateIframeThemes(theme) {
            const iframes = document.getElementsByTagName('iframe');

            for (let i = 0; i < iframes.length; i++) {
                const iframeDocument = iframes[i].contentWindow.document;
                iframeDocument.documentElement.setAttribute('theme', theme);
            }
        }

        function observeHtmlClassChanges() {
            const htmlElement = document.documentElement;

            const observer = new MutationObserver((mutations) => {
                mutations.forEach((mutation) => {
                    if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
                        const theme = htmlElement.classList.contains('theme-light') ? 'light' : 'dark';
                        // console.log(theme)
                        updateIframeThemes(theme);
                    }
                });
            });

            observer.observe(htmlElement, { attributes: true });
        }

        function resize_iframe_out(el) {
            const htmlElement = document.documentElement;
            const theme = htmlElement.classList.contains('theme-light') ? 'light' : 'dark';
            el.contentWindow.document.documentElement.setAttribute('theme', theme);

            let h = el.contentWindow.document.body.scrollHeight;
            el.height = h === 0 ? 0 : h + 41;
        }

        function doObserveIFrame(el) {
            resize_iframe_out(el)
            let observer = new MutationObserver(function (mutations) {
                var resize = false
                for (let mutation of mutations) {
                    // skip attribute changes except open to avoid loop
                    // (callback sees change in iframe, triggers resize, sees change...)
                    if (mutation.type === 'attributes') {
                        if (mutation.attributeName === 'open') {
                            resize_iframe_out(el)
                            resized = true
                        }
                    } else {

                        resize = true
                    }
                }
                if (resize) {
                    resize_iframe_out(el)
                }

    // if (mutations[0].addedNodes.length || mutations[0].removedNodes.length) {

                // }
            });

            observer.observe(el.contentDocument.documentElement, {childList: true, subtree: true, characterData: true, attributes: true});

        }

        function observeIFrame(el) {
            // console.log("el.contentWindow: " + el.contentWindow)
            // console.log("el.contentWindow.document: " + el.contentWindow.document)
            // console.log("el.contentWindow.document.body: " + el.contentWindow.document.body)
            // console.log("el.contentWindow.performance: " + el.contentWindow.performance)
            // console.log("el.contentWindow.performance.timing.loadEventEnd: " + el.contentWindow.performance.timing.loadEventEnd)

            if (el.contentWindow && el.contentWindow.performance && el.contentWindow.performance.timing.loadEventEnd === 0) {
                // console.log("ready path")
                el.addEventListener('load', () => doObserveIFrame(el), true)
            } else {
                // console.log("not ready path")
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
                    // console.log(addedNode.tagName)
                    if (addedNode.tagName === 'IFRAME') {
                        // console.log("IFRAME loaded")
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
        observeHtmlClassChanges()
        // initialize theme
        const htmlElement = document.documentElement;
        const theme = htmlElement.classList.contains('theme-light') ? 'light' : 'dark';
        updateIframeThemes(theme);
    }
</script>
