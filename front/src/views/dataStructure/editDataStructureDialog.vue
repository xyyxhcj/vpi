<template>
    <el-dialog id="editDataStructureDialog" :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" width="90%">
        <el-form :model="form" :rules="form_rules" label-width="100px" ref="form"
                 style="margin:10px 60px 10px 0;width:auto">
            <el-form-item label="name" label-width="100px" prop="name">
                <el-input v-model="form.name" size="mini"/>
            </el-form-item>
            <el-form-item label="remark" label-width="100px" prop="remark">
                <el-input v-model="form.remark" size="mini"/>
            </el-form-item>
        </el-form>
        <data-structure :show-list="dataList" :entity="form" ref="dataStructure"
                        :config="{refPre:'editDataStructure',onlyRead:dialog.onlyRead}"/>
        <div slot="footer" v-if="!dialog.onlyRead">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";
    import DataStructure from "../../components/dataStructure/dataStructure";

    export default {
        name: 'editDataStructureDialog',
        components: {DataStructure},
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        url: '',
                        onlyRead: false,
                    }
                }
            },
            form: Object,
        },
        data() {
            return {
                CONSTANT,
                form_rules: {
                    name: [
                        {required: true, message: 'please enter name'}
                    ],
                },
                dataList: [],
            };
        },
        methods: {
            checkParamKey() {
                let paramKeyIsEmpty = false;
                this.dataList.forEach(item => {
                    if (item.paramKey === '' && (item.paramDesc !== '' || item.value !== '')) {
                        paramKeyIsEmpty = true;
                        item.paramKeyIsEmpty = true;
                    }
                });
                return paramKeyIsEmpty;
            },
            submitForm() {
                this.$refs['form'].validate((valid) => {
                    let checkParam = this.checkParamKey();
                    if (!valid || checkParam) {
                        this.$message.error('params lose');
                        return;
                    }
                    let copyRootList = JSON.parse(JSON.stringify(this.form.dataList, (key, value) => key === 'parent' ? null : value));
                    UTILS.filterEmptyParams(copyRootList);
                    this.form.dataList = copyRootList;
                    this.$axios.post(this.dialog.url, this.form).then(resp => {
                        UTILS.showResult(this, resp, function (obj) {
                            obj.$emit('flush');
                        });
                    });
                });
            },
            init() {
                if (this.form.id !== undefined) {
                    UTILS.fillShowList(this.form.dataList, this.dataList);
                } else {
                    this.$nextTick(() => {
                        this.$refs['dataStructure'].init();
                    });
                }
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #editDataStructureDialog
        min-width 1300px

</style>
