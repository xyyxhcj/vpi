<template>
    <div id="api-edit-container">
        <el-form :model="form" label-position="left" label-width="80px" ref="form" :rules="form_rules">
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
                <el-input v-model="form.apiUri" size="mini" style="width: 92%"/>
            </el-form-item>
            <el-form-item label="name" size="mini" prop="name">
                <el-input v-model="form.name" size="mini"/>
            </el-form-item>
        </el-form>
        <el-tabs type="card" :value="reqDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Request Header">
                <api-headers :data-list="form.requestHeaders" ref="reqHeaders" :config="{onlyRead:false,refPre:'req'}"/>
            </el-tab-pane>
            <el-tab-pane label="Request Param" name="requestParam">
                <div style="text-align: left;margin-left: 15px">
                    <el-radio v-model.trim="form.requestParamType" :label="0" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="form.requestParamType" :label="1" size="mini" disabled>
                        {{CONSTANT.REQUEST_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :show-list="reqShowDataList" :entity="form.requestParamDto"
                                ref="reqDataStructure" :config="{refPre:'editApiReq'}"/>
            </el-tab-pane>
        </el-tabs>
        <el-tabs type="card" :value="respDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Response Header">
                <api-headers :data-list="form.responseHeaders" ref="respHeaders"
                             :config="{onlyRead:false,refPre:'resp'}"/>
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
                <data-structure :show-list="respShowDataList" :entity="form.responseParamDto"
                                ref="respDataStructure" v-if="form.responseParamType===0"
                                :config="{refPre:'editApiResp'}"/>
                <el-input type="textarea" placeholder="remark" v-model="form.responseParamDto.remark" v-else/>
            </el-tab-pane>
        </el-tabs>
        <el-row style="line-height: 25px">
            <el-col :span="12">
                <div style="color: #44B549">Success Example</div>
                <el-input type="textarea" :rows="5" v-model="form.apiSuccessMock"/>
            </el-col>
            <el-col :span="12">
                <div style="color: #F56C6C">Failure Example</div>
                <el-input type="textarea" :rows="5" v-model="form.apiFailureMock"/>
            </el-col>
        </el-row>
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
                    groupId: this.$route.query.groupId,
                    name: '',
                    apiUri: '/',
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
                    apiSuccessMock: '',
                    apiFailureMock: '',
                },
                reqDefaultCard: 'requestParam',
                respDefaultCard: 'responseParam',
                reqShowDataList: [],
                respShowDataList: [],
                form_rules: {
                    name: [
                        {required: true, message: 'enter name'}
                    ],
                }
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
                            if (this.form.reqParamIsReference) {
                                this.form.requestParamDto.reference = true;
                            }
                            if (this.form.respParamIsReference) {
                                this.form.responseParamDto.reference = true;
                            }
                            UTILS.fillShowList(this.form.requestParamDto.dataList, this.reqShowDataList,
                                this.form.requestParamDto.reference);
                            UTILS.fillShowList(this.form.responseParamDto.dataList, this.respShowDataList,
                                this.form.responseParamDto.reference);
                            this.$nextTick(() => {
                                if (this.form.requestHeaders.length === 0) {
                                    this.$refs['reqHeaders'].init();
                                }
                                if (this.form.responseHeaders.length === 0) {
                                    this.$refs['respHeaders'].init();
                                }
                            });

                            if (this.$route.query.copy) {
                                // if copy api
                                this.form.id = null;
                                if (!this.form.reqParamIsReference) {
                                    // if no all reference
                                    this.form.requestParamDto.id = null;
                                    this.reqShowDataList.forEach(item => {
                                        if (item.parent &&
                                            (item.parent.reference ||
                                                (item.parent.referenceStructureId && item.parent.referenceStructureId !== ''))) {
                                            // parent is reference
                                            item.reference = true;
                                        } else {
                                            item.id = null;
                                            item.structureId = null;
                                        }
                                    });
                                }
                                if (!this.form.respParamIsReference) {
                                    // if no all reference
                                    this.form.responseParamDto.id = null;
                                    this.respShowDataList.forEach(item => {
                                        if (item.parent &&
                                            (item.parent.reference ||
                                                (item.parent.referenceStructureId && item.parent.referenceStructureId !== ''))) {
                                            // parent is reference
                                            item.reference = true;
                                        } else {
                                            item.id = null;
                                            item.structureId = null;
                                        }
                                    });
                                }
                                this.form.requestHeaders.forEach(header => {
                                    header.id = null;
                                    header.apiId = null;
                                });
                                this.form.responseHeaders.forEach(header => {
                                    header.id = null;
                                    header.apiId = null;
                                });
                            }
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
                    let url;
                    if (this.form.id) {
                        url = CONSTANT.REQUEST_URL.API_EDIT;
                    } else {
                        url = CONSTANT.REQUEST_URL.API_ADD;
                    }
                    this.$axios.post(url, this.form).then(resp => {
                        if (UTILS.checkResp(resp)) {
                            this.$router.push({
                                path: '/api/detail',
                                query: {id: resp.data.data}
                            });
                        }
                    });
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
