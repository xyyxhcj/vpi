<template>
    <div id="project-container">
        <el-table :data="tableData" @row-click="clickRow"
                  :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  :row-style="{cursor:'pointer'}">
            <el-table-column min-width="200">
                <template slot="header">
                    name
                    <el-tag size="mini" style="margin:0 10px;cursor:pointer;" @click="return2Previous"
                            v-if="selectedGroup!==undefined">
                        return to previous
                    </el-tag>
                </template>
                <template slot-scope="scope">
                    <i class="el-icon-folder" v-if="scope.row.projectType===undefined" style="margin-right: 10px"/>
                    <span>{{scope.row.name}}</span>
                </template>
            </el-table-column>
            <el-table-column label="projectVersion" prop="projectVersion" width="115"/>
            <el-table-column label="projectType" width="98" :formatter="projectTypeFormat"/>
            <el-table-column label="desc" prop="desc" min-width="150"/>
            <el-table-column label="updateTime" width="160" :formatter="(row)=>dateFormat(row.updateTime)"/>
            <el-table-column min-width="330">
                <template slot="header">
                    <el-button size="mini" type="success" @click="addProjectGroup">Add ProjectGroup</el-button>
                    <el-button size="mini" type="success" @click="addProject">Add Project</el-button>
                    <el-button size="mini" type="warning" @click="batchOperate">Batch Operate</el-button>
                </template>
                <template slot-scope="scope" v-if="scope.row.createId===user.id">
                    <el-button size="mini" v-if="scope.row.projectType!==undefined" @click.native.stop="auth(scope.row)">Auth</el-button>
                    <el-button size="mini" @click.native.stop="edit(scope.row)">Edit</el-button>
                    <el-button size="mini" type="danger" @click.native.stop="del(scope.row)">Delete</el-button>
                </template>
            </el-table-column>
        </el-table>
        <edit-auth-dialog :dialog="editAuthDialog" ref="authDialog"/>
        <edit-project-group-dialog :dialog="editProjectGroupDialog" :form="editProjectGroupForm"
                                   @flush="findListByGroup"/>
        <edit-project-dialog :dialog="editProjectDialog" :form="editProjectForm" @flush="findListByGroup"/>
        <confirm-dialog :dialog="delConfirmDialog" :form="delProjectForm" @flush="findListByGroup"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../common/js/constant";
    import {UTILS} from "../common/js/utils";
    import EditProjectGroupDialog from "./editProjectGroupDialog";
    import EditProjectDialog from "./editProjectDialog";
    import EditAuthDialog from "./editAuthDialog";
    import ConfirmDialog from "../components/confirm/confirmDialog";

    export default {
        name: 'index',
        components: {ConfirmDialog, EditAuthDialog, EditProjectDialog, EditProjectGroupDialog},
        data() {
            return {
                user: this.$store.getters.user,
                editAuthDialog: {
                    show: false,
                    project: Object,
                },
                editProjectDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                editProjectForm: {},
                editProjectGroupDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                editProjectGroupForm: {},
                tableData: [],
                query: {
                    groupId: undefined
                },
                selectedGroup: undefined,
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: '',
                    url: '',
                },
                delProjectForm: {id: ''},
            };
        },
        methods: {
            auth(row) {
                this.editAuthDialog = {
                    show: true,
                    project: row,
                };
                this.$nextTick(() => {
                    this.$refs['authDialog'].init();
                });
            },
            edit(row) {
                if (row.projectType !== undefined) {
                    this.editProjectDialog = {
                        show: true,
                        title: 'edit project',
                        url: CONSTANT.REQUEST_URL.PROJECT_EDIT
                    };
                    this.editProjectForm = UTILS.copyProperty(row, ['id', 'name', 'groupId', 'projectType', 'projectVersion', 'desc']);
                } else {
                    this.editProjectGroupDialog = {
                        show: true,
                        title: 'edit project group',
                        url: CONSTANT.REQUEST_URL.PROJECT_GROUP_EDIT
                    };
                    this.editProjectGroupForm = UTILS.copyProperty(row, ['id', 'name', 'parentId']);
                }
            },
            del(row) {
                this.delProjectForm.id = row.id;
                if (row.projectType !== undefined) {
                    this.delConfirmDialog.content = 'Are you sure delete project?';
                    this.delConfirmDialog.url = CONSTANT.REQUEST_URL.PROJECT_REMOVE;
                    this.delConfirmDialog.show = true;
                } else {
                    this.delConfirmDialog.content = 'Are you sure delete project group?';
                    this.delConfirmDialog.url = CONSTANT.REQUEST_URL.PROJECT_GROUP_DELETE;
                    this.delConfirmDialog.show = true;
                }
            },
            addProjectGroup() {
                this.editProjectGroupForm = {parentId: this.query.groupId};
                this.editProjectGroupDialog = {
                    show: true,
                    title: 'add project group',
                    url: CONSTANT.REQUEST_URL.PROJECT_GROUP_ADD
                };
            },
            addProject() {
                this.editProjectForm = {
                    groupId: this.query.groupId,
                    projectVersion: CONSTANT.CONFIG.DEFAULT_PROJECT_VERSION,
                    projectType: CONSTANT.CONFIG.DEFAULT_PROJECT_TYPE,
                };
                this.editProjectDialog = {
                    show: true,
                    title: 'add project',
                    url: CONSTANT.REQUEST_URL.PROJECT_ADD
                };
            },
            batchOperate() {

            },
            projectTypeFormat(row) {
                return row.projectType !== undefined ? CONSTANT.PROJECT_TYPE[row.projectType] : '';
            },
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            findListByGroup() {
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_FIND_LIST_BY_GROUP, this.query).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.tableData = resp.data.data;
                    }
                })
            },
            clickRow(row) {
                if (row.projectType === undefined) {
                    this.selectedGroup = row;
                    this.query.groupId = row.id;
                    this.findListByGroup();
                } else {
                    this.$store.dispatch('selectProject', row);
                    this.$router.push('/apiDoc');
                }
            },
            return2Previous() {
                this.query.groupId = this.selectedGroup.parentId;
                if (this.query.groupId !== null) {
                    this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_GROUP_FIND_DETAIL, {id: this.query.groupId})
                        .then(resp => {
                            if (UTILS.checkResp(resp)) {
                                this.selectedGroup = resp.data.data;
                            }
                        });
                } else {
                    this.selectedGroup = undefined;
                }
                this.findListByGroup();
            }
        },
        created() {
            if (this.user) {
                this.findListByGroup();
            } else {
                this.$router.go(0);
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #project-container
        border 1px solid #d9d9d9
        background-color white

        .el-button
            padding 5px 5px
            margin-top: 1.5px
</style>
