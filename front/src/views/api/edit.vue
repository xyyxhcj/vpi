<template>
    <div id="api-edit-container">
        <el-form :model="form" label-position="left" label-width="80px">
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
        <el-tabs type="card" :value="defaultCard" style="line-height: 25px">
            <el-tab-pane label="Header">头部</el-tab-pane>
            <el-tab-pane label="Request Param" name="requestParam">
                <div style="text-align: left;margin-left: 15px">
                    <el-radio v-model.trim="form.requestParamType" :label="0" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="form.requestParamType" :label="1" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :data-list="showDataList" :root-list="form.requestParamDto.dataList" ref="dataStructure"/>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import DataStructure from "../../components/dataStructure/dataStructure";

    export default {
        name: 'edit',
        components: {DataStructure},
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
                        requestParamType: 0,
                        requestParamDto: {
                            dataList: [],
                        },
                        dataList: []
                    }
                }
            }
        },
        data() {
            return {
                CONSTANT,
                defaultCard: 'requestParam',
                showDataList: [],
            }
        },
        methods: {
            init() {
                if (this.form.id !== undefined) {
                    /*this.rootList = this.form.dataList;
                    let stack = this.rootList.slice();
                    while (stack.length > 0) {
                        let pop = stack.shift();
                        pop.paramKeyIsEmpty = false;
                        pop.show = true;
                        if (!pop.parent) {
                            pop.level = 0;
                        } else {
                            pop.level = pop.parent.level + 1;
                        }
                        this.dataList.push(pop);
                        if (pop.subList.length > 0) {
                            for (let i = pop.subList.length - 1; i >= 0; i--) {
                                let item = pop.subList[i];
                                item.parent = pop;
                                stack.splice(0, 0, item);
                            }
                        }
                    }
                    let item = JSON.parse(this.itemTemplateStr);
                    this.dataList.push(item);
                    this.rootList.push(item);*/
                } else {
                    this.rootList = [];
                    this.$nextTick(() => {
                        this.$refs['dataStructure'].init();
                    });
                }
            }
        },
        mounted() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>

</style>
