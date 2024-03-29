function isJSON(str) {
    if (typeof str == 'string') {
        try {
            let obj = JSON.parse(str);
            return !!(typeof obj == 'object' && obj);
        } catch (e) {
            return false;
        }
    }
}

function formatJson(json, options) {
    let reg,
        formatted = '',
        pad = 0,
        PADDING = '    '; // one can also use '\t' or a different number of spaces
    // optional settings
    options = options || {};
    // remove newline where '{' or '[' follows ':'
    options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true);
    // use a space after a colon
    options.spaceAfterColon = (options.spaceAfterColon !== false);

    // begin formatting...

    // make sure we start with the JSON as a string
    if (typeof json !== 'string') {
        json = JSON.stringify(json);
    }
    // parse and stringify in order to remove extra whitespace
    json = JSON.parse(json);
    json = JSON.stringify(json);

    // add newline before and after curly braces
    reg = /([{}])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline before and after square brackets
    reg = /([[]])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline after comma
    reg = /(,)/g;
    json = json.replace(reg, '$1\r\n');

    // remove multiple newlines
    reg = /(\r\n\r\n)/g;
    json = json.replace(reg, '\r\n');

    // remove newlines before commas
    reg = /\r\n,/g;
    json = json.replace(reg, ',');

    // optional formatting...
    if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
        reg = /:\r\n{/g;
        json = json.replace(reg, ':{');
        reg = /:\r\n\[/g;
        json = json.replace(reg, ':[');
    }
    if (options.spaceAfterColon) {
        reg = /:/g;
        json = json.replace(reg, ': ');
    }

    json.split('\r\n').forEach(node => {
        let i = 0,
            indent = 0,
            padding = '';

        if (node.match(/{$/) || node.match(/\[$/)) {
            indent = 1;
        } else if (node.match(/}/) || node.match(/]/)) {
            if (pad !== 0) {
                pad -= 1;
            }
        } else {
            indent = 0;
        }

        for (i = 0; i < pad; i++) {
            padding += PADDING;
        }
        formatted += padding + node + '\r\n';
        pad += indent;
    });
    return formatted;
}

function getCurrentTabId(callback) {
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        if (callback) callback(tabs.length ? tabs[0].id : null);
    });
}

function sendMessageToContentScript(message, callback) {
    getCurrentTabId((tabId) => {
        chrome.tabs.sendMessage(tabId, message, function (response) {
            if (callback) callback(response);
        });
    });
}

function sendError(error) {
    if (error && error !== '') {
        console.log(error);
        sendMessageToContentScript(JSON.stringify(error));
    }
}

function axios(url, requestParamType, method, data, headers = undefined) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        if (headers) {
            Object.keys(headers).forEach(key => xhr.setRequestHeader(key, headers[key]));
        }
        xhr.onerror = function (e) {
            alert("Unknown Error Occurred. Server response not received.");
        };
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== 4) {
                return;
            }
            if (xhr.status === 200) {
                resolve({resp: xhr.response, respHeaders: xhr.getAllResponseHeaders()});
            } else {
                reject({status: xhr.status, statusText: xhr.statusText});
            }
        };
        let reqData = '';
        if (requestParamType === 1) {
            // use x-www-form-urlencoded
            Object.keys(data).forEach(key => reqData += (key + '=' + encodeURIComponent(data[key]) + '&'));
        } else {
            reqData = JSON.stringify(data);
        }
        xhr.send(reqData);
    });
}

chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    try {
        let {url, requestParamType, headers, method, params, logUrl, logHeaders, apiId, testCaseInfo} = request;
        if (!url) {
            return;
        }
        let start = new Date().getTime();
        axios(url, requestParamType, method, params, headers).then(({resp, respHeaders}) => {
            sendMessageToContentScript({
                reqHeaders: headers,
                reqData: typeof params === 'string' && !isJSON(params) ? params : formatJson(params),
                respHeaders: respHeaders,
                respData: typeof resp === 'string' && !isJSON(resp) ? resp : formatJson(resp),
                testCaseInfo,
            });
            // save history
            let split = respHeaders.split('\r\n');
            let headerDict = {};
            split.forEach(item => {
                let keyValue = item.split(': ');
                if ('' !== keyValue[0]) {
                    headerDict[keyValue[0]] = keyValue[1];
                }
            });
            let jsonHeaders = JSON.stringify(headerDict);
            // save  method,url,requestTime
            axios(logUrl, 0, 'POST', {
                apiId: apiId,
                method: method,
                url: url,
                requestTime: new Date().getTime() - start,
                requestInfo: JSON.stringify({headers: headers, data: params}),
                responseInfo: JSON.stringify({headers: headerDict, data: resp}),
            }, logHeaders).catch(error => sendError(error));
            }).catch(error => sendError(error));
    } finally {
        sendResponse();
    }
});
