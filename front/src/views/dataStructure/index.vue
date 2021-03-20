<template>
    <div id="data-container" class="api-container">
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  :row-style="{cursor:'pointer'}" @row-click="showDataStructure">
            <el-table-column label="name" prop="name" width="200"/>
            <el-table-column label="remark" prop="remark" width="200"/>
            <el-table-column label="createName" prop="createName" width="150"/>
            <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
            <el-table-column>
                <template slot="header" v-if="hasAuth">
                    <el-row>
                        <el-col :span="3">
                            <el-button size="mini" type="success" @click="addDataStructure">Add</el-button>
                        </el-col>
                        <el-col :span="8">
                            <el-input placeholder="search name or remark" v-model.trim="query.nameOrRemark"
                                      @keyup.enter.native="findPage" size="mini" style="width: 160px"/>
                        </el-col>
                    </el-row>
                </template>
                <template slot-scope="scope">
                    <el-button size="mini" @click.stop="showReference(scope.row)">Show Reference</el-button>
                    <el-button size="mini" @click.stop="editDataStructure(scope.row)" v-if="hasAuth">Edit</el-button>
                    <el-button size="mini" type="danger" @click.stop="delDataStructure(scope.row)" v-if="hasAuth">
                        Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <page-template :query="query" @flush="findPage"/>
        <edit-data-structure-dialog :dialog="editDialog" :form="form" @flush="findPage" ref="editDataStructure"
                                    :data-list="form.dataList" :root-list="form.rootList"/>
        <show-reference-api-dialog :dialog="showReferenceApiDialog" ref="show-reference-api-dialog"/>
        <confirm-dialog :dialog="delConfirmDialog" :form="delForm" @flush="findPage" @error="delFailed"/>
    </div>
</template>

<script type="text/ecmascript-6">
import EditDataStructureDialog from "../../components/dataStructure/editDataStructureDialog";
import PageTemplate from "../../components/pageTemplate/pageTemplate";
import {CONSTANT} from "../../common/js/constant";
import {UTILS} from "../../common/js/utils";
import ShowReferenceApiDialog from "./showReferenceApiDialog";
import ConfirmDialog from "../../components/confirm/confirmDialog";

export default {
        name: 'index',
        components: {ConfirmDialog, ShowReferenceApiDialog, PageTemplate, EditDataStructureDialog},
        data() {
            return {
                selectedProjectUserType: this.$store.getters.selectedProjectUserType,
                projectId: this.$store.getters.selectedProjectId,
                dataList: [],
                editDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                form: {},
                query: {
                    nameOrRemark: '',
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
                showReferenceApiDialog: {
                    show: false,
                    structureId: '',
                },
                delConfirmDialog: {
                    show: false,
                    title: 'Delete Confirm',
                    content: '',
                    url: '',
                },
                delForm: {id: '', row: undefined},
            };
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
            findPage() {
                this.query.projectId = this.projectId;
                UTILS.findPage(this, this, CONSTANT.REQUEST_URL.STRUCTURE_FIND_PAGE);
            },
            addDataStructure() {
                this.form = {
                    projectId: this.projectId,
                };
                this.editDialog = {
                    show: true,
                    title: 'Add',
                    url: CONSTANT.REQUEST_URL.STRUCTURE_ADD
                };
                this.$nextTick(() => {
                    this.$refs['editDataStructure'].init();
                });
            },
            editDataStructure(row) {
                this.editDialog = {
                    show: true,
                    title: 'Edit',
                    url: CONSTANT.REQUEST_URL.STRUCTURE_EDIT
                };
                this.$axios.post(CONSTANT.REQUEST_URL.STRUCTURE_FIND_DETAIL, row).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.form = resp.data.data;
                        this.$nextTick(() => {
                            this.$refs['editDataStructure'].init();
                        });
                    }
                });
            },
            showDataStructure(row) {
                this.editDialog = {
                    show: true,
                    title: 'View',
                    onlyRead: true,
                };
                this.$axios.post(CONSTANT.REQUEST_URL.STRUCTURE_FIND_DETAIL, row).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.form = resp.data.data;
                        this.$nextTick(() => {
                            this.$refs['editDataStructure'].init();
                        });
                    }
                });
            },
            showReference(row) {
                this.showReferenceApiDialog.structureId = row.id;
                this.showReferenceApiDialog.show = true;
                this.$refs['show-reference-api-dialog'].findList();
            },
            delDataStructure(row) {
                // STRUCTURE_REMOVE
                this.delForm.id = row.id;
                this.delForm.row = row;
                this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api group: {name}?',
                    {name: row.name});
                this.delConfirmDialog.url = CONSTANT.REQUEST_URL.STRUCTURE_REMOVE;
                this.delConfirmDialog.show = true;
            },
            delFailed(respData) {
                if (respData.code === CONSTANT.RESULT_CODE.STRUCTURE_USED) {
                    this.showReference(this.delForm.row);
                }
                this.$message({
                    message: 'delete failed: ' + respData.message,
                    type: 'error',
                    customClass: 'error-msg',
                });
            }
        },
        created() {
            this.findPage();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #data-container
        border 1px solid #d9d9d9
    .error-msg
        z-index 3000!important
</style>
