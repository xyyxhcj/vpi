window.addEventListener("message", function (e) {
    if ('webpackOk' !== e.data.type) {
        // send to background.js
        chrome.runtime.sendMessage(e.data);
    }
}, false);

function getHeaderStr(reqHeaders) {
    let headerStr = '';
    Object.keys(reqHeaders).forEach(key => headerStr = headerStr + key + ': ' + reqHeaders[key] + '\r\n');
    return headerStr;
}

// listen background message
chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    try {
        if (typeof request === 'string') {
            console.log('abnormal operation >>> ', request);
            return;
        }
        let {reqHeaders, reqData, respHeaders, respData, testCaseInfo} = request;
        if (testCaseInfo !== undefined) {
            let {checkField, checkValue, testCaseId, errorBackground, failBackground} = testCaseInfo;
            // check response
            let fieldQueue = checkField.split('.')
            let respObj;
            try {
                respObj = JSON.parse(respData);
            } catch (e) {
                console.warn("response data not json!", e, respData);
                return;
            }
            let fieldVal = undefined;
            while (fieldQueue.length > 0) {
                const field = fieldQueue.shift();
                fieldVal = respObj[field];
                if (!fieldVal) {
                    break;
                }
            }
            let caseResult = "error";
            let background = errorBackground;
            if (checkValue !== undefined && fieldVal !== undefined) {
                if (Object.is(checkValue.toString(), fieldVal.toString())) {
                    caseResult = "success";
                    background = '';
                } else {
                    caseResult = "fail";
                    background = failBackground;
                }
            }
            const element = document.getElementById(testCaseId);
            element.innerText = caseResult;
            if (background) {
                element.style.background = background;
            }
            document.getElementById(testCaseId + '-popover').innerText = 'Request Info:\r\n'
                + getHeaderStr(reqHeaders) + '\r\n' + reqData + '\r\nResponse Info:\r\n'
                + respHeaders + '\r\n' + respData
            //  todo info write to each row, add checkVal type
            return;
        }
        if (reqHeaders) {
            document.getElementById('req-headers').innerText = getHeaderStr(reqHeaders);
        }
        document.getElementById('resp-headers').innerText = respHeaders;
        document.getElementById('req-data').innerText = reqData;
        document.getElementById('resp-data').innerText = respData;
    } finally {
        sendResponse();
    }
});

window.onload = () => {
    let vpiPluginSign = document.getElementById('vpi-plugin-loaded');
    if (vpiPluginSign) {
        vpiPluginSign.innerHTML = 'loaded';
    }
};
