import {Message} from 'element-ui';
import {CONSTANT} from "@/common/js/constant";
import store from '@/store';
import Vue from 'vue';

export const PLUGIN_UTILS = {
    checkValid() {
        return new Promise((resolve, reject) => {
            let vpiPluginSign = document.getElementById('vpi-plugin-loaded');
            if (!vpiPluginSign || vpiPluginSign.innerHTML === '') {
                Message.error('please install vpi plugin');
                reject();
            } else {
                resolve();
            }
        });
    },
    setContentType(headers, apiRequestType, requestParamType) {
        if (apiRequestType !== 1) {
            // Ignore Get
            let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[requestParamType];
            headers[contentTypeName] = contentTypeValue;
        }
    },
    checkURL(url) {
        //url= 协议://(ftp的登录信息)[IP|域名](:端口号)(/或?请求参数)
        let strRegex = '^((https|http|ftp)://)?'//(https或http或ftp):// 可有可无
            + '(([\\w_!~*\'()\\.&=+$%-]+: )?[\\w_!~*\'()\\.&=+$%-]+@)?' //ftp的user@  可有可无
            + '(([0-9]{1,3}\\.){3}[0-9]{1,3}' // IP形式的URL- 3位数字.3位数字.3位数字.3位数字
            + '|' // 允许IP和DOMAIN（域名）
            + '(localhost)|'	//匹配localhost
            + '([\\w_!~*\'()-]+\\.)*' // 域名- 至少一个[英文或数字_!~*\'()-]加上.
            + '\\w+\\.' // 一级域名 -英文或数字  加上.
            + '[a-zA-Z]{1,6})' // 顶级域名- 1-6位英文
            + '(:[0-9]{1,5})?' // 端口- :80 ,1-5位数字
            + '((/?)|' // url无参数结尾 - 斜杆或这没有
            + '(/[\\w_!~*\'()\\.;?:@&=+$,%#-]+)+/?)$';//请求参数结尾- 英文或数字和[]内的各种字符

        let re = new RegExp(strRegex, 'i');//i不区分大小写
        //将url做uri转码后再匹配，解除请求参数中的中文和空字符影响
        return re.test(encodeURI(url));
    },
    setLoading(obj, fieldName) {
        Vue.set(obj, fieldName, true);
        setTimeout(() => obj[fieldName] = false, 1000);
    },
    send2Plugin(selectedEnv, api, headers, params, testCaseInfo = undefined) {
        let url = selectedEnv && selectedEnv.frontUri ? (selectedEnv.frontUri + api.apiUri)
            : api.apiUri;
        if (url.startsWith('/')) {
            Message.closeAll();
            Message.error('url error：' + url);
            return;
        } else if (!this.checkURL(url)) {
            Message.closeAll();
            Message.error('url error：' + url);
            return;
        } else if (!url.startsWith('http')) {
            url = 'http://' + url;
        }
        let method = CONSTANT.REQUEST_TYPE[api.apiRequestType];
        PLUGIN_UTILS.setContentType(headers, api.apiRequestType, api.requestParamType);
        if (api.apiRequestType === 1) {
            // use Get
            let paramStr = '';
            Object.keys(params).forEach(key => paramStr += (key + '=' + params[key] + '&'));
            let index = url.indexOf('?');
            if (index !== -1) {
                url = url.substring(0, index);
            }
            url += '?' + paramStr;
            params = paramStr;
        }
        let logHeaders = {};
        logHeaders[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH] = store.getters.loginAuth;
        logHeaders[CONSTANT.CONTENT_TYPE[0][0]] = CONSTANT.CONTENT_TYPE[0][1];
        window.postMessage({
            url: url,
            requestParamType: api.requestParamType,
            headers: headers,
            method: method,
            params: params,
            logUrl: CONSTANT.CONFIG.HOST + CONSTANT.REQUEST_URL.API_TEST_HISTORY_ADD,
            logHeaders: logHeaders,
            apiId: api.id,
            testCaseInfo: testCaseInfo,
        }, '*');
    },
}
