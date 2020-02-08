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

function axios(url, method, data, headers = undefined) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url);
        if (headers) {
            Object.keys(headers).forEach(key => xhr.setRequestHeader(key, headers[key]));
        }
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== 4) {
                return;
            }
            if (xhr.status === 200) {
                resolve({resp: xhr.response, respHeaders: xhr.getAllResponseHeaders()});
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.send(data);
    });
}

window.addEventListener("message", function (e) {
    if ('webpackOk' !== e.data.type) {
        let {url, headers, method, params, logUrl, logHeaders, apiId} = e.data;
        if (!url) {
            return;
        }
        axios(url, method, JSON.stringify(params), headers).then(({resp, respHeaders}) => {
            document.getElementById('resp-headers').innerText = respHeaders;
            document.getElementById('resp-data').innerText = formatJson(resp);
            if (headers) {
                let headerStr = '';
                Object.keys(headers).forEach(key => headerStr = headerStr + key + ': ' + headers[key] + '\r\n');
                document.getElementById('req-headers').innerText = headerStr;
            }
            document.getElementById('req-data').innerText = formatJson(params);
            // save to history
            let split = respHeaders.split('\r\n');
            let headerDict = {};
            split.forEach(item => {
                let keyValue = item.split(': ');
                if ('' !== keyValue[0]) {
                    headerDict[keyValue[0]] = keyValue[1];
                }
            });
            let jsonHeaders = JSON.stringify(headerDict);
            axios(logUrl, 'POST', JSON.stringify({
                apiId: apiId,
                requestInfo: JSON.stringify({headers: headers, data: params}),
                responseInfo: JSON.stringify({headers: headerDict, data: resp}),
            }), logHeaders);
        });
    }
}, false);
