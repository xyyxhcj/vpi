<template>
    <div id="api-edit-container">
        <el-form :model="form" label-position="left" label-width="80px" ref="form">
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
        <el-tabs type="card" :value="reqDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Request Header">
                <api-headers :data-list="form.requestHeaders" ref="reqHeaders"/>
            </el-tab-pane>
            <el-tab-pane label="Request Param" name="requestParam">
                <div style="text-align: left;margin-left: 15px">
                    <el-radio v-model.trim="form.requestParamType" :label="0" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="form.requestParamType" :label="1" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :data-list="reqShowDataList" :root-list="form.requestParamDto.dataList"
                                ref="reqDataStructure" :config="{refPre:'editApiReq'}"/>
            </el-tab-pane>
        </el-tabs>
        <el-tabs type="card" :value="respDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Response Header">
                <api-headers :data-list="form.responseHeaders" ref="respHeaders"/>
            </el-tab-pane>
            <el-tab-pane label="Response Param" name="responseParam">
                <div style="text-align: left;margin:0 0 10px 15px">
                    <el-radio v-model.trim="form.responseParamType" :label="0" size="mini">
                        {{CONSTANT.RESPONSE_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="form.responseParamType" :label="1" size="mini">
                        {{CONSTANT.RESPONSE_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :data-list="respShowDataList" :root-list="form.responseParamDto.dataList"
                                ref="respDataStructure" v-if="form.responseParamType===0"
                                :config="{refPre:'editApiResp'}"/>
                <el-input type="textarea" placeholder="remark" v-model="form.responseParamDto.remark" v-else/>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";
    import DataStructure from "../../components/dataStructure/dataStructure";
    import ApiHeaders from "./components/apiHeaders";

    export default {
        name: 'edit',
        components: {ApiHeaders, DataStructure},
        data() {
            return {
                CONSTANT,
                form: {
                    projectId: this.$store.getters.selectedProjectId,
                    name: '',
                    path: '/',
                    type: '',
                    apiRequestType: 0,
                    apiStatus: 0,
                    requestParamType: 0,
                    requestParamDto: {
                        dataList: [],
                    },
                    responseParamType: 0,
                    responseParamDto: {
                        dataList: [],
                    },
                    requestHeaders: [],
                    responseHeaders: [],
                },
                reqDefaultCard: 'requestParam',
                respDefaultCard: 'responseParam',
                reqShowDataList: [],
                respShowDataList: [],
            }
        },
        methods: {
            init() {
                if (this.$route.query.id !== undefined) {
                    this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.id}).then(resp => {
                        if (UTILS.checkResp(resp)) {
                            this.form = resp.data.data;
                            this.form.requestParamDto = this.form.requestParamVo;
                            this.form.responseParamDto = this.form.responseParamVo;
                            if (this.form.requestParamDto) {
                                UTILS.fillShowDataList(this.form.requestParamDto.dataList, this.reqShowDataList);
                            } else {
                                this.reqShowDataList = [JSON.parse(JSON.stringify(CONSTANT.ITEM_TEMPLATE))];
                                this.form.requestParamDto = {dataList: this.reqShowDataList};
                            }
                            if (this.form.responseParamDto) {
                                UTILS.fillShowDataList(this.form.responseParamDto.dataList, this.respShowDataList);
                            } else {
                                this.respShowDataList = [JSON.parse(JSON.stringify(CONSTANT.ITEM_TEMPLATE))];
                                this.form.responseParamDto = {dataList: this.respShowDataList};
                            }
                            this.$nextTick(() => {
                                if (this.form.requestHeaders.length === 0) {
                                    this.$refs['reqHeaders'].init();
                                }
                                if (this.form.responseHeaders.length === 0) {
                                    this.$refs['respHeaders'].init();
                                }
                            });
                        }
                    });
                } else {
                    this.rootList = [];
                    this.$nextTick(() => {
                        this.$refs['reqDataStructure'].init();
                        this.$refs['respDataStructure'].init();
                        this.$refs['reqHeaders'].init();
                        this.$refs['respHeaders'].init();
                    });
                }
            },
            checkParam() {
                let paramIsEmpty = false;
                let checkParamKey = item => {
                    if (item.paramKey === '' && (item.paramDesc !== '' || item.value !== '')) {
                        paramIsEmpty = true;
                        item.paramKeyIsEmpty = true;
                    }
                };
                this.reqShowDataList.forEach(checkParamKey);
                this.respShowDataList.forEach(checkParamKey);
                let checkName = item => {
                    if (item.name === '' && (item.desc !== '' || item.value !== '')) {
                        paramIsEmpty = true;
                        item.nameIsEmpty = true;
                    }
                };
                this.form.requestHeaders.forEach(checkName);
                this.form.responseHeaders.forEach(checkName);
                return paramIsEmpty;
            },
            filterEmptyHeader(headers) {
                for (let i = headers.length - 1; i >= 0; i--) {
                    let item = headers[i];
                    if (item.name === '') {
                        headers.splice(i, 1);
                    }
                }
            },
            save() {
                console.log(this.form);
                this.$refs['form'].validate((valid) => {
                    let checkParam = this.checkParam();
                    if (!valid || checkParam) {
                        this.$message.error('params lose');
                        return;
                    }
                    let replacerParent = function (key, value) {
                        return key === 'parent' ? null : value;
                    };
                    let copyRootList = JSON.parse(JSON.stringify(this.form.requestParamDto.dataList, replacerParent));
                    UTILS.filterEmptyParams(copyRootList);
                    this.form.requestParamDto.dataList = copyRootList;
                    copyRootList = JSON.parse(JSON.stringify(this.form.responseParamDto.dataList, replacerParent));
                    UTILS.filterEmptyParams(copyRootList);
                    this.form.responseParamDto.dataList = copyRootList;
                    this.filterEmptyHeader(this.form.requestHeaders);
                    this.filterEmptyHeader(this.form.responseHeaders);
                    if (this.form.id) {
                        this.$axios.post(CONSTANT.REQUEST_URL.API_EDIT, this.form).then(resp => {
                            UTILS.showResult(this, resp, function (obj) {
                                // todo obj.$emit('flush');
                            }, false);
                        });
                    } else {
                        this.$axios.post(CONSTANT.REQUEST_URL.API_ADD, this.form).then(resp => {
                            UTILS.showResult(this, resp, function (obj) {
                                // todo obj.$emit('flush');
                            }, false);
                        });
                    }
                });

            }
        },
        mounted() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>

</style>
