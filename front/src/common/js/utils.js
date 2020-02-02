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
    findPage: function (obj, url, func = function () {
    }) {
        obj.$axios.post(url, obj.query).then(resp => {
            if (this.checkResp(resp)) {
                const data = resp.data.data;
                obj.dataList = data.records;
                obj.query.page.total = data.total;
                if (data.total !== 0 && (obj.query.page.current - 1) * obj.query.page.size > data.total) {
                    obj.query.page.current = 1;
                    this.findPage(obj);
                }
                func(obj);
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
    fillShowDataList(sourceTree, destList) {
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
            destList.push(pop);
            if (pop.subList.length > 0) {
                for (let i = pop.subList.length - 1; i >= 0; i--) {
                    let item = pop.subList[i];
                    item.parent = pop;
                    stack.splice(0, 0, item);
                }
            }
        }
        let itemTemplateStr = JSON.stringify(CONSTANT.ITEM_TEMPLATE);
        let item = JSON.parse(itemTemplateStr);
        destList.push(item);
        sourceTree.push(item);
    },
    checkAuth: function (obj, permissionId) {
        return this.contains(obj.btnList, permissionId, function (btn, element) {
            return element === btn.menuId;
        })
    },
};
