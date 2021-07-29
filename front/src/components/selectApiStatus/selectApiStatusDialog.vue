<template>
    <el-dialog id="selectApiStatusDialog" :append-to-body="true" title="Edit Api Status" :visible.sync="dialog.show"
               :close-on-click-modal="false" width="665px" destroy-on-close>
        <el-radio-group size="small" v-model="dialog.apiStatus">
            <el-row class="row success">
                <el-radio-button :label="0">{{CONSTANT.API_STATUS[0]}}</el-radio-button>
            </el-row>
            <el-row class="row warning">
                <el-radio-button :label="7">{{CONSTANT.API_STATUS[7]}}</el-radio-button>
                <el-radio-button :label="6">{{CONSTANT.API_STATUS[6]}}</el-radio-button>
                <el-radio-button :label="1">{{CONSTANT.API_STATUS[1]}}</el-radio-button>
                <el-radio-button :label="5">{{CONSTANT.API_STATUS[5]}}</el-radio-button>
                <el-radio-button :label="4">{{CONSTANT.API_STATUS[4]}}</el-radio-button>
                <el-radio-button :label="3">{{CONSTANT.API_STATUS[3]}}</el-radio-button>
            </el-row>
            <el-row class="row error">
                <el-radio-button :label="8">{{CONSTANT.API_STATUS[8]}}</el-radio-button>
                <el-radio-button :label="2">{{CONSTANT.API_STATUS[2]}}</el-radio-button>
            </el-row>
        </el-radio-group>
        <div slot="footer" style="text-align: left">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="submitForm" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";

export default {
        name: 'selectApiStatusDialog',
        props: {
            dialog: {
                default() {
                    return {
                        show: false,
                        ids: [],
                        projectId: '',
                        apiStatus: undefined,
                    };
                }
            }
        },
        data() {
            return {
                CONSTANT,
                apiStatus: undefined,
            }
        },
        methods: {
            submitForm() {
                this.$axios.post(CONSTANT.REQUEST_URL.API_SWITCH_STATUS, this.dialog).then(resp => {
                    UTILS.showResult(this, resp,()=>{
                        this.$emit('flush');
                    });
                });
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #selectApiStatusDialog
        .row
            padding 5px

            .el-radio-button__inner
                width 100px

            .is-active
                .el-radio-button__inner
                    border-radius 0

        .success .el-radio-button
            border 1px solid #44B549

        .warning .el-radio-button
            border 1px solid #E6A23C

        .error .el-radio-button
            border 1px solid #F56C6C


</style>
