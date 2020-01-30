<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="35%">
        <el-form :model="form" ref="form" label-width="120px" :rules="form_rules">
            <el-form-item label="name" prop="name">
                <el-input v-model.trim="form.name"/>
            </el-form-item>
            <el-form-item label="projectType" prop="projectType">
                <el-select v-model.trim="form.projectType">
                    <el-option :label="CONSTANT.PROJECT_TYPE[0]" :value="0"/>
                    <el-option :label="CONSTANT.PROJECT_TYPE[1]" :value="1"/>
                    <el-option :label="CONSTANT.PROJECT_TYPE[2]" :value="2"/>
                    <el-option :label="CONSTANT.PROJECT_TYPE[3]" :value="3"/>
                </el-select>
            </el-form-item>
            <el-form-item label="projectVersion" prop="projectVersion">
                <el-input v-model.trim="form.projectVersion"/>
            </el-form-item>
            <el-form-item label="desc" prop="desc">
                <el-input v-model.trim="form.desc"/>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../common/js/utils";
    import {CONSTANT} from "../common/js/constant";

    export default {
        name: 'editProjectDialog',
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
            form: Object
        },
        data() {
            return {
                CONSTANT,
                form_rules: {
                    name: [
                        {required: true, message: 'enter name'}
                    ],
                }
            };
        },
        methods: {
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        this.$axios.post(this.dialog.url, this.form).then(resp => {
                            UTILS.showResult(this, resp, function (obj) {
                                obj.$emit('flush');
                            });
                        });
                    }
                });
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
