<template>
    <el-dialog :append-to-body="true" :visible.sync="dialog.show" :title="dialog.title"
               :close-on-click-modal="false" center width="50%">
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  highlight-current-row @current-change="selectDataStructure" :row-style="{cursor:'pointer'}">
            <el-table-column label="name" prop="name"/>
            <el-table-column label="remark" prop="remark"/>
        </el-table>
        <page-template :query="query" @flush="findPage"/>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";
    import PageTemplate from "../pageTemplate/pageTemplate";

    export default {
        name: 'selectDataStructureDialog',
        components: {PageTemplate},
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                    };
                }
            }
        },
        data() {
            return {
                dataList: [],
                query: {
                    projectId: this.$store.getters.selectedProjectId,
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
            }
        },
        methods: {
            findPage() {
                UTILS.findPage(this, this, CONSTANT.REQUEST_URL.STRUCTURE_FIND_PAGE, () => {

                });
            },
            selectDataStructure(row) {
                this.$emit('selectDataStructure', row);
                this.dialog.show = false;
            }
        },
        mounted() {
            this.findPage();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
