<template>
    <div class="headers-common">
        <el-table :data="dataList" style="width: 100%">
            <el-table-column type="index" width="40"/>
            <el-table-column label="name" width="280" ref="param-key-container">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.name" @input="nameChange(scope.$index,scope.row)"
                              size="mini"/>
                    <span v-if="scope.row.nameIsEmpty"
                          style="font-size: 12px;color: #F56C6C">enter name</span>
                </template>
            </el-table-column>
            <el-table-column label="requireType" width="125">
                <template slot-scope="scope">
                    <el-select :value="scope.row.requireType+''" size="mini"
                               @change="(selectedValue)=>scope.row.requireType=selectedValue">
                        <el-option v-for="key in Object.keys(CONSTANT.REQUIRED_TYPE_STR)"
                                   :key="key" :label="CONSTANT.REQUIRED_TYPE_STR[key]" :value="key"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="desc" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.desc" size="mini"/>
                </template>
            </el-table-column>
            <el-table-column label="value" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.value" size="mini"/>
                </template>
            </el-table-column>
            <el-table-column label="operate">
                <template slot-scope="scope">
                    <el-button size="mini" type="danger" @click="del(scope.$index,scope.row)">Delete</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../../common/js/constant";

    export default {
        name: 'apiHeaders',
        props: {
            dataList: {
                type: Array,
            },
        },
        data() {
            return {
                CONSTANT,
                itemTemplateStr: JSON.stringify({
                    name: '', value: '', desc: '', requireType: 0, nameIsEmpty: false
                }),
            }
        },
        methods: {
            nameChange(index, row) {
                if (this.dataList[this.dataList.length - 1] === row) {
                    // add
                    let item = JSON.parse(this.itemTemplateStr);
                    this.dataList.push(item);
                }
                row.nameIsEmpty = row.name === '' && (row.desc !== '' || row.value !== '');
            },
            del(index) {
                if (this.dataList.length > 1) {
                    this.dataList.splice(index, 1);
                }
            },
            init() {
                if (this.dataList.length === 0) {
                    for (let i = 0; i < CONSTANT.CONFIG.DEFAULT_DATA_LIST_SIZE; i++) {
                        let item = JSON.parse(this.itemTemplateStr);
                        this.dataList.push(item);
                    }
                }
            }
        },
        created() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .headers-common
        .el-table td, .el-table th
            padding 0

        .cell
            padding 0
</style>
