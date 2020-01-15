<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="75%">
        <el-table :data="dataList" tooltip-effect="dark" style="width: 100%" ref="userTable" row-key="id"
                  @selection-change="selectedChange">
            <el-table-column type="selection" width="55" :selectable="notCreate" :reserve-selection="true">
            </el-table-column>
            <el-table-column prop="userName" label="userName" width="150"/>
            <el-table-column prop="loginName" label="loginName" width="150"/>
            <el-table-column prop="phone" label="phone" width="150"/>
            <el-table-column prop="email" label="email" width="150"/>
        </el-table>
        <page-template :query="query" @flush="findPage"/>
        <div slot="footer" class="footer-container">
            <!--<el-select class="selectInput" v-model="selectedList" multiple @visible-change="showSelect"
                       ref="showSelected" :collapse-tags="true">
                <el-option
                        v-for="item in dataList"
                        :key="item.id"
                        :value="item.userName+'('+item.loginName+')'">
                </el-option>
            </el-select>-->
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="setUsers" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    // cjTodo 2020/1/12 如果数据中id与管理员id相同，则置灰不可选 https://blog.csdn.net/QQ_Empire/article/details/94464118
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import PageTemplate from "../pageTemplate/pageTemplate";

    export default {
        name: 'selectUserDialog',
        components: {PageTemplate},
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        url: '',
                        project: Object,
                    };
                }
            },
        },
        data() {
            return {
                user: this.$store.getters.user,
                CONSTANT: CONSTANT,
                dataList: [],
                query: {
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
                projectUserQuery: {
                    projectId: '',
                },
                selectedList: [],
            }
        },
        computed: {
        },
        methods: {
            setUsers() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        /* todo this.$axios.post(this.dialog.url, this.form).then(resp => {
                            UTILS.showResult(this, resp);
                        });*/
                    }
                });
            },
            findPage() {
                this.projectUserQuery.projectId = this.dialog.project.id;
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_FIND_PROJECT_USER, this.projectUserQuery).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.selectedList = resp.data.data;
                        // console.log(this.selectedList);
                        UTILS.findPage(this, CONSTANT.REQUEST_URL.USER_FIND_PAGE,function (obj) {
                            obj.$refs['userTable'].toggleRowSelection({id: obj.user.id});
                            obj.selectRows(resp.data.data, obj);
                        });
                    }
                });
            },
            selectedChange(selectedRows) {
                this.selectedList = selectedRows;
            },
            selectRows(rows, obj) {
                rows.forEach(row => {
                    obj.$refs['userTable'].toggleRowSelection(row);
                    if (!UTILS.contains(obj.selectedList, row, function (selectedListElement, rowElement) {
                        return selectedListElement.id === rowElement.id;
                    })) {
                        obj.selectedList.push(row);
                    }
                });
            },
            notCreate(row) {
                return row.id !== this.user.id;
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .footer-container
        text-align left

    .selectInput
        margin-right 100px
        width 500px
</style>
