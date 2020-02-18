<template>
    <div class="data-structure-common">
        <el-table :data="showList" style="width: 100%" :row-style="rowStyle" border :ref="config.refPre+'table'">
            <el-table-column type="index" width="40"/>
            <el-table-column type="selection" width="25" v-if="config.test"/>
            <el-table-column label="paramKey" width="280" ref="param-key-container">
                <template slot-scope="scope">
                    <span :style="{padding:countKeyPadding(scope.row)}">
                        <template v-if="scope.row.subList.length>0">
                            <i v-if="scope.row.showSub!==false" class="el-icon-remove-outline"
                               style="padding:5px 6px 5px 0" @click="hiddenSub(scope.row)"/>
                            <i v-if="scope.row.showSub===false" class="el-icon-circle-plus-outline"
                               style="padding:5px 6px 5px 0" @click="showSub(scope.row)"/>
                        </template>
                        <span style="border-left:1px solid #d9d9d9;padding:10px 2px" :key="index"
                              v-for="(item,index) in Array(scope.row.level)">
                            <i class="el-icon-arrow-right"/>
                        </span>
                    </span>
                    <el-input v-model.trim="scope.row.paramKey" @input="paramKeyChange(scope.$index,scope.row)"
                              :style="{width:countKeyInputWidth(scope.row)}" size="mini"
                              :ref="config.refPre+'paramKey'+scope.$index"
                              @keyup.down.native="moveDown(scope.$index,'paramKey')"
                              @keyup.up.native="moveUp(scope.$index,'paramKey')" v-if="!config.onlyRead"/>
                    <template v-else>{{scope.row.paramKey}}</template>
                    <span v-if="scope.row.paramKeyIsEmpty"
                          style="font-size: 12px;color: #F56C6C">enter paramKey</span>
                </template>
            </el-table-column>
            <el-table-column label="paramType" width="110">
                <template slot-scope="scope">
                    <el-select :value="scope.row.paramType+''" filterable size="mini"
                               @change="(selectedValue)=>scope.row.paramType=selectedValue"
                               v-if="!config.onlyRead">
                        <el-option v-for="key in Object.keys(CONSTANT.PARAM_TYPE_STR)"
                                   :key="key" :label="CONSTANT.PARAM_TYPE_STR[key]" :value="key"/>
                    </el-select>
                    <template v-else>{{CONSTANT.PARAM_TYPE_STR[scope.row.paramType]}}</template>
                </template>
            </el-table-column>
            <el-table-column label="requireType" width="125" v-if="!config.test">
                <template slot-scope="scope">
                    <el-select :value="scope.row.requireType+''" size="mini"
                               @change="(selectedValue)=>scope.row.requireType=selectedValue"
                               v-if="!config.onlyRead">
                        <el-option v-for="key in Object.keys(CONSTANT.REQUIRED_TYPE_STR)"
                                   :key="key" :label="CONSTANT.REQUIRED_TYPE_STR[key]" :value="key"/>
                    </el-select>
                    <template v-else>{{CONSTANT.REQUIRED_TYPE_STR[scope.row.requireType]}}</template>
                </template>
            </el-table-column>
            <el-table-column label="paramDesc" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.paramDesc" size="mini"
                              :ref="config.refPre+'paramDesc'+scope.$index"
                              @keyup.down.native="moveDown(scope.$index,'paramDesc')"
                              @keyup.up.native="moveUp(scope.$index,'paramDesc')"
                              v-if="!config.onlyRead&&!config.test"/>
                    <template v-else>{{scope.row.paramDesc}}</template>
                </template>
            </el-table-column>
            <el-table-column label="value" width="180">
                <template slot-scope="scope">
                    <el-input v-model.trim="scope.row.value" size="mini"
                              :ref="config.refPre+'value'+scope.$index"
                              @keyup.down.native="moveDown(scope.$index,'value')"
                              @keyup.up.native="moveUp(scope.$index,'value')"
                              v-if="!config.onlyRead"/>
                    <template v-else>{{scope.row.value}}</template>
                </template>
            </el-table-column>
            <el-table-column v-if="!config.onlyRead" min-width="250px">
                <template slot="header">
                    <more-operate :info="{showDataStructure:showDataStructure}"/>
                    <el-tooltip class="item" effect="dark" content="override" placement="left">
                        <el-button size="mini" type="success" @click="showImportJsonDialog">Import Json</el-button>
                    </el-tooltip>
                    <el-button size="mini" @click="addField">Add Field</el-button>
                </template>
                <template slot-scope="scope">
                    <more-operate :info="{index:scope.$index,row:scope.row,showDataStructure:showDataStructure}"/>
                    <el-tooltip class="item" effect="dark" content="additional" placement="left">
                        <el-button size="mini" type="success" @click="showImportJsonDialog(scope.$index,scope.row)">
                            Import Json
                        </el-button>
                    </el-tooltip>
                    <el-button size="mini" @click="addSubField(scope.$index,scope.row)">Add Sub Field</el-button>
                    <el-button size="mini" type="danger" @click="del(scope.$index,scope.row)">Delete</el-button>
                </template>
            </el-table-column>
        </el-table>
        <import-json-dialog :dialog="importJsonDialog" @flush="importJson"/>
        <select-data-structure-dialog :dialog="selectDataStructureDialog" @selectDataStructure="selectDataStructure"
                                      ref="selectDataStructureDialog"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import ImportJsonDialog from "./importJsonDialog";
    import {UTILS} from "../../common/js/utils";
    import MoreOperate from "./components/moreOperate";
    import SelectDataStructureDialog from "./selectDataStructureDialog";

    export default {
        name: 'dataStructure',
        components: {SelectDataStructureDialog, MoreOperate, ImportJsonDialog},
        props: {
            showList: {
                type: Array,
            },
            entity: {
                type: Object,
                default() {
                    return {
                        id: undefined,
                        reference: undefined,
                        // root tree
                        dataList: [],
                    };
                }
            },
            config: {
                type: Object,
                default() {
                    return {
                        refPre: '',
                        onlyRead: false,
                        test: false,
                    }
                }
            }
        },
        data() {
            return {
                CONSTANT,
                importJsonDialog: {
                    show: false,
                    title: 'import json',
                    parentLevel: -1,
                    row: undefined,
                    rowIndex: undefined,
                },
                selectDataStructureDialog: {
                    show: false,
                    title: 'select data structure',
                    selectedIndex: undefined,
                    selectedRow: undefined,
                }
            }
        },
        methods: {
            signSelected() {
                let selectedList = this.$refs[this.config.refPre + 'table'].selection;
                this.showList.forEach(row => {
                    row.selected = selectedList.indexOf(row) > -1;
                });
            },
            importJson(data) {
                if (!data) {
                    return;
                }
                this.showList.splice(0);
                if (this.importJsonDialog.row === undefined) {
                    this.entity.dataList.splice(0);
                    data.forEach(item => this.entity.dataList.push(item));
                } else {
                    if (this.importJsonDialog.row.subList !== undefined) {
                        let dict = {};
                        for (let i = 0; i < this.importJsonDialog.row.subList.length; i++) {
                            dict[this.importJsonDialog.row.subList[i].paramKey] = i;
                        }
                        data.forEach(item => {
                            let index = dict[item.paramKey];
                            if (index !== undefined) {
                                this.importJsonDialog.row.subList[index] = item;
                            } else {
                                this.importJsonDialog.row.subList.push(item);
                            }
                        });
                    } else {
                        this.importJsonDialog.row.subList = [];
                    }
                }
                UTILS.fillShowList(this.entity.dataList, this.showList);
                this.$refs[this.config.refPre + 'table'].toggleAllSelection();
            },
            showImportJsonDialog(index, row) {
                this.importJsonDialog.row = row;
                if (row !== undefined) {
                    this.importJsonDialog.rowIndex = index;
                    this.importJsonDialog.parentLevel = row.level;
                } else {
                    this.importJsonDialog.rowIndex = undefined;
                    this.importJsonDialog.parentLevel = -1;
                }
                this.importJsonDialog.show = true;
            },
            moveUp(index, refMid) {
                if (index !== 0) {
                    this.$refs[this.config.refPre + refMid + (index - 1)].focus();
                }
            },
            moveDown(index, refMid) {
                if (index !== this.showList.length - 1) {
                    this.$refs[this.config.refPre + refMid + (index + 1)].focus();
                }
            },
            rowStyle({row}) {
                return {'display': row.show ? '' : 'none'};
            },
            countKeyPadding(row) {
                return row.level > 0 && row.subList.length === 0 ? '0 0 0 20px' : '0';
            },
            hiddenSub(row) {
                this.$set(row, 'showSub', false);
                let stack = row.subList.slice();
                while (stack.length > 0) {
                    let pop = stack.shift();
                    pop.show = false;
                    if (pop.subList && pop.subList.length > 0) {
                        pop.subList.forEach(item => stack.push(item));
                    }
                }
            },
            showSub(row) {
                row.showSub = true;
                let stack = row.subList.slice();
                while (stack.length > 0) {
                    let pop = stack.shift();
                    pop.show = true;
                    if (pop.showSub !== false && pop.subList && pop.subList.length > 0) {
                        pop.subList.forEach(item => stack.push(item));
                    }
                }
            },
            paramKeyChange(index, row) {
                if (!row.parent && this.entity.dataList[this.entity.dataList.length - 1] === row) {
                    // add root
                    this.addField();
                }
                row.paramKeyIsEmpty = row.paramKey === '' && (row.paramDesc !== '' || row.value !== '');
            },
            countKeyInputWidth(row) {
                let preWidth = row.level > 0 || row.subList.length > 0 ? 20 : 0;
                for (let i = 0; i < row.level; i++) {
                    preWidth += 19;
                }
                return this.$refs['param-key-container'].width - 20 - preWidth + 'px';
            },
            addField() {
                UTILS.pushItemTemplate(this.entity.dataList, this.showList);
            },
            addSubField(index, row) {
                let item = JSON.parse(CONSTANT.ITEM_TEMPLATE);
                item.level = row.level + 1;
                item.parent = row;
                row.subList.splice(0, 0, item);
                this.showList.splice(index + 1, 0, item);
            },
            del(index, row) {
                let parent = row.parent;
                // remove from showList,with subTree
                // get same level next element
                if (parent) {
                    let curr = row;
                    while (parent.parent && parent.subList.indexOf(curr) === parent.subList.length - 1) {
                        curr = parent;
                        parent = parent.parent;
                    }
                    if (parent.subList.indexOf(curr) !== parent.subList.length - 1) {
                        let nextIndex = this.showList.indexOf(parent.subList[parent.subList.indexOf(curr) + 1]);
                        this.showList.splice(index, nextIndex - index);
                    } else {
                        let rootIndex = this.entity.dataList.indexOf(parent);
                        if (rootIndex === this.entity.dataList.length - 1) {
                            this.showList.splice(index, this.showList.length - index);
                        } else {
                            let nextIndex = this.showList.indexOf(this.entity.dataList[rootIndex + 1]);
                            this.showList.splice(index, nextIndex - index);
                        }

                    }
                } else {
                    let rootIndex = this.entity.dataList.indexOf(row);
                    if (rootIndex === this.entity.dataList.length - 1) {
                        this.showList.splice(index, this.showList.length - index);
                    } else {
                        let nextIndex = this.showList.indexOf(this.entity.dataList[rootIndex + 1]);
                        this.showList.splice(index, nextIndex - index);
                    }
                }
                // remove from tree
                if (parent) {
                    parent.subList.splice(parent.subList.indexOf(row), 1);
                } else {
                    this.entity.dataList.splice(this.entity.dataList.indexOf(row), 1);
                }
            },
            init() {
                if (this.showList.length === 0) {
                    for (let i = 0; i < CONSTANT.CONFIG.DEFAULT_DATA_LIST_SIZE; i++) {
                        UTILS.pushItemTemplate(this.entity.dataList, this.showList);
                    }
                }
                let $refTable = this.$refs[this.config.refPre + 'table'];
                this.showList.forEach(row => {
                    if (row.paramKey !== '' && row.requireType === 0) {
                        $refTable.toggleRowSelection(row);
                    }
                });
            },
            showDataStructure(index, row) {
                this.selectDataStructureDialog.selectedIndex = index;
                this.selectDataStructureDialog.selectedRow = row;
                this.$refs['selectDataStructureDialog'].findPage();
                this.selectDataStructureDialog.show = true;
            },
            selectDataStructure(selectedDataStructure) {
                this.$axios.post(CONSTANT.REQUEST_URL.STRUCTURE_FIND_DETAIL, {id: selectedDataStructure.id}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        if (this.selectDataStructureDialog.selectedRow){
                            // add to sub
                            this.selectDataStructureDialog.selectedRow.referenceStructureId = selectedDataStructure.id;
                            this.selectDataStructureDialog.selectedRow.subList = resp.data.data.dataList;
                        } else {
                            // override all
                            this.entity.dataList = resp.data.data.dataList;
                            this.entity.id = selectedDataStructure.id;
                            this.entity.reference = true;
                        }
                    }
                    this.showList.splice(0);
                    UTILS.fillShowList(this.entity.dataList, this.showList);
                    this.$refs[this.config.refPre + 'table'].toggleAllSelection();
                });
            }
        }
        ,
        created() {
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .data-structure-common
        .el-table td, .el-table th
            padding 0 0 0 3px

        .cell, div.cell
            padding 0 0 0 3px
            height 28px

        .el-button
            padding 7px 7px

        .more-operate
            padding 10px
            cursor: pointer;
            color: #409EFF;
</style>
