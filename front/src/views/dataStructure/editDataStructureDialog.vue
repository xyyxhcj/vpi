<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" width="90%">
        <el-form :model="form" :rules="form_rules" label-width="100px" ref="form"
                 style="margin:10px 60px 10px 0;width:auto">
            <el-form-item label="name" label-width="100px" prop="name">
                <el-input v-model.trim="form.name"/>
            </el-form-item>
            <el-form-item label="remark" label-width="100px" prop="remark">
                <el-input v-model.trim="form.remark"/>
            </el-form-item>
        </el-form>
        <el-table :data="dataList" style="width: 100%" height="800px" :row-style="rowStyle">
            <el-table-column type="index" width="40"/>
            <el-table-column label="paramKey" width="280" ref="param-key-container">
                <template slot-scope="scope">
                    <span :style="{padding:countKeyPadding(scope.row)}">
                        <template v-if="scope.row.subList.length>0">
                            <i v-if="scope.row.showSub!==false" class="el-icon-remove-outline"
                               style="padding:10px 6px 10px 0" @click="hiddenSub(scope.row)"/>
                            <i v-if="scope.row.showSub===false" class="el-icon-circle-plus-outline"
                               style="padding:10px 6px 10px 0" @click="showSub(scope.row)"/>
                        </template>
                        <span style="border-left:1px solid #d9d9d9;padding:10px 2px" :key="index"
                              v-for="(item,index) in Array(scope.row.level)">
                            <i class="el-icon-arrow-right"/>
                        </span>
                    </span>
                    <el-input v-model.trim="scope.row.paramKey" @input="paramKeyChange(scope.$index,scope.row)"
                              :style="{width:countKeyInputWidth(scope.row)}"/>
                    <span v-if="scope.row.paramKeyIsEmpty" style="font-size: 12px;color: #F56C6C">enter paramKey</span>
                </template>
            </el-table-column>
            <el-table-column label="paramType" width="180">
                <template slot-scope="scope">
                    <el-select :value="scope.row.paramType+''" filterable
                               @change="(selectedValue)=>scope.row.paramType=selectedValue">
                        <el-option v-for="key in Object.keys(CONSTANT.PARAM_TYPE_STR)"
                                   :key="key" :label="CONSTANT.PARAM_TYPE_STR[key]" :value="key"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="requireType" width="180">
                <template slot-scope="scope">
                    <el-select :value="scope.row.requireType+''"
                               @change="(selectedValue)=>scope.row.requireType=selectedValue">
                        <el-option v-for="key in Object.keys(CONSTANT.REQUIRED_TYPE_STR)"
                                   :key="key" :label="CONSTANT.REQUIRED_TYPE_STR[key]" :value="key"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="paramDesc" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.paramDesc"/>
                </template>
            </el-table-column>
            <el-table-column label="value" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.value"/>
                </template>
            </el-table-column>
            <el-table-column label="operate">
                <template slot-scope="scope">
                    <el-button size="mini" @click="addSubField(scope.$index,scope.row)">Add Sub Field
                    </el-button>
                    <el-button size="mini" type="danger" @click="del(scope.$index,scope.row)">Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";

    export default {
        name: 'editDataStructureDialog',
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        url: ''
                    }
                }
            },
            form: Object,
        },
        data() {
            let itemTemplateStr = JSON.stringify({
                paramKey: '',
                paramType: CONSTANT.PARAM_TYPE.STRING,
                requireType: CONSTANT.REQUIRED_TYPE.REQUIRED,
                paramDesc: '',
                value: '',
                level: 0,
                subList: [],
                paramKeyIsEmpty: false,
                show: true,
            });
            let dataList = Array();
            let rootList = Array();
            for (let i = 0; i < CONSTANT.CONFIG.DEFAULT_DATA_LIST_SIZE; i++) {
                let item = JSON.parse(itemTemplateStr);
                dataList.push(item);
                rootList.push(item);
            }
            return {
                CONSTANT: CONSTANT,
                form_rules: {
                    name: [
                        {required: true, message: 'please enter name'}
                    ],
                },
                itemTemplateStr: itemTemplateStr,
                dataList: dataList,
                rootList: rootList,
            }
        },
        methods: {
            countKeyPadding(row) {
                return row.level > 0 && row.subList.length === 0 ? '0 0 0 20px' : '0';
            },
            countKeyInputWidth(row) {
                let preWidth = row.level > 0 || row.subList.length > 0 ? 20 : 0;
                for (let i = 0; i < row.level; i++) {
                    preWidth += 19;
                }
                return this.$refs['param-key-container'].width - 20 - preWidth + 'px';
            },
            addSubField(index, row) {
                let item = JSON.parse(this.itemTemplateStr);
                item.level = row.level + 1;
                item.parent = row;
                row.subList.splice(0, 0, item);
                this.dataList.splice(index + 1, 0, item);
            },
            del(index, row) {

            },
            paramKeyChange(index, row) {
                if (!row.parent && this.rootList[this.rootList.length - 1] === row) {
                    // add root
                    let item = JSON.parse(this.itemTemplateStr);
                    this.rootList.push(item);
                    this.dataList.splice(index + 1, 0, item);
                } else if (row.parent && row.parent.subList[row.parent.subList.length - 1] === row) {
                    // add subList
                    let item = JSON.parse(this.itemTemplateStr);
                    item.level = row.level;
                    item.parent = row.parent;
                    row.parent.subList.push(item);
                    this.dataList.splice(index + 1, 0, item);
                }
                if (row.paramKey === '' && (row.paramDesc !== '' || row.value !== '')) {
                    row.paramKeyIsEmpty = true;
                } else {
                    row.paramKeyIsEmpty = false;
                }
            },
            filterParams(params) {
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
            checkParamKey() {
                let paramKeyIsEmpty = false;
                this.dataList.forEach(item => {
                    if (item.paramKey === '' && (item.paramDesc !== '' || item.value !== '')) {
                        paramKeyIsEmpty = true;
                        item.paramKeyIsEmpty = true;
                    }
                });
                return paramKeyIsEmpty;
            },
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    let checkParam = this.checkParamKey();
                    if (!valid || checkParam) {
                        this.$message.error('params lose');
                        return;
                    }
                    console.log(this.rootList);
                    let copyRootList = JSON.parse(JSON.stringify(this.rootList, function (key, value) {
                        return key === 'parent' ? '' : value;
                    }));
                    this.filterParams(copyRootList);
                    console.log(copyRootList);
                    this.$axios.post(this.dialog.url, this.form).then(resp => {
                        UTILS.showResult(this, resp, function (obj) {
                            obj.$emit('flush');
                        });
                    });
                });
            },
            rowStyle({row}) {
                return {'display': row.show ? '' : 'none'};
            },
            hiddenSub(row) {
                row.showSub = false;
                let stack = row.subList.slice(0);
                while (stack.length > 0) {
                    let pop = stack.pop();
                    pop.show = false;
                    if (pop.subList && pop.subList.length > 0) {
                        pop.subList.forEach(item => stack.push(item));
                    }
                }
            },
            showSub(row) {
                row.showSub = true;
                let stack = row.subList.slice(0);
                while (stack.length > 0) {
                    let pop = stack.pop();
                    pop.show = true;
                    if (pop.showSub !== false && pop.subList && pop.subList.length > 0) {
                        pop.subList.forEach(item => stack.push(item));
                    }
                }
            },
        },
        created() {
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
