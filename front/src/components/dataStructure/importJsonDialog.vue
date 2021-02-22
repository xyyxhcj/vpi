<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show" destroy-on-close
               :close-on-click-modal="false" center width="60%">
        <el-input type="textarea" :rows="15" placeholder="please input json data" v-model="jsonData">
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
                        parentLevel: -1,
                    }
                }
            },
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
                // format data
                let result = UTILS.json2ViewData(JSON.parse(this.jsonData), this.dialog.parentLevel);
                this.$emit('flush', result);
                this.dialog.show = false;
                this.jsonData = '';
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
