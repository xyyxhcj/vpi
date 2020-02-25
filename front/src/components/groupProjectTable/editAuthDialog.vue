<template>
    <el-dialog id="auth-dialog" :append-to-body="true" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="78%" :fullscreen="true" @submit.native.prevent>
        <el-container>
            <el-aside width="200px" class="auth-aside">
                <el-header class="aside-header">Role</el-header>
                <el-menu :default-active="projectUserQuery.userType" @select="selectRole">
                    <el-menu-item :index="CONSTANT.AUTH_ROLE.ALL">All</el-menu-item>
                    <el-menu-item :index="CONSTANT.AUTH_ROLE.ADMIN">Admin</el-menu-item>
                    <el-menu-item :index="CONSTANT.AUTH_ROLE.READ_WRITE">Read-Write</el-menu-item>
                    <el-menu-item :index="CONSTANT.AUTH_ROLE.READ">Read-Only</el-menu-item>
                </el-menu>
            </el-aside>
            <el-container>
                <el-header class="member-header">{{dialog.project.name}} Member</el-header>
                <el-main>
                    <el-table :data="projectUserList"
                              :header-cell-style="{color:'#44B549','font-weight':'bold'}">
                        <el-table-column label="user" :formatter="formatUser"/>
                        <el-table-column label="role" width="200">
                            <template slot-scope="scope">
                                <el-select :value="scope.row.userType+''"
                                           @change="(selectedValue)=>changeUserType(selectedValue,scope.row)">
                                    <el-option v-for="key in Object.keys(CONSTANT.PROJECT_USER_TYPE)"
                                               :key="key" :label="CONSTANT.PROJECT_USER_TYPE[key]" :value="key"/>
                                </el-select>
                            </template>
                        </el-table-column>
                        <el-table-column>
                            <template slot="header">
                                <el-button size="mini" type="success" @click="setProjectUser">set Project User
                                </el-button>
                            </template>
                            <template slot-scope="scope">
                                <el-button size="mini" type="danger" @click.native.stop="del(scope.row)">Delete
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-main>
            </el-container>
        </el-container>
        <select-user-dialog :dialog="selectUserDialog" ref="selectUserDialog" @flush="findProjectUser"/>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import SelectUserDialog from "../selectUser/selectUserDialog";

    export default {
        name: 'editAuthDialog',
        components: {SelectUserDialog},
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        project: Object,
                    }
                }
            },
        },
        data() {
            return {
                CONSTANT,
                projectUserQuery: {
                    projectId: '',
                    userType: CONSTANT.AUTH_ROLE.ALL,
                },
                projectUserList: [],
                selectUserDialog: {
                    show: false,
                    url: CONSTANT.REQUEST_URL.PROJECT_ASSIGN,
                },
            }
        },
        computed: {},
        methods: {
            selectRole(index) {
                this.projectUserQuery.userType = index;
                this.findProjectUser();
            },
            formatUser(user) {
                return UTILS.formatStr(CONSTANT.CONFIG.USER_SHOW_STYLE, user);
            },
            findProjectUser() {
                this.projectUserQuery.projectId = this.dialog.project.id;
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_FIND_PROJECT_USER, this.projectUserQuery).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.projectUserList = resp.data.data;
                    }
                });
            },
            setProjectUser() {
                this.selectUserDialog.project = this.dialog.project;
                this.selectUserDialog.show = true;
                this.$refs['selectUserDialog'].findPage();

            },
            del(row) {
                this.projectUserQuery.projectId = this.dialog.project.id;
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_EDIT_PROJECT_USER, {
                    projectId: this.dialog.project.id,
                    id: row.projectUserId,
                    isDel: CONSTANT.IS.YES,
                }).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.findProjectUser();
                    }
                });
            },
            changeUserType(userType, row) {
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_EDIT_PROJECT_USER, {
                    projectId: this.dialog.project.id,
                    id: row.projectUserId,
                    userType: userType
                }).then(resp => {
                    UTILS.showResult(this, resp, function (obj) {
                        obj.findProjectUser();
                    }, false);
                });
            },
            init() {
                this.projectUserQuery.userType = CONSTANT.AUTH_ROLE.ALL;
                this.findProjectUser();
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #auth-dialog
        .el-dialog__header
            padding 0

        .el-dialog__body
            padding 0

        .aside-header
            line-height 60px
            border-bottom 2px solid #d9d9d9
            color #44B549
            font-weight bold

        .auth-aside
            min-width 34px
            text-align left
            margin-left 1px
            box-shadow 1px 0 1px #bdbdbd
            border 1px solid #d9d9d9

            .reset-padding-left
                padding-left 6px !important

                .el-tooltip
                    padding-left 6px !important

            .el-menu-item.reset-padding-left.is-active
                background-color #e5e7e8

        .member-header
            text-align center
            line-height 60px
            border-bottom 2px solid #d9d9d9
            color #44B549
            font-weight bold
</style>
