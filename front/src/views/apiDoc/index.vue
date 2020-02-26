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
                <div class="select-all" @click="selectGroup">all</div>
                <el-tree :data="groups" :props="{label:'name',children:'childList'}" :expand-on-click-node="false"
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
                            <span class="el-dropdown-link" @click.stop="()=>{}">
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
                <div style="width: auto;background-color:#fff;text-align: left;padding:5px 0 0 10px">
                    <el-row type="flex">
                        <el-col style="width: 155px">
                            <el-dropdown size="mini" @command="command">
                                <el-button size="mini"
                                           :type="query.apiStatus===undefined?'primary':
                                   (query.apiStatus===0?'success':
                                        query.apiStatus===2||query.apiStatus===8?'danger':'warning')">
                                    {{query.apiStatus===undefined?'filter status':CONSTANT.API_STATUS[query.apiStatus]}}
                                    <i class="el-icon-arrow-down el-icon--right"/>
                                </el-button>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item class="api-status-option" :command="()=>selectQueryStatus()">
                                        All
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option success"
                                                      :command="()=>selectQueryStatus(0)">
                                        {{CONSTANT.API_STATUS[0]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(7)">
                                        {{CONSTANT.API_STATUS[7]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(6)">
                                        {{CONSTANT.API_STATUS[6]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(1)">
                                        {{CONSTANT.API_STATUS[1]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(5)">
                                        {{CONSTANT.API_STATUS[5]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(4)">
                                        {{CONSTANT.API_STATUS[4]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(3)">
                                        {{CONSTANT.API_STATUS[3]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option warning"
                                                      :command="()=>selectQueryStatus(8)">
                                        {{CONSTANT.API_STATUS[8]}}
                                    </el-dropdown-item>
                                    <el-dropdown-item class="api-status-option error"
                                                      :command="()=>selectQueryStatus(2)">
                                        {{CONSTANT.API_STATUS[2]}}
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </el-col>
                        <el-col style="width: 200px">
                            <el-input placeholder="search name or uri" v-model.trim="query.nameOrUri"
                                      @keyup.enter.native="findApiPage" size="mini" style="top:2px;width: 150px"/>
                        </el-col>
                        <el-col>
                            <el-button icon="el-icon-search" @click="findApiPage" circle size="mini"/>
                        </el-col>
                    </el-row>
                </div>
                <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}" :height="tableHeight"
                          :row-style="{cursor:'pointer'}" @row-click="clickRow" row-key="id" ref="api-doc-table">
                    <el-table-column type="selection" :width="showSelect?'20':'1'"/>
                    <el-table-column width="81">
                        <template slot-scope="scope">
                            <el-popover trigger="hover" placement="right-start" v-if="scope.row.group">
                                <p style="font-size: 10px">Group: {{scope.row.group.name}}</p>
                                <div slot="reference">
                                    <el-tag size="mini" effect="plain" style="padding: 0 4px"
                                            :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                                        {{CONSTANT.API_STATUS[scope.row.apiStatus]}}
                                    </el-tag>
                                </div>
                            </el-popover>
                            <el-tag size="mini" effect="plain" style="padding: 0 4px" v-else
                                    :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                                {{CONSTANT.API_STATUS[scope.row.apiStatus]}}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="name" prop="name" width="200"/>
                    <el-table-column label="apiUri" prop="apiUri" width="200"/>
                    <el-table-column label="createName" prop="createName" width="110"/>
                    <el-table-column label="updateName" prop="updateName" width="110"/>
                    <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
                    <el-table-column v-if="hasAuth" width="420">
                        <template slot="header">
                            <el-row>
                                <el-col :span="24">
                                    <template v-if="!showSelect">
                                        <el-button size="mini" type="success" @click.stop="addApi">Add</el-button>
                                        <el-button size="mini" type="warning" @click.stop="batchOperate">
                                            Batch Operate
                                        </el-button>
                                    </template>
                                    <template v-else>
                                        <el-button size="mini" @click.stop="showSelect=false">Cancel</el-button>
                                        <el-button size="mini" type="primary" @click.stop="switchStatus">
                                            Switch Status
                                        </el-button>
                                        <el-button size="mini" type="primary" @click.stop="moveGroup">
                                            Move Group
                                        </el-button>
                                        <el-button size="mini" type="danger" @click.stop="delApi">Batch Delete
                                        </el-button>
                                    </template>
                                </el-col>
                            </el-row>
                        </template>
                        <template slot-scope="scope">
                            <el-button size="mini" @click.stop="testApi(scope.row)">Test</el-button>
                            <el-button size="mini" @click.stop="editApi(scope.row)">Edit</el-button>
                            <el-button size="mini" @click.stop="viewApiByTab(scope.row)">New Tab</el-button>
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
        <select-api-status-dialog :dialog="selectApiStatusDialog" @flush="findApiPage"/>
        <select-api-group-dialog :dialog="selectApiGroupDialog" @flush="findApiPage" ref="select-api-group-dialog"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import EditApiGroupDialog from "./editApiGroupDialog";
    import PageTemplate from "../../components/pageTemplate/pageTemplate";
    import ConfirmDialog from "../../components/confirm/confirmDialog";
    import SelectApiStatusDialog from "../../components/selectApiStatus/selectApiStatusDialog";
    import SelectApiGroupDialog from "../../components/selectApiGroup/selectApiGroupDialog";

    export default {
        name: 'index',
        components: {SelectApiGroupDialog, SelectApiStatusDialog, ConfirmDialog, PageTemplate, EditApiGroupDialog},
        data() {
            return {
                CONSTANT,
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
                    groupIds: [],
                    apiStatus: undefined,
                    nameOrUri: '',
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
                selectedGroupId: '',
                selectedGroup: undefined,
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: '',
                    url: '',
                    flush: () => {
                    }
                },
                delForm: {ids: [], projectId: this.$store.getters.selectedProjectId},
                showSelect: false,
                selectApiStatusDialog: {
                    show: false,
                    ids: [],
                    projectId: '',
                    apiStatus: '',
                },
                selectApiGroupDialog: {
                    show: false,
                    ids: [],
                    projectId: this.$store.getters.selectedProjectId,
                },
                tableHeight: window.innerHeight - 149,
            }
        },
        computed: {
            hasAuth() {
                return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
            },
        },
        methods: {
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            selectGroup(apiGroup) {
                if (!apiGroup) {
                    // click all
                    let selectElement = document.getElementsByClassName('el-tree-node is-current is-focusable');
                    if (selectElement.length > 0) {
                        selectElement[0].classList.remove('is-current');
                    }
                    this.selectedGroupId = '';
                    this.selectedGroup = undefined;
                } else {
                    this.selectedGroupId = apiGroup.id;
                    this.selectedGroup = apiGroup;
                }
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
                this.showSelect = false;
                this.selectApiStatusDialog.apiStatus = '';
                this.query.projectId = this.projectId;
                this.query.groupIds = [];
                if (this.selectedGroup && this.selectedGroup.id) {
                    // get all child group id
                    let stack = Array(this.selectedGroup);
                    while (stack.length > 0) {
                        let pop = stack.pop();
                        this.query.groupIds.push(pop.id);
                        if (pop.childList && pop.childList.length > 0) {
                            pop.childList.forEach(child => stack.push(child));
                        }
                    }
                }
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
                this.showSelect = true;
            },
            editApi(api) {
                this.$router.push({
                    path: '/api/edit',
                    query: {id: api.id}
                });
            },
            clickRow(row) {
                if (this.showSelect) {
                    this.$refs['api-doc-table'].toggleRowSelection(row);
                } else {
                    this.$router.push({
                        path: '/api/detail',
                        query: {id: row.id}
                    });
                }
            },
            delApiGroup(group) {
                this.delForm.id = group.id;
                this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api group: {name}?',
                    {name: group.name});
                this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_GROUP_DELETE;
                this.delConfirmDialog.flush = () => this.findApiGroups();
                this.delConfirmDialog.show = true;
            },
            viewApiByTab(api) {
                let newTab = this.$router.resolve({
                    path: '/api/detail',
                    query: {id: api.id},
                });
                window.open(newTab.href, '_blank');
            },
            copyApi(api) {
                this.$router.push({
                    path: '/api/edit',
                    query: {id: api.id, copy: true}
                });
            },
            testApi(api) {
                this.$router.push({
                    path: '/api/test',
                    query: {
                        id: api.id,
                        selectedEnv: this.selectedEnv,
                    }
                });
            },
            delApi(api) {
                if (api.id !== undefined) {
                    this.delForm.ids = [api.id];
                    this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api:  {name}?',
                        {name: api.name});
                } else {
                    let selection = this.$refs['api-doc-table'].selection;
                    if (!selection || selection.length === 0) {
                        this.$message.error('please select first');
                        return;
                    }
                    this.delForm.ids = [];
                    selection.forEach(row => this.delForm.ids.push(row.id));
                }
                this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_REMOVE;
                this.delConfirmDialog.flush = () => this.findApiPage();
                this.delConfirmDialog.content = 'Are you sure delete api?';
                this.delConfirmDialog.show = true;
            },
            switchStatus() {
                let selection = this.$refs['api-doc-table'].selection;
                if (!selection || selection.length === 0) {
                    this.$message.error('please select first');
                    return;
                }
                this.selectApiStatusDialog.projectId = this.projectId;
                this.selectApiStatusDialog.ids = [];
                selection.forEach(row => this.selectApiStatusDialog.ids.push(row.id));
                this.selectApiStatusDialog.show = true;
            },
            moveGroup() {
                let selection = this.$refs['api-doc-table'].selection;
                if (!selection || selection.length === 0) {
                    this.$message.error('please select first');
                    return;
                }
                this.selectApiGroupDialog.ids = [];
                selection.forEach(row => this.selectApiGroupDialog.ids.push(row.id));
                this.selectApiGroupDialog.show = true;
                this.$refs['select-api-group-dialog'].findApiGroups();
            },
            selectQueryStatus(apiStatus) {
                this.query.apiStatus = apiStatus;
                this.findApiPage();
            }
        },
        created() {
            this.findApiGroups();
            this.findApiPage();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .api-status-option
        &.success
            color #44B549

        &.warning
            color #E6A23C

        &.error
            color #F56C6C

    #api-doc-container
        border 1px solid #d9d9d9

        .el-tree-node__content
            height 30px

        .el-table__header-wrapper
            height 40px

        div.cell
            padding 0 5px

        .el-button
            margin-top 1.5px

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
