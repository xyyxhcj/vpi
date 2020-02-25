<template>
    <div class="group-project-table">
        <el-table :data="tableData" @row-click="clickRow" :ref="config.refPre+'projectTable'"
                  :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  :row-style="{cursor:'pointer'}">
            <el-table-column type="selection" width="25" v-if="showSelect"/>
            <el-table-column width="350">
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
            <el-table-column label="desc" prop="desc" width="200"/>
            <el-table-column label="create" prop="createName" width="150" v-if="!config.isOwner"/>
            <el-table-column label="updateTime" width="160" :formatter="(row)=>dateFormat(row.updateTime)"/>
            <el-table-column min-width="330" v-if="config.isOwner">
                <template slot="header">
                    <template v-if="!showSelect">
                        <el-button size="mini" type="success" @click="addProjectGroup">Add ProjectGroup</el-button>
                        <el-button size="mini" type="success" @click="addProject">Add Project</el-button>
                        <el-button size="mini" type="warning" @click="batchOperate">Batch Operate</el-button>
                    </template>
                    <template v-else>
                        <el-button size="mini" type="warning" @click.stop="moveGroup">Move Group</el-button>
                        <el-button size="mini" @click.stop="showSelect=false">Cancel</el-button>
                    </template>
                </template>
                <template slot-scope="scope" v-if="scope.row.createId===user.id">
                    <el-button size="mini" v-if="scope.row.projectType!==undefined"
                               @click.native.stop="auth(scope.row)">
                        Auth
                    </el-button>
                    <el-button size="mini" @click.native.stop="edit(scope.row)">Edit</el-button>
                    <el-button size="mini" type="danger" @click.native.stop="del(scope.row)">Delete</el-button>
                </template>
            </el-table-column>
            <el-table-column v-if="!config.isOwner">
                <template slot-scope="scope" v-if="parseInt(CONSTANT.AUTH_ROLE.ADMIN)===scope.row.userType">
                    <el-button size="mini" @click.native.stop="auth(scope.row)">
                        Auth
                    </el-button>
                </template>

            </el-table-column>
        </el-table>
        <edit-auth-dialog :dialog="editAuthDialog" ref="auth-dialog"/>
        <edit-project-group-dialog :dialog="editProjectGroupDialog" :form="editProjectGroupForm"
                                   @flush="findList"/>
        <edit-project-dialog :dialog="editProjectDialog" :form="editProjectForm" @flush="findList"/>
        <confirm-dialog :dialog="delConfirmDialog" :form="delProjectForm" @flush="findList"/>
        <select-project-group-dialog :dialog="selectProjectGroupDialog" @flush="findList"
                                     ref="select-project-group-dialog"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import ConfirmDialog from "../confirm/confirmDialog";
    import EditAuthDialog from "./editAuthDialog";
    import EditProjectDialog from "./editProjectDialog";
    import EditProjectGroupDialog from "./editProjectGroupDialog";
    import SelectProjectGroupDialog from "./selectProjectGroupDialog";

    export default {
        name: 'groupProjectTable',
        components: {
            SelectProjectGroupDialog,
            ConfirmDialog,
            EditAuthDialog,
            EditProjectDialog,
            EditProjectGroupDialog
        },
        props: {
            config: {
                default() {
                    return {
                        reqUri: undefined,
                        isOwner: false,
                        refPre: '',
                    };
                }
            },
        },
        data() {
            return {
                CONSTANT,
                user: this.$store.getters.user,
                tableData: [],
                selectedGroup: undefined,
                query: {
                    groupId: undefined
                },
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: '',
                    url: '',
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
                editAuthDialog: {
                    show: false,
                    project: Object,
                },
                delProjectForm: {id: ''},
                showSelect: false,
                selectProjectGroupDialog: {
                    show: false,
                    projects: [],
                    selectedGroups: [],
                },
            };
        },
        methods: {
            projectTypeFormat(row) {
                return row.projectType !== undefined ? CONSTANT.PROJECT_TYPE[row.projectType] : '';
            },
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            clickRow(row) {
                if (this.showSelect) {
                    this.$refs[this.config.refPre + 'projectTable'].toggleRowSelection(row);
                } else if (row.projectType === undefined) {
                    this.selectedGroup = row;
                    this.query.groupId = row.id;
                    this.findList();
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
                this.findList();
            },
            findList() {
                this.showSelect = false;
                if (this.config.reqUri) {
                    this.$axios.post(this.config.reqUri, this.query).then(resp => {
                        if (UTILS.checkResp(resp)) {
                            this.tableData = resp.data.data;
                        }
                    });
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
            auth(row) {
                this.editAuthDialog = {
                    show: true,
                    project: row,
                };
                this.$nextTick(() => {
                    this.$refs['auth-dialog'].init();
                });
            },
            batchOperate() {
                this.showSelect = true;
            },
            moveGroup() {
                let selection = this.$refs[this.config.refPre + 'projectTable'].selection;
                if (!selection || selection.length === 0) {
                    this.$message.error('please select first');
                    return;
                }
                this.selectProjectGroupDialog.projects = [];
                this.selectProjectGroupDialog.selectedGroups = [];
                selection.forEach(row => {
                    if (row.projectType === undefined) {
                        this.selectProjectGroupDialog.selectedGroups.push(row.id);
                        this.selectProjectGroupDialog.projects.push({groupId: row.id});
                    } else {
                        this.selectProjectGroupDialog.projects.push({id: row.id})
                    }
                });
                this.selectProjectGroupDialog.show = true;
                this.$refs['select-project-group-dialog'].findProjectGroups();
            }
        },
        created() {
            if (this.user) {
                this.findList();
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .group-project-table
        padding-left 15px

        div.cell
            padding 0 1px

</style>
