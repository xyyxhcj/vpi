<template>
    <div id="api-test-container" style="min-width: 1250px;line-height: 15px">
        <el-row>
            <el-col :span="24" style="text-align: left">
                <el-select v-model.trim="api.apiRequestType" style="width: 8%" size="mini">
                    <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
                    <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
                </el-select>
                <el-input v-model="api.apiUri" size="mini" style="width: 92%">
                    <el-input slot="prepend" v-model="selectedEnv.frontUri" size="mini"
                              style="padding:0;width: 90px" v-if="selectedEnv"/>
                </el-input>
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
                <data-structure :data-list="reqShowDataList" :root-list="api.requestParamVo.dataList"
                                ref="reqDataStructure" :config="{test:true}"/>
            </el-tab-pane>
        </el-tabs>
        <div style="text-align: left;margin: 10px">
            <el-dropdown size="small" split-button type="success" @command="command" @click="send">
                Send
                <el-dropdown-menu>
                    <el-dropdown-item :command="newTab">New Tab</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <el-tabs type="card" style="line-height: 25px">
            <el-tab-pane label="Response Info">

            </el-tab-pane>
            <el-tab-pane label="Request Info">
            </el-tab-pane>
        </el-tabs>
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
                reqShowDataList: [],
                respShowDataList: [],
                responseInfo: {},
                requestInfo: {},
                selectedEnv: this.$route.query.selectedEnv,
            };
        },
        methods: {
            flushEnv(env) {
                this.selectedEnv = env;
            },
            init() {
                this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.id}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.api = resp.data.data;
                        if (this.api.requestParamVo) {
                            UTILS.fillShowDataList(this.api.requestParamVo.dataList, this.reqShowDataList);
                            this.$refs['reqDataStructure'].init();
                        }
                    }
                });
            },
            send() {
                console.log('send');
            },
            command(func) {
                func();
            },
            newTab() {
                console.log('newTab');
            },
        },
        mounted() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #api-test-container
        .el-input-group__prepend
            padding 0
            border 0

            .el-input__inner
                padding 0
                background-color #f5f5f5
</style>
