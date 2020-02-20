import CryptoJS from 'crypto-js/crypto-js';
import {CONSTANT} from "./constant";

/**
 * concat date
 */
function concatZero(str) {
    return ('00' + str).substr(str.length);
}

export const UTILS = {
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
            data = this.loadFromLocal(localId, localKey, []);
        } else {
            // save to local
            this.saveToLocal(localId, localKey, data);
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
    // aes
    aesEncrypt: function (data, key) {
        key = CryptoJS.enc.Utf8.parse(key);
        let str = CryptoJS.enc.Utf8.parse(data);
        let encrypted = CryptoJS.AES.encrypt(str, key, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();
    },
    aesDecrypt: function (data, key) {
        key = CryptoJS.enc.Utf8.parse(key);
        let decryptedData = CryptoJS.AES.decrypt(data, key, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return decryptedData.toString(CryptoJS.enc.Utf8);
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
    formatJson(json, options) {
        let reg = null,
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
    },
    isJSON: function (str) {
        if (typeof str == 'string') {
            try {
                let obj = JSON.parse(str);
                return !!(typeof obj == 'object' && obj);
            } catch (e) {
                return false;
            }
        }
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
    }, closeDialog = true) {
        if (this.checkResp(resp)) {
            func(obj);
            if (closeDialog) {
                obj.dialog.show = false;
            }
        } else {
            this.errorMsg(obj, resp);
        }
    },
    findPage: function ($this, dataObj, url, func = function () {
    }) {
        $this.$axios.post(url, dataObj.query).then(resp => {
            if (this.checkResp(resp)) {
                const data = resp.data.data;
                dataObj.dataList = data.records;
                dataObj.query.page.total = data.total;
                if (data.total !== 0 && (dataObj.query.page.current - 1) * dataObj.query.page.size > data.total) {
                    dataObj.query.page.current = 1;
                    this.findPage($this, dataObj);
                }
                func($this, dataObj);
            }
        });
    },
    findAll: function (obj) {
        obj.$axios.post(obj.dialog.url, {}).then(resp => {
            if (this.checkResp(resp)) {
                obj.data = resp.data.data;
            } else {
                obj.$message({
                    message: resp.data ? resp.data.message : 'connect fail',
                    type: 'error'
                });
            }
        });
    },
    filterEmptyParams(params) {
        let needFilters = Array();
        for (let i = params.length - 1; i >= 0; i--) {
            let item = params[i];
            if (item.paramKey === '') {
                params.splice(i, 1);
            } else if (item.subList.length > 0) {
                needFilters.push(item.subList);
            }
        }
        while (needFilters.length > 0) {
            let subList = needFilters.pop();
            for (let i = subList.length - 1; i >= 0; i--) {
                let item = subList[i];
                if (item.paramKey === '') {
                    subList.splice(i, 1);
                } else if (item.subList.length > 0) {
                    needFilters.push(item.subList);
                }
            }
        }
    },
    pushItemTemplate: function (treeList, dataList) {
        let item = JSON.parse(CONSTANT.ITEM_TEMPLATE);
        treeList.push(item);
        dataList.push(item);
    },
    fillShowList(sourceTree, destList, allReference = false, addTemplate = true) {
        destList.splice(0);
        let stack = sourceTree.slice();
        while (stack.length > 0) {
            let pop = stack.shift();
            pop.paramKeyIsEmpty = false;
            pop.show = true;
            if (!pop.parent) {
                pop.level = 0;
            } else {
                pop.level = pop.parent.level + 1;
            }
            // sign reference
            if (allReference || (pop.parent &&
                (pop.parent.reference ||
                    (pop.parent.referenceStructureId && pop.parent.referenceStructureId !== '')))) {
                // parent is reference
                pop.reference = true;
            }
            destList.push(pop);
            if (pop.subList.length > 0) {
                for (let i = pop.subList.length - 1; i >= 0; i--) {
                    let item = pop.subList[i];
                    item.parent = pop;
                    stack.splice(0, 0, item);
                }
            }
        }
        if (addTemplate) {
            this.pushItemTemplate(sourceTree, destList);
        }
    },
    checkAuth(obj, permissionId) {
        return this.contains(obj.btnList, permissionId, function (btn, element) {
            return element === btn.menuId;
        })
    },
    // json transform to view
    pushItem(list, obj, stack, level = 0) {
        Object.keys(obj).forEach(key => {
            let itemTemp = JSON.parse(CONSTANT.ITEM_TEMPLATE);
            itemTemp.paramKey = key;
            itemTemp.paramDesc = itemTemp.value = obj[key];
            itemTemp.level = level;
            if (constructor) {
                if (itemTemp.value !== undefined && itemTemp.value !== null) {
                    switch (itemTemp.value.constructor) {
                        case String:
                            itemTemp.paramType = CONSTANT.PARAM_TYPE.STRING;
                            break;
                        case Number:
                            itemTemp.paramType = CONSTANT.PARAM_TYPE.NUMBER;
                            break;
                        case Object:
                            itemTemp.paramType = CONSTANT.PARAM_TYPE.OBJECT;
                            if (stack) {
                                stack.push({
                                    list: itemTemp.subList,
                                    obj: itemTemp.value,
                                    level: itemTemp.level
                                });
                                itemTemp.paramDesc = itemTemp.value = JSON.stringify(itemTemp.value);
                            }
                            break;
                        case Array:
                            itemTemp.paramType = CONSTANT.PARAM_TYPE.ARRAY;
                            if (itemTemp.value.length > 0 && stack) {
                                stack.push({
                                    list: itemTemp.subList,
                                    obj: itemTemp.value[0],
                                    level: itemTemp.level
                                });
                                itemTemp.paramDesc = itemTemp.value = JSON.stringify(itemTemp.value);
                            }
                            break;
                        case Date:
                            itemTemp.paramType = CONSTANT.PARAM_TYPE.TIME;
                            break;
                        default:
                    }
                }
            }
            list.push(itemTemp);
        });
    },
    // json transform to viewData
    json2ViewData(jsonObj, parentLevel = -1) {
        let result = [];
        let stack = [];
        this.pushItem(result, jsonObj, stack, parentLevel + 1);
        while (stack.length > 0) {
            let {list, obj, level} = stack.shift();
            this.pushItem(list, obj, stack, level + 1);
        }
        return result;
    },
};
