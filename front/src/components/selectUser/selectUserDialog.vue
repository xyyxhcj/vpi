<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="75%">
        <el-table :data="dataList" tooltip-effect="dark" style="width: 100%">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    label="日期"
                    width="120">
                <template slot-scope="scope">{{ scope.row.date }}</template>
            </el-table-column>
            <el-table-column prop="userName" label="userName" width="150"/>
            <el-table-column prop="loginName" label="loginName" width="150"/>
            <el-table-column prop="phone" label="phone" width="150"/>
            <el-table-column prop="email" label="email" width="150"/>
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="1000">
        </el-pagination>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="addUsers" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    // cjTodo 2020/1/12 如果数据中id与管理员id相同，则置灰不可选
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'selectUserDialog',
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        url: ''
                    }
                }
            },
        },
        data() {
            return {
                dataList: [],
                query: {
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                }
            }
        },
        methods: {
            addUsers() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        /*this.$axios.post(this.dialog.url, this.form).then(resp => {
                            UTILS.showResult(this, resp);
                        });*/
                    }
                });
            },
            findPage() {
                UTILS.findPage(this, CONSTANT.REQUEST_URL.USER_FIND_PAGE);
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
