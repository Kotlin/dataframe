window.addEventListener('load', function () {
    function sendTheme(theme, iframe) {
        const maxAttempts = 10;
        const interval = 200; // 200ms
        let attempts = 0;

        function attemptSend() {
            if (attempts >= maxAttempts) return;
            iframe.contentWindow.postMessage({ type: 'setTheme', theme }, '*');
            attempts++;
        }

        attemptSend();
        const intervalId = setInterval(() => {
            attemptSend();
            if (attempts >= maxAttempts) clearInterval(intervalId);
        }, interval);
    }

    function setIframeHeight(iframe, height) {
        iframe.style.height = height + 'px';
    }

    const htmlElement = document.documentElement;

    function getCurrentTheme() {
        return htmlElement.classList.contains('theme-light') ? 'light' : 'dark';
    }

    window.addEventListener('message', (event) => {
        if (event.data.type === 'iframeHeight') {
            document.querySelectorAll('iframe').forEach((iframe) => {
                if (iframe.contentWindow === event.source) {
                    setIframeHeight(iframe, event.data.height);
                }
            });
        }
    });

    function updateAllIframeThemes() {
        const theme = getCurrentTheme();
        document.querySelectorAll('iframe').forEach((iframe) => {
            if (iframe.contentWindow) {
                sendTheme(theme, iframe);
            }
        });
    }

    const observer = new MutationObserver(() => {
        updateAllIframeThemes();
    });

    observer.observe(htmlElement, { attributes: true });

    function observeIframeLoad(iframe) {
        iframe.addEventListener('load', () => sendTheme(getCurrentTheme(), iframe));
        if (iframe.contentDocument && iframe.contentDocument.readyState === 'complete') {
            sendTheme(getCurrentTheme(), iframe);
        }
    }

    document.querySelectorAll('iframe').forEach(observeIframeLoad);

    const bodyObserver = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            mutation.addedNodes.forEach((node) => {
                if (node.tagName === 'IFRAME') observeIframeLoad(node);
                else if (node.querySelectorAll) node.querySelectorAll('iframe').forEach(observeIframeLoad);
            });
        });
    });

    bodyObserver.observe(document.body, { childList: true, subtree: true });

    updateAllIframeThemes();
});
