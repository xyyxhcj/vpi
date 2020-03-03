<template>
    <el-container id="app">
        <left-menu v-if="selectedProjectId"/>
        <el-container>
            <el-header class="header-common">
                <el-row>
                    <el-col :span="10">
                        <div>
                            <div class="project-path">
                                <span style="margin-right: 15px">
                                    <template v-for="(path,index) in pathList">
                                        {{path}}
                                        <template v-if="index!==pathList.length-1">></template>
                                    </template>
                                </span>
                                <el-link v-if="$route.path.startsWith('/api/')" icon="el-icon-refresh-left"
                                         @click="$router.push('/apiDoc')">Go Back
                                </el-link>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="4">
                        <div class="project-name">
                            <el-dropdown @command="changeProject" v-if="selectedProjectName">
                                <span class="el-dropdown-link">
                                    {{selectedProjectName}}
                                    <i class="el-icon-arrow-down el-icon--right"/>
                                </span>
                                <el-dropdown-menu>
                                    <el-dropdown-item>switch project</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                            <el-button type="text" style="padding: 0;color: red" @click="changeProject" v-else>
                                choose project
                            </el-button>
                        </div>
                    </el-col>
                    <el-col :span="10">
                        <div class="user-info">
                            <span class="add-user-icon" v-if="isAdmin">
                                <el-button type="success" icon="el-icon-plus" circle size="mini" @click="addUser"/>
                                <el-button type="danger" icon="el-icon-minus" circle size="mini" @click="delUser"/>
                            </span>
                            <el-dropdown @command="userCommand">
                                <span class="el-dropdown-link">
                                    {{user?user.loginName:''}}
                                    <i class="el-icon-arrow-down el-icon--right" style="cursor:pointer"/>
                                </span>
                                <el-dropdown-menu>
                                    <el-dropdown-item :command="modify">modify</el-dropdown-item>
                                    <el-dropdown-item :command="loginOut">login out</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                            <i class="el-icon-help" style="margin-left:15px;color:#409EFF;cursor:pointer"
                               @click="help"/>
                        </div>
                    </el-col>
                </el-row>
            </el-header>
            <el-main class="main-common">
                <router-view/>
            </el-main>
        </el-container>
        <login-dialog :dialog="loginDialog"/>
        <edit-user-dialog :dialog="editUserDialog"/>
        <edit-password-dialog :dialog="editPwdDialog"/>
        <select-user-dialog :dialog="selectUserDialog" ref="selectUserDialog"/>
        <span style="visibility: hidden" id="vpi-plugin-loaded"></span>
    </el-container>
</template>

<script>

    import leftMenu from "./components/leftMenu/leftMenu";
    import LoginDialog from "./components/login/loginDialog";
    import EditUserDialog from "./components/editUser/editUserDialog";
    import {CONSTANT} from "./common/js/constant";
    import {UTILS} from "./common/js/utils";
    import EditPasswordDialog from "./components/editUser/editPasswordDialog";
    import SelectUserDialog from "./components/selectUser/selectUserDialog";

    export default {
        name: 'app',
        components: {SelectUserDialog, EditPasswordDialog, EditUserDialog, LoginDialog, leftMenu},
        data() {
            return {
                CONSTANT,
                editUserDialog: {
                    show: false,
                    title: '',
                    url: '',
                },
                editPwdDialog: {
                    show: false,
                    title: '',
                    url: '',
                },
                user: this.$store.getters.user,
                selectUserDialog: {
                    show: false,
                    url: CONSTANT.REQUEST_URL.USER_REMOVE,
                    forDel: true,
                },
            }
        },
        methods: {
            help() {
                if (this.$route.path !== '/help') {
                    this.$router.push('/help');
                }
            },
            changeProject() {
                this.$router.push('/');
            },
            addUser() {
                this.editUserDialog = {
                    show: true,
                    title: 'add user',
                    url: CONSTANT.REQUEST_URL.USER_ADD,
                };
            },
            userCommand(command) {
                command();
            },
            loginOut() {
                this.$axios.post(CONSTANT.REQUEST_URL.LOGIN_OUT).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.$store.dispatch('loginOut');
                        this.$router.go(0);
                    }
                });
            },
            modify() {
                this.editPwdDialog = {
                    show: true,
                    title: 'modify password',
                    url: CONSTANT.REQUEST_URL.USER_UPDATE,
                };
            },
            delUser() {
                this.selectUserDialog.show = true;
                this.$refs['selectUserDialog'].findPageForDelete();
            }
        },
        computed: {
            loginDialog: {
                get() {
                    return {
                        show: this.$route.path !== '/help' && !this.$store.getters.loginAuth,
                    }
                },
            },
            selectedProjectId: {
                get() {
                    return this.$store.getters.selectedProjectId;
                },
            },
            selectedProjectName: {
                get() {
                    return this.$store.getters.selectedProjectName;
                },
            },
            isAdmin: {
                get() {
                    return this.$store.getters.user && CONSTANT.CONFIG.ADMIN_LOGIN_NAME === this.$store.getters.user.loginName;
                },
            },
            pathList: {
                get() {
                    return this.$route.name ? this.$route.name.split('/') : [];
                }
            }
        },
        created() {
        }
    }
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #app
        font-family 'Source Han Sans CN', Helvetica, Arial, sans-serif
        -webkit-font-smoothing antialiased
        -moz-osx-font-smoothing grayscale
        height 100%
        border 1px solid #eee
        text-align center
        color #2c3e50

    .header-common
        max-height 40px
        padding 5px 20px
        border-bottom 1px solid #d9d9d9
        font-size 14px
        background-color white

        .project-path
            line-height 20px
            margin 4px
            text-align left

        .project-name
            line-height 28px

            .el-dropdown-link
                cursor: pointer
                color: #409EFF

            .el-icon-arrow-down
                font-size: 12px

        .user-info
            line-height 28px
            text-align right

            .add-user-icon
                padding-right 10px
                color green

    .main-common
        padding 9px
        background-color rgb(245, 245, 245)

</style>

