<template>
    <el-dialog
            title="Login" :visible.sync="dialog.show" width="30%" class="login-dialog"
            :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
            center>
        <el-form :model="user" status-icon ref="form" :rules="loginRules" label-width="100px">
            <el-form-item label="loginName" prop="loginName">
                <el-input v-model.trim="user.loginName"/>
            </el-form-item>
            <el-form-item label="password" prop="password" style="height: 25px">
                <el-input type="password" v-model.trim="user.password" @keyup.enter.native="submitForm"/>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-checkbox-group v-model="checkList" style="line-height: 30px">
                <el-checkbox label="Remember Me" @change="user.isRememberMe=!user.isRememberMe"/>
            </el-checkbox-group>
            <el-button @click="resetForm">Reset</el-button>
            <el-button type="primary" @click="submitForm">Login</el-button>
        </span>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'loginDialog',
        props: {
            dialog: {
                show: false,
            }
        },
        data() {
            return {
                user: {
                    loginName: '',
                    password: '',
                    isRememberMe: false,
                },
                loginRules: {
                    loginName: [
                        {required: true, message: 'enter loginName', trigger: 'blur'},
                    ],
                    password: [
                        {required: true, message: 'enter password', trigger: 'blur'}
                    ]
                },
                checkList: [],
            }
        },
        methods: {
            resetForm() {
                this.$refs['form'].resetFields();
                this.user = {
                    loginName: '',
                    password: '',
                    isRememberMe: false,
                }
            },
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        this.$axios.post(CONSTANT.REQUEST_URL.LOGIN, this.user).then(resp => {
                            if (UTILS.checkResp(resp)) {
                                this.logined(resp);
                                if (this.user.isRememberMe) {
                                    this.$store.dispatch('rememberInfo', this.user);
                                } else {
                                    this.$store.dispatch('rememberInfo', {});
                                }
                                this.dialog.show = false;
                            } else {
                                this.$message({
                                    message: resp.data ? resp.data.message : 'login failed',
                                    type: 'error'
                                });
                            }
                        });
                    } else {
                        return false;
                    }
                });
            },
            logined(resp) {
                let data = resp.data.data;
                this.$store.dispatch('login', data);
                this.$router.go(0);
            },
        },
        created() {
            let user = this.$store.getters.rememberInfo;
            if (user) {
                this.user = user;
                if (user.isRememberMe) {
                    this.checkList.push('Remember Me');
                }
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .login-dialog
        .el-dialog__body
            padding-bottom 0
            margin-bottom 0

        .el-dialog__footer
            padding-top 0
</style>
