<template>
    <div id="api-doc-container" style="min-width:1250px">
        <el-container>
            <el-aside width="150px" style="font-size:5px">
                <div class="api-group-header">
                    <el-button type="primary" icon="el-icon-plus" size="mini" @click="addSubGroup(null)">group
                    </el-button>
                </div>
                <div class="select-all">all</div>
                <el-tree
                        :data="groups"
                        :props="{label:'name',children:'childList'}"
                        node-key="id"
                        default-expand-all
                        draggable>
                    <span class="api-group-node" slot-scope="{node,data}">
                        <span style="float:left;padding-left: 1px">
                            <template v-if="node.label.length>15-data.getLevel(data)*3">
                                <el-popover popper-class="api-doc-popover" placement="top" width="90" trigger="hover">
                                    <span>{{node.label}}</span>
                                    <span slot="reference">
                                        {{ node.label.substr(0,15-data.getLevel(data)*3)+'...' }}
                                    </span>
                                </el-popover>
                            </template>
                            <template v-if="node.label.length<=15-data.getLevel(data)*3">
                                {{node.label}}
                            </template>
                        </span>
                        <el-dropdown @command="groupCommand" style="float:right;padding-right: 5px">
                        <span class="el-dropdown-link">
                            <i class="el-icon-arrow-down el-icon-more"/>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item :command="()=>addSubGroup(data)">add sub group</el-dropdown-item>
                            <el-dropdown-item :command="()=>editSubGroup(data)">edit</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    </span>
                </el-tree>
            </el-aside>
            <el-main>Main</el-main>
        </el-container>
        <edit-api-group-dialog :dialog="editApiGroupDialog" :form="editApiGroupForm" @flush="findApiGroups"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import EditApiGroupDialog from "./editApiGroupDialog";

    export default {
        name: 'index',
        components: {EditApiGroupDialog},
        data() {
            return {
                projectId: this.$store.getters.selectedProjectId,
                groups: [],
                editApiGroupDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                editApiGroupForm: {},
            }
        },
        methods: {
            selectGroup(apiGroupId, path) {
                console.log(apiGroupId);
            },
            groupCommand(func) {
                func();
            },
            addSubGroup(data) {
                this.editApiGroupForm = {
                    name: '',
                    projectId: this.projectId,
                };
                if (data) {
                    this.editApiGroupForm.parentId = data.id;
                }
                this.editApiGroupDialog = {
                    show: true,
                    title: 'Add Api Group',
                    url: CONSTANT.REQUEST_URL.API_GROUP_ADD,
                };
            },
            editSubGroup(data) {
                console.log(data.getLevel(data));
                this.editApiGroupForm = JSON.parse(JSON.stringify(data));
                this.editApiGroupDialog = {
                    show: true,
                    title: 'Edit Api Group',
                    url: CONSTANT.REQUEST_URL.API_GROUP_EDIT,
                };
            },
            findApiGroups() {
                this.groups = [];
                this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_FIND_LIST, {projectId: this.projectId}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        let all = resp.data.data;
                        let dict = {};
                        all.forEach(item => dict[item.id] = item);
                        for (let i = all.length - 1; i >= 0; i--) {
                            let item = all[i];
                            item.getLevel =
                                (item) => item.parentId ? dict[item.parentId].getLevel(dict[item.parentId]) + 1 : 0;
                            if (item.parentId === null) {
                                this.groups.splice(0, 0, item);
                            } else if (dict[item.parentId].childList) {
                                dict[item.parentId].childList.splice(0, 0, item);
                            } else {
                                dict[item.parentId].childList = [item];
                            }
                        }
                    }
                });
            },
        },
        created() {
            this.findApiGroups();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #api-doc-container
        border 1px solid #d9d9d9

        .el-aside
            background-color #D3DCE6
            color #333
            text-align center
            line-height 26px

            .api-group-node
                width 100%

        .api-group-header
            text-align left
            padding 5px
            background #FFF

        .el-tree-node__expand-icon
            padding 1px
        .el-main
            padding 5px
</style>
