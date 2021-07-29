<template>
    <div id="env-container">
        <el-table :data="tableData" :header-cell-style="{color:'#44B549','font-weight':'bold'}">
            <el-table-column label="Name" prop="name"/>
            <el-table-column label="Desc" prop="desc"/>
            <el-table-column label="Front uri" prop="frontUri"/>
            <el-table-column width="300">
                <template slot="header">
                    <el-row :gutter="11">
                        <el-col :span="8" style="margin-bottom: 5px">
                            <el-input style="width: 172px"
                                      v-model.trim="search" size="mini" placeholder="enter keyword search"/>
                        </el-col>
                    </el-row>
                    <el-row v-if="hasAuth">
                        <el-col :span="24">
                            <el-button size="mini" type="success" @click="addEnvConfig">Add</el-button>
                        </el-col>
                    </el-row>
                </template>
                <template slot-scope="scope" v-if="hasAuth">
                    <el-button
                            size="mini"
                            @click="editEnvConfig(scope.row)">Edit
                    </el-button>
                    <el-button
                            size="mini"
                            type="danger"
                            @click="deleteEnvConfig(scope.row)">Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <edit-env-config-dialog :dialog="editDialog" :form="form" @flush="findList" ref="edit-env-config-dialog"/>
        <confirm-dialog :dialog="delConfirmDialog" :form="delEnvConfForm" @flush="findList"/>
    </div>
</template>

<script type="text/ecmascript-6">

import EditEnvConfigDialog from "./editEnvConfigDialog";
import {CONSTANT} from "../../common/js/constant";
import {UTILS} from "../../common/js/utils";
import ConfirmDialog from "../../components/confirm/confirmDialog";

export default {
        name: 'index',
        components: {ConfirmDialog, EditEnvConfigDialog},
        data() {
            return {
                selectedProjectUserType: this.$store.getters.selectedProjectUserType,
                projectId: this.$store.getters.selectedProjectId,
                envConfigList: [],
                search: '',
                editDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                form: {},
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: 'Are you sure delete environment config?',
                    url: CONSTANT.REQUEST_URL.API_ENV_DELETE,
                },
                delEnvConfForm: {id: ''},
            }
        },
        computed: {
            tableData() {
                return this.envConfigList.filter(data => {
                    if (!this.search) {
                        return true;
                    }
                    let name = data.name.toLowerCase();
                    let desc = data.desc ? data.desc.toLowerCase() : '';
                    let frontUri = data.frontUri ? data.frontUri.toLowerCase() : '';
                    let search = this.search.toLowerCase();
                    return name.includes(search) || desc.includes(search) || frontUri.includes(search);
                });
            },
            hasAuth() {
                return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
            }
        },
        methods: {
            findList() {
                this.$axios.post(CONSTANT.REQUEST_URL.API_ENV_FIND_LIST, {projectId: this.projectId}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.envConfigList = resp.data.data;
                    }
                });
            },
            editEnvConfig(row) {
                this.editDialog = {
                    show: true,
                    title: 'Edit',
                    url: CONSTANT.REQUEST_URL.API_ENV_EDIT
                };
                this.form = UTILS.copyProperty(row, ['id', 'projectId', 'name', 'desc', 'frontUri', 'globalVariable', 'additionalVariable']);
                if (row.envHeader && row.envHeader !== '') {
                    this.form.envHeaders = JSON.parse(row.envHeader);
                } else {
                    this.form.envHeaders = [];
                }
                this.$nextTick(() => {
                    this.$refs['edit-env-config-dialog'].init();
                });
            },
            deleteEnvConfig(row) {
                this.delEnvConfForm.id = row.id;
                this.delConfirmDialog.show = true;
            },
            addEnvConfig() {
                this.editDialog = {
                    show: true,
                    title: 'Add',
                    url: CONSTANT.REQUEST_URL.API_ENV_ADD
                };
                this.form = {
                    projectId: this.projectId
                };
                this.form.envHeaders = [];
                this.$nextTick(() => {
                    this.$refs['edit-env-config-dialog'].init();
                });
            },
        },
        created() {
            this.findList();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #env-container
        border 1px solid #d9d9d9
</style>
