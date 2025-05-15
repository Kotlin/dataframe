window.addEventListener('load', () => {
    function updateIframeThemes(theme) {
        const iframes = document.querySelectorAll('iframe');

        iframes.forEach((iframe) => {
            if (iframe.contentWindow && iframe.contentWindow.document) {
                iframe.contentWindow.document.documentElement.setAttribute('theme', theme);
            }
        });
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

    window.addEventListener('message', (event) => {
        if (event.data.type === 'iframeHeight') {
            document.querySelectorAll('iframe').forEach((iframe) => {
                if (iframe.contentWindow === event.source) {
                    iframe.style.height = event.data.height + 'px';
                }
            });
        }
    });

    function observeIframe(iframe) {
        const theme = document.documentElement.classList.contains('theme-light') ? 'light' : 'dark';

        function sendTheme() {
            iframe.contentDocument.documentElement.setAttribute('theme', theme);
        }

        iframe.addEventListener('load', sendTheme);
        if (iframe.contentDocument.readyState === 'complete') sendTheme();
    }

    document.querySelectorAll('iframe').forEach(observeIframe);

    const bodyObserver = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            mutation.addedNodes.forEach((node) => {
                if (node.tagName === 'IFRAME') observeIframe(node);
                else if (node.querySelectorAll) {
                    node.querySelectorAll('iframe').forEach(observeIframe);
                }
            });
        });
    });

    bodyObserver.observe(document.body, { childList: true, subtree: true });

    observeHtmlClassChanges();

    const initialTheme = document.documentElement.classList.contains('theme-light') ? 'light' : 'dark';
    updateIframeThemes(initialTheme);
});
