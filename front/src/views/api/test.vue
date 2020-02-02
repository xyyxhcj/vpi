<template>
    <div id="api-test-container" style="min-width: 1250px;line-height: 15px">
        <el-row>
            <el-col :span="24" style="text-align: left">
                <el-select v-model.trim="api.apiRequestType" style="width: 8%" size="mini">
                    <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
                    <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
                </el-select>
                <el-input v-model="api.apiUri" size="mini" style="width: 92%"/>
            </el-col>
        </el-row>
        <div style="text-align: left;margin: 5px;line-height: 30px;">{{api.name}}</div>
        <el-tabs type="card" :value="reqDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Request Header">
                <api-headers :data-list="api.requestHeaders" ref="reqHeaders"/>
            </el-tab-pane>
            <el-tab-pane label="Request Param" name="requestParam">
                <div style="text-align: left;margin-left: 15px">
                    <el-radio v-model.trim="api.requestParamType" :label="0" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="api.requestParamType" :label="1" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <!--<data-structure :data-list="reqShowDataList" :root-list="api.requestParamDto.dataList"
                                ref="reqDataStructure" :config="{refPre:'editApiReq'}"/>-->
            </el-tab-pane>
        </el-tabs>
        <!--
        <el-tabs type="card" :value="reqDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Request Header">
                <api-headers :data-list="api.requestHeaders" ref="reqHeaders"/>
            </el-tab-pane>
            <el-tab-pane label="Request Param" name="requestParam">
                <div style="text-align: left;margin-left: 15px">
                    <el-radio v-model.trim="api.requestParamType" :label="0" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="api.requestParamType" :label="1" size="mini">
                        {{CONSTANT.REQUEST_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :data-list="reqShowDataList" :root-list="api.requestParamDto.dataList"
                                ref="reqDataStructure" :config="{refPre:'editApiReq'}"/>
            </el-tab-pane>
        </el-tabs>
        <el-tabs type="card" :value="respDefaultCard" style="line-height: 25px">
            <el-tab-pane label="Response Header">
                <api-headers :data-list="api.responseHeaders" ref="respHeaders"/>
            </el-tab-pane>
            <el-tab-pane label="Response Param" name="responseParam">
                <div style="text-align: left;margin:0 0 10px 15px">
                    <el-radio v-model.trim="api.responseParamType" :label="0" size="mini">
                        {{CONSTANT.RESPONSE_PARAM_TYPE[0]}}
                    </el-radio>
                    <el-radio v-model.trim="api.responseParamType" :label="1" size="mini">
                        {{CONSTANT.RESPONSE_PARAM_TYPE[1]}}
                    </el-radio>
                </div>
                <data-structure :data-list="respShowDataList" :root-list="api.responseParamDto.dataList"
                                ref="respDataStructure" v-if="api.responseParamType===0"
                                :config="{refPre:'editApiResp'}"/>
                <el-input type="textarea" placeholder="remark" v-model="api.responseParamDto.remark" v-else/>
            </el-tab-pane>
        </el-tabs>
        <el-row style="line-height: 25px">
            <el-col :span="12">
                <div style="color: #44B549">Success Example</div>
                <el-input type="textarea" :rows="5" v-model="api.apiSuccessMock"/>
            </el-col>
            <el-col :span="12">
                <div style="color: #F56C6C">Failure Example</div>
                <el-input type="textarea" :rows="5" v-model="api.apiFailureMock"/>
            </el-col>
        </el-row>-->
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import ApiHeaders from "./components/apiHeaders";
    import DataStructure from "../../components/dataStructure/dataStructure";

    export default {
        name: 'test',
        components: {DataStructure, ApiHeaders},
        data() {
            return {
                CONSTANT,
                UTILS,
                api: {
                    projectId: this.$store.getters.selectedProjectId,
                    name: '',
                    apiUri: '/',
                    type: '',
                    apiRequestType: 0,
                    apiStatus: 0,
                    requestParamType: 0,
                    requestParamVo: {
                        dataList: [],
                    },
                    responseParamType: 0,
                    responseParamVo: {
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
            };
        },
        methods: {
            init() {
                this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.id}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.form = resp.data.data;
                        this.api.requestParamDto = this.api.requestParamVo;
                        this.api.responseParamDto = this.api.responseParamVo;
                        if (this.api.requestParamDto) {
                            UTILS.fillShowDataList(this.api.requestParamDto.dataList, this.reqShowDataList);
                        } else {
                            this.reqShowDataList = [JSON.parse(JSON.stringify(CONSTANT.ITEM_TEMPLATE))];
                            this.api.requestParamDto = {dataList: this.reqShowDataList};
                        }
                        if (this.api.responseParamDto) {
                            UTILS.fillShowDataList(this.api.responseParamDto.dataList, this.respShowDataList);
                        } else {
                            this.respShowDataList = [JSON.parse(JSON.stringify(CONSTANT.ITEM_TEMPLATE))];
                            this.api.responseParamDto = {dataList: this.respShowDataList};
                        }
                        /*this.$nextTick(() => {
                            if (this.api.requestHeaders.length === 0) {
                                this.$refs['reqHeaders'].init();
                            }
                            if (this.api.responseHeaders.length === 0) {
                                this.$refs['respHeaders'].init();
                            }
                        });*/
                    }
                });
            },
        },
        mounted() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
