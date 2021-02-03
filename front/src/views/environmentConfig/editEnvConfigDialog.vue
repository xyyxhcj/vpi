<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show" width="845px"
               :close-on-click-modal="false" destroy-on-close>
        <el-form :model="form" :rules="form_rules" label-width="100px" ref="form"
                 style="margin:10px 60px 10px 0;width:auto">
            <el-form-item label="name" label-width="100px" prop="name">
                <el-input v-model.trim="form.name"/>
            </el-form-item>
            <el-form-item label="desc" label-width="100px" prop="desc">
                <el-input v-model.trim="form.desc"/>
            </el-form-item>
            <el-form-item label="frontUri" label-width="100px">
                <el-input v-model.trim="form.frontUri"/>
            </el-form-item>
        </el-form>
        <api-headers :data-list="form.envHeaders" ref="headers" :config="{onlyRead:false}"/>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
import {UTILS} from "../../common/js/utils";
import ApiHeaders from "../../components/apiHeaders/apiHeaders";

export default {
        name: 'editEnvConfigDialog',
        components: {ApiHeaders},
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
                        {required: true, message: 'please enter name'}
                    ],
                }
            }
        },
        methods: {
            checkParam() {
                let paramIsEmpty = false;
                this.form.envHeaders.forEach(item => {
                    if (item.name === '' && (item.desc !== '' || item.value !== '')) {
                        paramIsEmpty = true;
                        item.nameIsEmpty = true;
                    }
                });
                return paramIsEmpty;
            },
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    let checkParam = this.checkParam();
                    if (!valid || checkParam) {
                        this.$message.error('params lose');
                        return;
                    }
                    let headers = [];
                    this.form.envHeaders.forEach(header => {
                        if (header.name && header.name !== '') {
                            headers.push(header);
                        }
                    });
                    this.form.envHeader = JSON.stringify(headers);
                    this.$axios.post(this.dialog.url, this.form).then(resp => {
                        UTILS.showResult(this, resp, function (obj) {
                            obj.$emit('flush');
                        });
                    });
                });
            },
            init() {
                this.$refs['headers'].init();
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
