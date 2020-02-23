<template>
    <el-dialog :append-to-body="true" title="Reference Api" :visible.sync="dialog.show" width="980px">
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}" height="700"
                  :row-style="{cursor:'pointer'}" @row-click="clickRow">
            <el-table-column width="81">
                <template slot-scope="scope">
                    <el-tag size="mini" effect="plain" style="padding: 0 4px"
                            :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                        {{CONSTANT.API_STATUS[scope.row.apiStatus]}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="name" prop="name" width="200"/>
            <el-table-column label="apiUri" prop="apiUri" width="200"/>
            <el-table-column label="createName" prop="createName" width="120"/>
            <el-table-column label="updateName" prop="updateName" width="120"/>
            <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
        </el-table>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'showReferenceApiDialog',
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        structureId: '',
                    }
                }
            },
        },
        data() {
            return {
                CONSTANT,
                dataList: [],
            }
        },
        methods: {
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            clickRow(row) {
                let newTab = this.$router.resolve({
                    path: '/api/detail',
                    query: {id: row.id},
                });
                window.open(newTab.href, '_blank');
            },
            findList() {
                this.dataList = [];
                if (this.dialog.structureId && this.dialog.structureId !== '') {
                    this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_REFERENCE_API, this.dialog).then(resp => {
                        if (UTILS.checkResp(resp)) {
                            this.dataList = resp.data.data;
                        }
                    });
                }
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
