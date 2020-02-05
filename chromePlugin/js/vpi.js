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
                let responseHeaders = xhr.getAllResponseHeaders();
                let split = responseHeaders.split('\r\n');
                let headerDict = {};
                split.forEach(item => {
                    let keyValue = item.split(': ');
                    if ('' !== keyValue[0]) {
                        headerDict[keyValue[0]] = keyValue[1];
                    }
                });
                console.log(xhr.response);
                console.log(headerDict);
                resolve({resp: xhr.response, respHeaders: JSON.stringify(headerDict)});
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.send(data);
    });
}

window.addEventListener("message", function (e) {
    if ('webpackOk' !== e.data.type) {
        console.log(e.data);
        let {url, headers, method, params} = e.data;
        axios(url, method, JSON.stringify(params), headers).then(({resp, respHeaders}) => {
            console.log(resp);
            document.getElementById('hidden-response').innerText = resp;
            // let headerDict = {};
            // Object.keys(headers).forEach(key=>headerDict[key]=)
            console.log(respHeaders);
            document.getElementById('hidden-response-header').innerText = respHeaders;
        });
    }
}, false);
