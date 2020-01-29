<template>
    <el-container id="app">
        <left-menu/>
        <el-container>
            <el-header class="header-common">
                <el-row>
                    <el-col :span="10">
                        <div class="project-path">{{$route.name}} > myTest</div>
                    </el-col>
                    <el-col :span="4">
                        <div class="project-name">
                            <el-dropdown @command="changeProject">
                                <span class="el-dropdown-link">
                                    {{selectedProjectName}}
                                    <i class="el-icon-arrow-down el-icon--right"/>
                                </span>
                                <el-dropdown-menu>
                                    <el-dropdown-item>switch project</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </el-col>
                    <el-col :span="10">
                        <div class="user-info">
                            <span class="add-user-icon" v-if="isAdmin">
                                <el-button type="success" icon="el-icon-plus" circle size="mini" @click="addUser"/>
                            </span>
                            <el-dropdown @command="userCommand">
                                <span class="el-dropdown-link">
                                    {{user?user.loginName:''}}
                                    <i class="el-icon-arrow-down el-icon--right"/>
                                </span>
                                <el-dropdown-menu>
                                    <el-dropdown-item :command="loginOut">loginOut</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
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
    </el-container>
</template>

<script>

    import leftMenu from "@/components/leftMenu/leftMenu";
    import LoginDialog from "./components/login/loginDialog";
    import EditUserDialog from "./components/editUser/editUserDialog";
    import {CONSTANT} from "./common/js/constant";
    import {UTILS} from "./common/js/utils";

    export default {
        name: 'app',
        components: {EditUserDialog, LoginDialog, leftMenu},
        data() {
            return {
                CONSTANT: CONSTANT,
                loginDialog: {
                    show: !this.$store.getters.loginAuth,
                },
                editUserDialog: {
                    show: false,
                    title: '',
                    url: '',
                },
                user: this.$store.getters.user,
            }
        },
        methods: {
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
        },
        computed: {
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
            margin 5px
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

