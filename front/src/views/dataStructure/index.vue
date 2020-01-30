<template>
    <div id="data-container" style="min-width: 1250px">
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}">
            <el-table-column label="name" prop="name" width="200"/>
            <el-table-column label="remark" prop="remark" width="200"/>
            <el-table-column label="createName" prop="createName" width="150"/>
            <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
            <el-table-column>
                <!-- eslint-disable-next-line vue/no-unused-vars -->
                <template slot="header" slot-scope="scope">
                    <el-row>
                        <el-col :span="24">
                            <el-button size="mini" type="success" @click="addDataStructure">Add</el-button>
                            <el-button size="mini" type="warning" @click="batchOperate">Batch Operate</el-button>
                        </el-col>
                    </el-row>
                </template>
                <template slot-scope="scope">
                    <el-button size="mini" @click="showReference(scope.row)">Show Reference</el-button>
                    <el-button size="mini" @click="editDataStructure(scope.row)">Edit</el-button>
                    <el-button size="mini" type="danger" @click="delDataStructure(scope.row)">Delete</el-button>
                </template>
            </el-table-column>
        </el-table>
        <page-template :query="query" @flush="findPage"/>
        <edit-data-structure-dialog :dialog="editDialog" :form="form" @flush="findPage" ref="editDataStructure"
                                    :data-list="form.dataList" :root-list="form.rootList"/>
    </div>
</template>

<script type="text/ecmascript-6">
    import EditDataStructureDialog from "./editDataStructureDialog";
    import PageTemplate from "../../components/pageTemplate/pageTemplate";
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'index',
        components: {PageTemplate, EditDataStructureDialog},
        data() {
            return {
                projectId: this.$store.getters.selectedProjectId,
                dataList: [],
                editDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                form: {},
                query: {
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
            };
        },
        methods: {
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            findPage() {
                this.query.projectId = this.projectId;
                UTILS.findPage(this, CONSTANT.REQUEST_URL.STRUCTURE_FIND_PAGE);
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
            batchOperate() {

            },
            editDataStructure(data) {
                this.editDialog = {
                    show: true,
                    title: 'Edit',
                    url: CONSTANT.REQUEST_URL.STRUCTURE_EDIT
                };
                this.$axios.post(CONSTANT.REQUEST_URL.STRUCTURE_FIND_DETAIL, data).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.form = resp.data.data;
                        this.$nextTick(() => {
                            this.$refs['editDataStructure'].init();
                        });
                    }
                });
            },
            showReference(data) {

            },
            delDataStructure(data) {

            },
        },
        created() {
            this.findPage();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #data-container
        border 1px solid #d9d9d9
</style>
