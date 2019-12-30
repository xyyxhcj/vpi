<template>
    <div>
        <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
                   :close-on-click-modal="false">
            <el-form :model="form" :rules="form_rules" label-width="100px" ref="form"
                     style="margin:10px 60px 10px 0;width:auto">
                <el-form-item label="name" label-width="100px" prop="itemKey">
                    <el-input autocomplete="off" v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="desc" label-width="100px" prop="itemValue">
                    <el-input autocomplete="off" v-model="form.desc"></el-input>
                </el-form-item>
                <el-form-item label="frontUri" label-width="100px">
                    <el-input autocomplete="off" type="number" v-model="form.frontUri"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="dialog.show = false" round>Cancel</el-button>
                <el-button @click="submitForm" type="primary" round>Submit</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'envConfigEditDialog',
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
                        {required: true, message: '名称不能为空'}
                    ],
                }
            }
        },
        methods: {
            submitForm() {
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
