<template>
    <el-dialog
            title="Login" :visible.sync="dialog.show" width="30%"
            :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
            center>
        <el-form :model="user" status-icon ref="form" :rules="loginRules" label-width="100px">
            <el-form-item label="loginName" prop="loginName">
                <el-input v-model="user.loginName"/>
            </el-form-item>
            <el-form-item label="password" prop="password">
                <el-input type="password" v-model="user.password" autocomplete="off" @keyup.enter.native="submitForm"/>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="resetForm">Reset</el-button>
            <el-button type="primary" @click="submitForm">Login</el-button>
        </span>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {utils} from "../../common/js/utils";

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
                    password: ''
                },
                loginRules: {
                    loginName: [
                        {required: true, message: 'enter loginName', trigger: 'blur'},
                    ],
                    password: [
                        {required: true, message: 'enter password', trigger: 'blur'}
                    ]
                },
            }
        },
        methods: {
            resetForm() {
                this.$refs['form'].resetFields();
            },
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        this.$axios.post(CONSTANT.REQUEST_URL.LOGIN, this.user).then(resp => {
                            if (utils.checkResp(resp)) {
                                this.logined(resp);
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
            },
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
