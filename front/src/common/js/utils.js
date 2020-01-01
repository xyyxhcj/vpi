import {CONSTANT} from "./constant";

/**
 * concat date
 */
function concatZero(str) {
    return ('00' + str).substr(str.length);
}

export const utils = {
    /**
     * get url param
     */
    urlParse: function () {
        let url = window.location.search;
        let obj = {};
        let reg = /[?&]([^?&]+)=([^?&]+)/g;
        while (reg.test(url)) {
            obj[decodeURIComponent(RegExp.$1)] = decodeURIComponent(RegExp.$2);
        }
        return obj;
    },

    /**
     * save data,need open cookie
     */
    saveToLocal: function (id, key, value) {
        // localStorage only save String
        let dataList = window.localStorage.__dataList__;
        if (!dataList) {
            dataList = {};
            dataList[id] = {};
        } else {
            dataList = JSON.parse(dataList);
            if (!dataList[id]) {
                dataList[id] = {};
            }
        }
        dataList[id][key] = value;
        window.localStorage.__dataList__ = JSON.stringify(dataList);
    },
    /**
     * read data
     */
    loadFromLocal: function (id, key, defaultValue = null) {
        let dataList = window.localStorage.__dataList__;
        if (!dataList) {
            return defaultValue;
        }
        dataList = JSON.parse(dataList);
        if (!dataList[id] || !dataList[id][key]) {
            return defaultValue;
        }
        return dataList[id][key];
    },
    /**
     * check array contain
     */
    contains: function (arr, element, comparator) {
        if (arr) {
            for (let i = 0; i < arr.length; i++) {
                if (comparator(arr[i], element)) {
                    return arr[i];
                }
            }
        }
        return false;
    },
    formatDate: function (date, fmt) {
        if (!date) {
            return '';
        }
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
        }
        let o = {
            'M+': date.getMonth() + 1,
            'd+': date.getDate(),
            'h+': date.getHours(),
            'm+': date.getMinutes(),
            's+': date.getSeconds(),
            'q+': Math.floor((date.getMonth() + 3) / 3),
            'S': date.getMilliseconds()
        };
        for (let k in o) {
            if (new RegExp(`(${k})`).test(fmt)) {
                let str = o[k] + '';
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : concatZero(str));
            }
        }
        return fmt;
    },
    // {key} -> {value}
    formatStr: function (str, dict) {
        if (str.length > 0) {
            Object.keys(dict).forEach(function (key) {
                if (dict[key] !== undefined) {
                    let reg = new RegExp("({" + key + "})", "g");
                    str = str.replace(reg, dict[key]);
                }
            });
        }
        return str;
    },
    syncRouterData: function (localId, localKey, data) {
        if (typeof (data) === 'undefined') {
            // get form local
            data = utils.loadFromLocal(localId, localKey, []);
        } else {
            // save to local
            utils.saveToLocal(localId, localKey, data);
        }
        return data;
    },
    // generate uuid
    uuid: function (len, radix) {
        let chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        let uuid = [], i;
        radix = radix || chars.length;
        if (len) {
            for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
        } else {
            let r;
            // rfc4122 requires these characters
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            // Fill in random data.  At i==19 set the high bits of clock sequence as
            // per rfc4122, sec. 4.1.5
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i === 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    },
    transformRequest: function (params) {
        let data = '';
        for (let key in params) {
            data += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&';
        }
        return data;
    },
    copyProperty: function (src = {}, fields = []) {
        let desc;
        if (fields.length === 0) {
            desc = JSON.parse(JSON.stringify(src));
        } else {
            desc = JSON.parse(JSON.stringify(src, fields));
        }
        return desc;
    },
    /* project common function */
    errorMsg: function (obj, resp) {
        obj.$message({
            message: resp.data ? resp.data.message : 'network error',
            type: 'error'
        })
    },
    checkResp: function (resp) {
        if (!resp.data) {
            return false;
        }
        return CONSTANT.RESULT_CODE.SUCCESS === resp.data.code;
    },
    showResult: function (obj, resp, func = function () {
    }) {
        if (this.checkResp(resp)) {
            obj.$message({
                message: 'operate success',
                type: 'success'
            });
            func(obj);
            obj.dialog.show = false;
        } else {
            this.errorMsg(obj, resp);
        }
    },
    findPage: function (obj) {
        obj.$axios.post(obj.findPerm.url, obj.searchData).then(resp => {
            if (this.checkResp(resp)) {
                const data = resp.data.data;
                obj.dataList = data.records;
                obj.searchData.page.total = data.total;
                if (data.total !== 0 && (obj.searchData.page.current - 1) * obj.searchData.page.size > data.total) {
                    obj.searchData.page.current = 1;
                    this.findPage(obj);
                }
            }
        });
    },
    findAll: function (obj) {
        obj.$axios.post(obj.dialog.url, {}).then(resp => {
            if (this.checkResp(resp)) {
                obj.data = resp.data.data;
            } else {
                obj.$message({
                    message: resp.data ? resp.data.message : '网络错误',
                    type: 'error'
                });
            }
        });
    },
    checkAuth: function (obj, permissionId) {
        return this.contains(obj.btnList, permissionId, function (btn, element) {
            if (element === btn.menuId) {
                return true;
            } else {
                return false;
            }
        })
    },
};
