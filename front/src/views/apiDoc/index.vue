<template>
    <div id="api-doc-container" style="min-width:1250px">
        <el-container>
            <el-aside width="150px" style="font-size:6px">
                <div class="api-group-header">
                    <el-button type="primary" icon="el-icon-plus" size="mini" @click="addSubGroup(null)"
                               v-if="hasAuth">
                        group
                    </el-button>
                </div>
                <div class="select-all" @click="selectGroup({id:''})">all</div>
                <el-tree :data="groups" :props="{label:'name',children:'childList'}"
                         node-key="id" default-expand-all @node-click="selectGroup"
                         draggable @node-drop="moveNode" highlight-current>
                    <span class="api-group-node" slot-scope="{node,data}">
                        <span style="float:left;padding-left: 1px">
                            <template v-if="node.label.length>14-data.getLevel(data)*3">
                                <el-popover popper-class="api-doc-popover" placement="top-end" :close-delay="0"
                                            trigger="hover">
                                    <span style="padding:0;font-size:5px">{{node.label}}</span>
                                    <span slot="reference">
                                        {{ node.label.substr(0,14-data.getLevel(data)*3)+'...' }}
                                    </span>
                                </el-popover>
                            </template>
                            <template v-if="node.label.length<=14-data.getLevel(data)*3">
                                {{node.label}}
                            </template>
                        </span>
                        <el-dropdown @command="command" style="float:right;padding-right: 5px">
                            <span class="el-dropdown-link">
                                <i class="el-icon-arrow-down el-icon-more"/>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item :command="()=>addSubGroup(data)">add sub group</el-dropdown-item>
                                <el-dropdown-item :command="()=>editSubGroup(data)">edit</el-dropdown-item>
                                <el-dropdown-item :command="()=>delApiGroup(data)"
                                                  style="color: red">delete</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </span>
                </el-tree>
            </el-aside>
            <el-main>
                <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}" height="895"
                          :row-style="{cursor:'pointer'}" @row-click="clickRow">
                    <el-table-column label="name" prop="name" width="200"/>
                    <el-table-column label="apiUri" prop="apiUri" width="200"/>
                    <el-table-column label="createName" prop="createName" width="150"/>
                    <el-table-column label="updateName" prop="updateName" width="150"/>
                    <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
                    <el-table-column v-if="hasAuth">
                        <template slot="header">
                            <el-row>
                                <el-col :span="24">
                                    <el-button size="mini" type="success" @click.stop="addApi">Add</el-button>
                                    <el-button size="mini" type="warning" @click.stop="batchOperate">Batch Operate
                                    </el-button>
                                </el-col>
                            </el-row>
                        </template>
                        <template slot-scope="scope">
                            <el-button size="mini" @click.stop="editApi(scope.row)">Edit</el-button>
                            <el-button size="mini" @click.stop="editApiByTag(scope.row)">New Tag</el-button>
                            <el-dropdown @command="command" style="padding-left: 10px">
                            <span class="el-dropdown-link" @click.stop="()=>{}">
                                <i class="el-icon-arrow-down el-icon-more"/>
                            </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item :command="()=>copyApi(scope.row)">Copy</el-dropdown-item>
                                    <el-dropdown-item :command="()=>delApi(scope.row)" style="color: red">Delete
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </template>
                    </el-table-column>
                </el-table>
                <page-template :query="query" @flush="findApiPage"/>
            </el-main>
        </el-container>
        <edit-api-group-dialog :dialog="editApiGroupDialog" :form="editApiGroupForm" @flush="findApiGroups"/>
        <confirm-dialog :dialog="delConfirmDialog" :form="delForm" @flush="delConfirmDialog.flush"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import EditApiGroupDialog from "./editApiGroupDialog";
    import PageTemplate from "../../components/pageTemplate/pageTemplate";
    import ConfirmDialog from "../../components/confirm/confirmDialog";

    export default {
        name: 'index',
        components: {ConfirmDialog, PageTemplate, EditApiGroupDialog},
        data() {
            return {
                selectedProjectUserType: this.$store.getters.selectedProjectUserType,
                projectId: this.$store.getters.selectedProjectId,
                groups: [],
                editApiGroupDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                editApiGroupForm: {},
                dataList: [],
                query: {
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
                selectedGroupId: '',
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: '',
                    url: '',
                    flush: () => {
                    }
                },
                delForm: {id: ''},
            }
        },
        computed: {
            hasAuth() {
                return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
            }
        },
        methods: {
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            selectGroup(apiGroup) {
                if (apiGroup.id === '') {
                    let selectElement = document.getElementsByClassName('el-tree-node is-current is-focusable');
                    if (selectElement.length > 0) {
                        selectElement[0].classList.remove('is-current');
                    }
                }
                this.selectedGroupId = apiGroup.id;
                this.findApiPage();
            },
            command(func) {
                if (func) {
                    func();
                }
            },
            addSubGroup(data) {
                this.editApiGroupForm = {
                    name: '',
                    projectId: this.projectId,
                    parentId: '',
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
                                (item) => (item.parentId && item.parentId !== '') ?
                                    dict[item.parentId].getLevel(dict[item.parentId]) + 1 : 0;
                            if (!item.parentId || item.parentId === '') {
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
            findApiPage() {
                this.query.projectId = this.projectId;
                this.query.groupId = this.selectedGroupId;
                UTILS.findPage(this, this, CONSTANT.REQUEST_URL.API_FIND_PAGE);
            },
            addApi() {
                this.$router.push({
                    path: '/api/edit',
                    query: {groupId: this.selectedGroupId}
                });

            },
            moveNode(before, after, inner) {
                let parentId = '';
                switch (inner) {
                    case 'before':
                        parentId = after.data.parentId;
                        break;
                    case 'inner':
                    case 'after':
                        parentId = after.data.id;
                        break;
                    default:
                }
                if (parentId !== before.data.parentId) {
                    before.data.parentId = parentId;
                    this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_EDIT, before.data).then(resp => {
                        if (UTILS.checkResp(resp)) {
                            this.findApiGroups();
                        }
                    });
                }
            },
            batchOperate() {

            },
            editApi(api) {
                this.$router.push({
                    path: '/api/edit',
                    query: {id: api.id}
                });
            },
            clickRow(row) {
                this.$router.push({
                    path: '/api/detail',
                    query: {id: row.id}
                });
            },
            delApiGroup(group) {
                this.delForm.id = group.id;
                this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api group: {name}?',
                    {name: group.name});
                this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_GROUP_DELETE;
                this.delConfirmDialog.flush = () => this.findApiGroups();
                this.delConfirmDialog.show = true;
            },
            editApiByTag(api) {

            },
            copyApi(api) {
                this.$router.push({
                    path: '/api/edit',
                    query: {id: api.id, copy: true}
                });
            },
            delApi(api) {
                this.delForm.id = api.id;
                this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api:  {name}?',
                    {name: api.name});
                this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_SAVE_REMOVE;
                this.delConfirmDialog.flush = () => this.findApiPage();
                this.delConfirmDialog.show = true;
            }
        },
        created() {
            this.findApiGroups();
            this.findApiPage();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #api-doc-container
        border 1px solid #d9d9d9

        .select-all
            cursor pointer

        .el-aside
            background-color #fff
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
            padding 0 0 0 5px
</style>
