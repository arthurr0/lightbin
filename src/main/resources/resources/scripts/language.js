// adding listener for content load
window.addEventListener('DOMContentLoaded', () => {
    const element = document.getElementById("language");
    fetch("/api/v1/syntaxes", { method: 'GET', headers: { 'Content-Type': 'text/plain' }})
        .then(response => response.json())
        .then(data => {
            for (const language of data) {
                element.appendChild(new Option(language, language))
            }
        });
});
