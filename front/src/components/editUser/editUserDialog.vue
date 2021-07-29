<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show" destroy-on-close
               :close-on-click-modal="false" center width="35%" style="min-width: 900px">
        <el-form :model="form" ref="form" label-width="140px" :rules="form_rules">
            <el-form-item label="Login name" prop="loginName">
                <el-input v-model.trim="form.loginName"/>
            </el-form-item>
            <el-form-item label="Password" prop="password">
                <el-input v-model.trim="form.password" type="password"/>
            </el-form-item>
            <el-form-item label="Retype password" prop="rePassword">
                <el-input v-model.trim="form.rePassword" type="password"/>
            </el-form-item>
            <el-form-item label="User name" prop="userName">
                <el-input v-model.trim="form.userName"/>
            </el-form-item>
            <el-form-item label="Phone" prop="phone">
                <el-input v-model.trim="form.phone"/>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
import {UTILS} from "@/common/js/utils";

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
                    callback(new Error('retype password error'));
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
                        {required: true, validator: validateRePassword, trigger: 'blur'},
                    ],
                }
            };
        },
        methods: {
            submitForm() {
                let form = this.$refs['form'];
                form.validate((valid) => {
                    if (valid) {
                        this.$axios.post(this.dialog.url, this.form).then(resp => {
                            UTILS.showResult(this, resp, () => form.resetFields());
                        });
                    }
                });
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
