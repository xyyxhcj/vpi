<template>
    <div id="data-container">
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}">
            <el-table-column label="name" prop="name"/>
            <el-table-column label="remark" prop="remark"/>
            <el-table-column label="type" prop="type"/>
            <el-table-column label="createName" prop="createName"/>
            <el-table-column label="updateTime" prop="updateTime"/>
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
        <edit-data-structure-dialog :dialog="editDialog" :form="form" @flush="findPage"/>
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
            findPage() {
                this.query.projectId = this.projectId;
                UTILS.findPage(this, CONSTANT.REQUEST_URL.STRUCTURE_FIND_PAGE);
            },
            addDataStructure() {
                this.editDialog = {
                    show: true,
                    title: 'Add',
                    url: CONSTANT.REQUEST_URL.STRUCTURE_ADD
                };
                this.form = {
                    projectId: this.projectId
                };
            },
            batchOperate() {

            },
            editDataStructure(data) {

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
