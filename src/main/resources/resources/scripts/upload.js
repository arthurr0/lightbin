function uploadSnippet() {
    const content = document.getElementById('content');
    const language = document.getElementById('language');
    if (!(content.value)) {
        halfmoon.initStickyAlert({
            title: 'Error',
            content: 'You can\'t upload empty snippet.'
        });
        return;
    }

    if (!(language.value)) {
        halfmoon.initStickyAlert({
            title: 'Error',
            content: 'Not selected language.'
        });
        return;
    }

    fetch("/api/v1/snippets/" + language.value + "/", { method: 'POST', headers: { 'Content-Type': 'text/plain' }, body: document.getElementById('content').value })
        .then(response => response.json())
        .then(data => {
            // if everything is ok
            if (data.status === 200) {
                // getting view button and setting its target
                const viewButton = document.getElementById('view-button');
                viewButton.addEventListener('click', () => {
                    window.location.href = '/snippet/' + data.identifier;
                });

                // getting copy button and setting its target
                const copyButton = document.getElementById('copy-button');
                copyButton.addEventListener('click', () => {
                    // copying link
                    copyToClipboard('https://bin.shitzuu.dev/snippet/' + data.identifier);
                });

                // showing success modal
                location.href = "#modal-3"
            } else {
                halfmoon.initStickyAlert({
                    title: 'Error',
                    content: data.status + ": " + data.content,
                });
            }
        });
}

// Copies a string to the clipboard. Must be called from within an
// event handler such as click. May return false if it failed, but
// this is not always possible. Browser support for Chrome 43+,
// Firefox 42+, Safari 10+, Edge and Internet Explorer 10+.
// Internet Explorer: The clipboard feature may be disabled by
// an administrator. By default a prompt is shown the first
// time the clipboard is used (per session).
function copyToClipboard(text) {
    if (window.clipboardData && window.clipboardData.setData) {
        // Internet Explorer-specific code path to prevent textarea being shown while dialog is visible.
        return window.clipboardData.setData("Text", text);
    } else if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
        const element = document.createElement("textarea");
        element.textContent = text;
        element.style.position = "fixed";  // Prevent scrolling to bottom of page in Microsoft Edge.
        document.body.appendChild(element);
        element.select();
        try {
            return document.execCommand("copy");  // Security exception may be thrown by some browsers.
        }
        catch (exception) {
            console.warn("Copy to clipboard failed.", ex);
        }
        finally {
            document.body.removeChild(element);
        }
    }
}