<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="35%">
        <el-input type="textarea" :rows="4" placeholder="please input json data" v-model="jsonData">
        </el-input>
        <div slot="footer">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="confirm" type="primary" round>Confirm</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'importJsonDialog',
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                    }
                }
            },
            dataList: {
                type: Array
            },
            rootList:{
                type: Array
            }
        },
        data() {
            return {
                jsonData: ''
            }
        },
        methods: {
            confirm() {
                // check json
                if (!UTILS.isJSON(this.jsonData)) {
                    this.$message.error('please input json data');
                    return;
                }
                // flush data
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
