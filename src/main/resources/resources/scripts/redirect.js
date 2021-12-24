// https://stackoverflow.com/a/11690095
function redirect(url) {
    const userAgent = navigator.userAgent.toLowerCase();
    const explorer = userAgent.indexOf('msie') !== -1;
    const version = parseInt(userAgent.substr(4, 2), 10);

    // Internet Explorer 8 and lower
    if (explorer && version < 9) {
        const link = document.createElement('a');
        link.href = url;
        document.body.appendChild(link);
        link.click();
    }

    // All other browsers can use the standard window.location.href (they don't lose HTTP_REFERER like Internet Explorer 8 & lower does)
    else {
        window.location.href = url;
    }
}