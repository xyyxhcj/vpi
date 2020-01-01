import axios from 'axios';
import {Loading, Message} from 'element-ui';
import {CONSTANT} from "./constant";
import {utils} from "./utils";

let loading;

function startLoading(url) {
    if (!utils.contains(CONSTANT.CLOSE_LOADING_URL, url, function (url1, url2) {
        if (url2.endsWith(url1)) {
            return true;
        } else {
            return false;
        }
    })) {
        loading = Loading.service({
            lock: true,
            text: 'Loading...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
    }
}

function endLoading(url) {
    if (!utils.contains(CONSTANT.CLOSE_LOADING_URL, url, function (url1, url2) {
        if (url2.endsWith(url1)) {
            return true;
        } else {
            return false;
        }
    })) {
        loading.close();
    }
}

axios.interceptors.request.use(config => {
    // default Content-Type
    if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json; charset=UTF-8';
    }
    startLoading(config.url);
    if (localStorage[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH]) {
        config.headers[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH] = localStorage[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH];
    }
    return config;
}, error => {
    return Promise.reject(error);
});

axios.interceptors.response.use(response => {
    endLoading(response.config.url);
    // clear token
    if (response.data) {
        if (CONSTANT.RESULT_CODE.USER_INVALID === response.data.code || CONSTANT.RESULT_CODE.LOGIN_NOT === response.data.code) {
            Message.error(response.data.message);
            localStorage.removeItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH);
        }
    }
    return response;
}, error => {
    console.info(error);
    endLoading(error.response.config.url);
    // Message.error(error.response.data);
    return Promise.reject(error);
});
export default axios;
