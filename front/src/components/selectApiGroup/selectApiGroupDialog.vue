<template>
    <el-dialog id="selectApiStatusDialog" :append-to-body="true" title="Move Group" :visible.sync="dialog.show"
               :close-on-click-modal="false" width="665px" destroy-on-close>
        <el-table :data="groups" row-key="id" border
                  default-expand-all :tree-props="{children: 'childList'}"
                  highlight-current-row @current-change="selectRow"
                  :row-style="{cursor:'pointer'}">
            <el-table-column prop="name" label="name" width="464"
                             :row-style="{cursor:'pointer'}"/>
            <el-table-column label="updateTime" width="160" :formatter="(row)=>dateFormat(row.updateTime)"/>
        </el-table>
        <div slot="footer" style="text-align: left">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "../../common/js/constant";
import {UTILS} from "../../common/js/utils";

export default {
        name: 'selectApiGroupDialog',
        props: {
            dialog: {
                default() {
                    return {
                        show: false,
                        ids: [],
                        groupId: undefined,
                        projectId: '',
                    };
                }
            }
        },
        data() {
            return {
                CONSTANT,
                groups: [],
            }
        },
        methods: {
            selectRow(row) {
                this.dialog.groupId = row.id;
            },
            findApiGroups() {
                this.groups = [];
                this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_FIND_LIST, {projectId: this.dialog.projectId}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        let all = resp.data.data;
                        let dict = {};
                        all.forEach(item => dict[item.id] = item);
                        for (let i = all.length - 1; i >= 0; i--) {
                            let item = all[i];
                            if (!item.parentId || item.parentId === '') {
                                this.groups.splice(0, 0, item);
                            } else if (dict[item.parentId].childList) {
                                dict[item.parentId].childList.splice(0, 0, item);
                            } else {
                                let parent = dict[item.parentId];
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
                this.$axios.post(CONSTANT.REQUEST_URL.API_MOVE_GROUP, this.dialog).then(resp => {
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
