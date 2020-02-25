<template>
    <el-dialog id="selectProjectGroupDialog" :append-to-body="true" title="Move Group" :visible.sync="dialog.show"
               :close-on-click-modal="false" width="665px">
        <el-table :data="groups" row-key="id" border ref="select-project-group-dialog"
                  default-expand-all :tree-props="{children: 'childList'}"
                  highlight-current-row @current-change="selectRow"
                  :row-style="{cursor:'pointer'}">
            <el-table-column prop="name" label="name" width="464"
                             :row-style="{cursor:'pointer'}"/>
            <el-table-column label="updateTime" width="160" :formatter="(row)=>dateFormat(row.updateTime)"/>
        </el-table>
        <div slot="footer" style="text-align: left">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Move</el-button>
            <el-button @click="move2Top" type="warning" round>Move To Top</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'selectProjectGroupDialog',
        props: {
            dialog: {
                default() {
                    return {
                        show: false,
                        groupId: undefined,
                        projects: [],
                        selectedGroups: [],
                    };
                }
            }
        },
        data() {
            return {
                CONSTANT,
                groups: [],
                groupDict: {},
            }
        },
        methods: {
            selectRow(row) {
                let checkStack = Array(row);
                while (checkStack.length > 0) {
                    let item = checkStack.shift();
                    if (!item) {
                        continue;
                    }
                    if (this.dialog.selectedGroups.indexOf(item.id) > -1) {
                        this.dialog.groupId = undefined;
                        this.$refs['select-project-group-dialog'].setCurrentRow();
                        this.$message.error('not support the move to self/child');
                        return;
                    }
                    if (item.parentId && item.parentId !== '') {
                        checkStack.push(this.groupDict[item.parentId]);
                    }
                }
                if (row) {
                    this.dialog.groupId = row.id;
                }
            },
            findProjectGroups() {
                this.groups = [];
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_GROUP_FIND_LIST, {}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        let all = resp.data.data;
                        this.groupDict = {};
                        all.forEach(item => this.groupDict[item.id] = item);
                        for (let i = all.length - 1; i >= 0; i--) {
                            let item = all[i];
                            if (!item.parentId || item.parentId === '') {
                                this.groups.splice(0, 0, item);
                            } else if (this.groupDict[item.parentId].childList) {
                                this.groupDict[item.parentId].childList.splice(0, 0, item);
                            } else {
                                let parent = this.groupDict[item.parentId];
                                parent.childList = [item];
                            }
                        }
                    }
                });
            },
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            submitForm() {
                if (!this.dialog.groupId) {
                    this.$message.error('please select first');
                    return;
                }
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_GROUP_MOVE_GROUP, this.dialog).then(resp => {
                    UTILS.showResult(this, resp, () => {
                        this.$emit('flush');
                    });
                });
            },
            move2Top() {
                this.dialog.groupId = null;
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_GROUP_MOVE_GROUP, this.dialog).then(resp => {
                    UTILS.showResult(this, resp, () => {
                        this.$emit('flush');
                    });
                });
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
