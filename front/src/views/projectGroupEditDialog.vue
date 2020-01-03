<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="35%">
        <el-form :model="form" ref="form" label-width="100px" :rules="form_rules">
            <el-form-item label="name" prop="name">
                <el-input autocomplete="off" v-model="form.name"/>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {utils} from "../common/js/utils";

    export default {
        name: 'projectGroupEditDialog',
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
                form_rules: {
                    name: [
                        {required: true, message: 'enter name'}
                    ],
                }
            }
        },
        methods: {
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    if (valid) {
                        this.$axios.post(this.dialog.url, this.form).then(resp => {
                            utils.showResult(this, resp, function (obj) {
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
