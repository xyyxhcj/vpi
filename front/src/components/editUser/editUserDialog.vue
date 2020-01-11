<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="35%">
        <el-form :model="form" ref="form" label-width="120px" :rules="form_rules">
            <el-form-item label="loginName" prop="loginName">
                <el-input autocomplete="off" v-model="form.loginName"/>
            </el-form-item>
            <el-form-item label="password" prop="password">
                <el-input autocomplete="off" v-model="form.password" type="password"/>
            </el-form-item>
            <el-form-item label="rePassword" prop="rePassword">
                <el-input autocomplete="off" v-model="form.rePassword" type="password"/>
            </el-form-item>
            <el-form-item label="userName" prop="userName">
                <el-input autocomplete="off" v-model="form.userName"/>
            </el-form-item>
            <el-form-item label="phone" prop="phone">
                <el-input autocomplete="off" v-model="form.phone"/>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'editUserDialog',
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
            form: {
                type: Object,
                default() {
                    return {
                        loginName: '',
                        password: '',
                        rePassword: '',
                        userName: '',
                        phone: '',
                    }
                }
            }
        },
        data() {
            let validateRePassword = (rule, value, callback) => {
                if (value !== this.form.password) {
                    callback(new Error('re-password error'));
                } else {
                    callback();
                }
            };
            return {
                form_rules: {
                    loginName: [
                        {required: true, message: 'enter loginName'}
                    ],
                    password: [
                        {required: true, message: 'enter password'}
                    ],
                    rePassword: [
                        {validator: validateRePassword, trigger: 'blur'},
                    ],
                }
            };
        },
        methods: {
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        this.$axios.post(this.dialog.url, this.form).then(resp => {
                            UTILS.showResult(this, resp);
                        });
                    }
                });
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
