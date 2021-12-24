//loading languages from api
function loadLanguage() {
    const element = document.getElementById("language");
    fetch("/api/v1/syntaxes", { method: 'GET', headers: { 'Content-Type': 'text/plain' }})
        .then(response => response.json())
        .then(data => {
            for (const lang of data) {
                element.appendChild(new Option(lang, lang))
            }
        });
}

// adding listener for content load
window.addEventListener('DOMContentLoaded', () => {
    loadLanguage();
});
