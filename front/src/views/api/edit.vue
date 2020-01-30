<template>
    <el-form :model="form" label-position="left" label-width="80px" style="background-color: #f5f5f5;">
        <el-form-item label="apiStatus" size="mini" style="text-align: left">
            <el-radio v-model.trim="form.apiStatus" :label="0" size="mini">{{CONSTANT.API_STATUS[0]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="1" size="mini">{{CONSTANT.API_STATUS[1]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="2" size="mini">{{CONSTANT.API_STATUS[2]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="3" size="mini">{{CONSTANT.API_STATUS[3]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="4" size="mini">{{CONSTANT.API_STATUS[4]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="5" size="mini">{{CONSTANT.API_STATUS[5]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="6" size="mini">{{CONSTANT.API_STATUS[6]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="7" size="mini">{{CONSTANT.API_STATUS[7]}}</el-radio>
            <el-radio v-model.trim="form.apiStatus" :label="8" size="mini">{{CONSTANT.API_STATUS[8]}}</el-radio>
        </el-form-item>
        <el-form-item label="path" size="mini">
            <el-select v-model.trim="form.apiRequestType" style="width: 8%">
                <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
                <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
            </el-select>
            <el-input v-model="form.path" size="mini" style="width: 92%"/>
        </el-form-item>
        <el-form-item label="name" size="mini">
            <el-input v-model="form.name" size="mini"/>
        </el-form-item>
    </el-form>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";

    export default {
        name: 'edit',
        props: {
            form: {
                type: Object,
                default() {
                    return {
                        name: '',
                        path: '/',
                        type: '',
                        apiRequestType: 0,
                        apiStatus: 0,
                    }
                }
            }
        },
        data() {
            return {
                CONSTANT,
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
