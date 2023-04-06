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
            if (el.contentWindow && el.contentWindow.performance && el.contentWindow.performance.timing.loadEventEnd === 0) {
                el.addEventListener('load', () => doObserveIFrame(el), true)
            } else {
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
                    if (addedNode.tagName === 'IFRAME') {
                        console.log("added iframe")
                        observeIFrame(addedNode);
                    }
                }
            });
        });

        bodyObserver.observe(document.body, {childList: true, subtree: true, attributes: true});
    }
</script>
