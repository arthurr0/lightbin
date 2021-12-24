// getting our button
const element = document.getElementById('theme-toggler');

// adding click listener
element.addEventListener('click', () => {
    // toggling theme
    halfmoon.toggleDarkMode();

    // setting cookie with remembered theme
    createCookie('dark-mode', halfmoon.darkModeOn ? 'true' : 'false');
});

// adding listener for content load
window.addEventListener('DOMContentLoaded', () => {
    // if cookie is set to true, then set dark mode
    if (readCookie('dark-mode') === 'true') {
        // toggling theme
        halfmoon.toggleDarkMode();
    }
});

// https://www.quirksmode.org/js/cookies.html
function createCookie(name, value, days) {
    let expires;
    if (days) {
        const date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));

        expires = "; expires=" + date.toGMTString();
    } else {
        expires = "";
    }
    document.cookie = name+"="+value+expires+"; path=/";
}

// https://www.quirksmode.org/js/cookies.html
function readCookie(name) {
    const nameEQ = name + "=";

    const ca = document.cookie.split(';');
    for (let index = 0; index < ca.length; index++) {
        let c = ca[index];
        while (c.charAt(0)===' ') {
            c = c.substring(1,c.length);
        }

        if (c.indexOf(nameEQ) === 0) {
            return c.substring(nameEQ.length,c.length);
        }
    }

    return null;
}