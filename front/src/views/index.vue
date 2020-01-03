<template>
    <div id="project-container">
        <el-table :data="tableData" @row-click="clickRow"
                  :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  :row-style="{cursor:'pointer'}">
            <el-table-column>
                <template slot="header">
                    name
                    <el-tag style="margin-left:10px;cursor:pointer;" @click="return2Previous"
                            v-if="selectedGroup!==undefined">
                        return to previous
                    </el-tag>
                </template>
                <template slot-scope="scope">
                    <i class="el-icon-folder" v-if="scope.row.projectType===undefined" style="margin-right: 10px"/>
                    <span>{{scope.row.name}}</span>
                </template>
            </el-table-column>
            <el-table-column label="projectVersion" prop="projectVersion" width="120"/>
            <el-table-column label="projectType" prop="projectType" width="100" :formatter="projectTypeFormat"/>
            <el-table-column label="desc" prop="desc"/>
            <el-table-column label="updateTime" prop="updateTime" width="200"
                             :formatter="(row)=>dateFormat(row.updateTime)"/>
            <el-table-column>
                <template slot="header">
                    <el-button size="mini" type="success" @click="addProjectGroup">Add ProjectGroup</el-button>
                    <el-button size="mini" type="success" @click="addProject">Add Project</el-button>
                    <el-button size="mini" type="warning" @click="batchOperate">batch operate</el-button>
                </template>
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            @click.native.stop="edit(scope.$index, scope.row)">Edit
                    </el-button>
                    <el-button
                            size="mini"
                            type="danger"
                            @click.native.stop="del(scope.$index, scope.row)">Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <project-group-edit-dialog :dialog="editProjectGroupDialog" :form="editProjectGroupForm"
                                   @flush="findListByGroup"/>
        <!--        <env-config-edit-dialog :dialog="editDialog" :form="form"/>-->
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../common/js/constant";
    import {utils} from "../common/js/utils";
    import ProjectGroupEditDialog from "./projectGroupEditDialog";

    export default {
        name: 'index',
        components: {ProjectGroupEditDialog},
        data() {
            return {
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
            };
        },
        methods: {
            edit() {

            },
            del() {

            },
            addProjectGroup() {
                this.editProjectGroupDialog = {
                    show: true,
                    title: 'add project group',
                    url: CONSTANT.REQUEST_URL.PROJECT_GROUP_ADD
                };
            },
            addProject() {

            },
            batchOperate() {

            },
            projectTypeFormat(row) {
                return row.projectType !== undefined ? CONSTANT.PROJECT_TYPE[row.projectType] : '';
            },
            dateFormat(time) {
                return utils.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            findListByGroup() {
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_FIND_LIST_BY_GROUP, this.query).then(resp => {
                    if (utils.checkResp(resp)) {
                        this.tableData = resp.data.data;
                    }
                })
            },
            clickRow(row) {
                if (row.projectType === undefined) {
                    this.selectedGroup = row;
                    this.query.groupId = row.id;
                    this.findListByGroup();
                }
            },
            return2Previous() {
                this.query.groupId = this.selectedGroup.parentId;
                if (this.query.groupId !== null) {
                    this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_GROUP_FIND_DETAIL, {id: this.query.groupId})
                        .then(resp => {
                            if (utils.checkResp(resp)) {
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
            this.findListByGroup();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #project-container
        border 1px solid #d9d9d9
        background-color white
</style>
