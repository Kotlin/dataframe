<script>
    window.onload = function () {
        function resize_iframe_out(el) {
            let h = el.contentWindow.document.body.scrollHeight;
            el.height = h === 0 ? 0 : h + 41;
        }

        function observeIFrame(el) {
            resize_iframe_out(el)
            let observer = new MutationObserver(function (mutations) {
                // if (mutations[0].addedNodes.length || mutations[0].removedNodes.length) {
                resize_iframe_out(el)
                // }
            });

            observer.observe(el.contentDocument.documentElement, {childList: true, subtree: true, characterData: true, attributes: true});

        }

        let iframes = document.querySelectorAll('iframe');

        for (let i = 0; i < iframes.length; i++) {
            observeIFrame(iframes[i])
        //     resize_iframe_out(iframes[i])
        //     let observer = new MutationObserver(function (mutations) {
        //     // if (mutations[0].addedNodes.length || mutations[0].removedNodes.length) {
        //         resize_iframe_out(iframes[i])
        //     // }
        // });
        //
        //     observer.observe(iframes[i].contentDocument.documentElement, {childList: true, subtree: true, characterData: true, attributes: true});
        }
    }
</script>
