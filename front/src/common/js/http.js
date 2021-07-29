import axios from 'axios';
import {Loading} from 'element-ui';
import {CONSTANT} from "./constant";
import {UTILS} from "./utils";

let loading;

function startLoading(url) {
    if (!UTILS.contains(CONSTANT.CLOSE_LOADING_URL, url, (url1, url2) => {
        return url2.endsWith(url1);
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
    if (!UTILS.contains(CONSTANT.CLOSE_LOADING_URL, url, (url1, url2) => {
        return url2.endsWith(url1);
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
    if (response.data && localStorage.getItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH)) {
        if (CONSTANT.RESULT_CODE.USER_INVALID === response.data.code || CONSTANT.RESULT_CODE.LOGIN_NOT === response.data.code) {
            localStorage.removeItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH);
            location.reload();
        }
    }
    return response;
}, error => {
    endLoading(error.response.config.url);
    return Promise.reject(error);
});
export default axios;
