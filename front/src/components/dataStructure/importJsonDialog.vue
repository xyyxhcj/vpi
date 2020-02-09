<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="60%">
        <el-input type="textarea" :rows="15" placeholder="please input json data" v-model="jsonData">
        </el-input>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="confirm" type="primary" round>Confirm</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";

    export default {
        name: 'importJsonDialog',
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        parentLevel: -1,
                    }
                }
            },
        },
        data() {
            return {
                jsonData: ''
            }
        },
        methods: {
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
            confirm() {
                // check json
                if (!UTILS.isJSON(this.jsonData)) {
                    this.$message.error('please input json data');
                    return;
                }
                // format data
                let jsonObj = JSON.parse(this.jsonData);
                let result = [];
                let stack = [];
                this.pushItem(result, jsonObj, stack, this.dialog.parentLevel + 1);
                while (stack.length > 0) {
                    let {list, obj, level} = stack.shift();
                    this.pushItem(list, obj, stack, level + 1);
                }
                this.$emit('flush', result);
                this.dialog.show = false;
                this.jsonData = '';
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
