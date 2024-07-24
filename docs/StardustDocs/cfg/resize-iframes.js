<meta name="google-site-verification" content="Lffz_ab-_S5cmA07ZXVbucHVklaRsnk8gEt8frHKjMk"/>
<!-- Google Analytics -->
<script>
    (function (w, d, s, l, i) {
        w[l] = w[l] || [];
        w[l].push({ 'gtm.start': new Date().getTime(), event: 'gtm.js' });
        var f = d.getElementsByTagName(s)[0], j = d.createElement(s), dl = l != 'dataLayer' ? '&amp;l=' + l : '';
        j.async = true;
        j.src = '//www.googletagmanager.com/gtm.js?id=' + i + dl;
        f.parentNode.insertBefore(j, f);
    })(window, document, 'script', 'dataLayer', 'GTM-5P98');
</script>
<!-- End Google Analytics -->
<script>
    window.onload = function() {
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
            resize_iframe_out(el);
            const mutationObserver = new MutationObserver((mutations) => {
                let resize = false;
                for (let mutation of mutations) {
                    // skip attribute changes except open to avoid loop
                    // (callback sees change in iframe, triggers resize, sees change...)
                    if (mutation.type === 'attributes') {
                        if (mutation.attributeName === 'open') {
                            resize = true;
                        }
                    } else {
                        resize = true;
                    }
                }
                if (resize) {
                    resize_iframe_out(el);
                }
            });
            mutationObserver.observe(
                el.contentDocument.documentElement,
                { childList: true, subtree: true, characterData: true, attributes: true },
            );

            const visibilityObserver = new IntersectionObserver(
                () => resize_iframe_out(el),
                { root: document.documentElement },
            );
            visibilityObserver.observe(el);
        }

        function observeIFrame(el) {
            if (el.contentWindow &&
                el.contentWindow.performance &&
                el.contentWindow.performance.timing.loadEventEnd === 0
            ) {
                el.addEventListener('load', () => doObserveIFrame(el), true);
            } else {
                doObserveIFrame(el);
            }
        }

        let iframes = document.querySelectorAll('iframe');
        for (let i = 0; i < iframes.length; i++) {
            observeIFrame(iframes[i]);
        }

        let bodyObserver = new MutationObserver((mutations) => {
            mutations.forEach(function(mutation) {
                for (let i = 0; i < mutation.addedNodes.length; i++) {
                    let addedNode = mutation.addedNodes[i];
                    if (addedNode.tagName === 'IFRAME') {
                        observeIFrame(addedNode);
                    } else if (addedNode.tagName === 'SECTION') {
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

        bodyObserver.observe(
            document.documentElement || document.body,
            { childList: true, subtree: true, attributes: true },
        );
        observeHtmlClassChanges();

        // initialize theme
        const htmlElement = document.documentElement;
        const theme = htmlElement.classList.contains('theme-light') ? 'light' : 'dark';
        updateIframeThemes(theme);
    };
</script>
