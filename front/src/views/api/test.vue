<template>
    <div id="api-test-container" style="min-width: 1250px;line-height: 15px">
        <el-row>
            <el-col :span="24" style="text-align: left">
                <el-select v-model.trim="api.apiRequestType" style="width: 8%" size="mini">
                    <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
                    <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
                </el-select>
                <el-input v-model="api.apiUri" size="mini" style="width: 91%">
                    <template slot="prepend">{{selectedEnv?selectedEnv.frontUri:''}}</template>
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
            <el-dropdown size="small" split-button type="success" @command="command"
                         @click="()=>!sendDisable?send():''">
                {{!sendDisable?'Send':'Wait...'}}
                <el-dropdown-menu>
                    <el-dropdown-item :command="newTab">New Tab</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <el-tabs type="card" class="test-info">
            <el-tab-pane label="Response Info">
                <line-text style="color: #44B549" text="Headers"/>
                <div id="resp-headers" ref="respHeaders" class="headers"></div>
                <line-text style="color: #44B549" text="Response Body"/>
                <el-dropdown size="mini" split-button type="primary" @click="saveMock(true)" @command="command"
                             v-if="$refs['respData']&&$refs['respData'].innerHTML.length>0">
                    Save Success Mock
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item :command="()=>saveMock(false)">saveFailureMock</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <pre id="resp-data" ref="respData" class="data"/>
            </el-tab-pane>
            <el-tab-pane label="Request Info">
                <line-text style="color: #44B549" text="Headers"/>
                <div id="req-headers" ref="respHeaders" class="headers"></div>
                <line-text style="color: #44B549" text="Request Body"/>
                <pre id="req-data" ref="reqData" class="data"/>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
    import ApiHeaders from "./components/apiHeaders";
    import DataStructure from "../../components/dataStructure/dataStructure";
    import LineText from "../../components/lineText/lineText";

    export default {
        name: 'test',
        components: {LineText, DataStructure, ApiHeaders},
        data() {
            return {
                CONSTANT,
                UTILS,
                api: {
                    id: '',
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
                sendDisable: false,
            };
        },
        computed: {
            respData: {
                get() {
                    return this.$refs['respData'].innerHTML;
                }
            },
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
            getParams() {
                let params = {};
                let stack = [];
                this.api.requestParamVo.dataList.forEach(data => {
                    let paramKey = data.paramKey;
                    if (paramKey !== '') {
                        let value;
                        if (data.subList.length > 0) {
                            if (data.paramType === CONSTANT.PARAM_TYPE.ARRAY) {
                                // array
                                value = [{}];
                            } else {
                                // object
                                value = {};
                            }
                            data.subList.forEach(item => stack.push({keys: [paramKey], value: item}));
                        } else {
                            value = data.value;
                        }
                        params[paramKey] = value;
                    }
                });
                while (stack.length > 0) {
                    let {keys: keys, value: pop} = stack.pop();
                    let paramKey = pop.paramKey;
                    if (paramKey !== '') {
                        let value;
                        if (pop.subList.length > 0) {
                            if (pop.paramType === CONSTANT.PARAM_TYPE.ARRAY) {
                                // array
                                value = [{}];
                            } else {
                                // object
                                value = {};
                            }
                            pop.subList.forEach(item => stack.push({
                                keys: keys.splice(keys.length - 1, 0, paramKey),
                                value: item
                            }));
                        } else {
                            value = pop.value;
                        }
                        let position;
                        keys.forEach(key => {
                            position = params[key]
                        });
                        if (Array.isArray(position)) {
                            position[0][paramKey] = value;
                        } else {
                            // object
                            position[paramKey] = value;
                        }
                    }
                }
                return params;
            },
            send() {
                let HOST = CONSTANT.HOST_URL[CONSTANT.CONFIG.getProfilesActive(CONSTANT.CONFIG.DEBUG)];
                this.sendDisable = true;
                let url = this.selectedEnv ? (this.selectedEnv.frontUri + this.api.apiUri) : this.api.apiUri;
                try {
                    if (url.startsWith('/')) {
                        this.$message.error('url error：' + url);
                        return;
                    }
                    let headers = {};
                    this.api.requestHeaders.forEach(item => {
                        headers[item.name] = item.value;
                    });
                    let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[this.api.requestParamType];
                    headers[contentTypeName] = contentTypeValue;
                    let method = CONSTANT.REQUEST_TYPE[this.api.apiRequestType];
                    let params = this.getParams();
                    let logHeaders = {};
                    logHeaders[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH] = this.$store.getters.loginAuth;
                    logHeaders[CONSTANT.CONTENT_TYPE[0][0]] = CONSTANT.CONTENT_TYPE[0][1];
                    window.postMessage({
                        url: url,
                        headers: headers,
                        method: method,
                        params: params,
                        logUrl: HOST + CONSTANT.REQUEST_URL.API_TEST_HISTORY_ADD,
                        logHeaders: logHeaders,
                        apiId: this.api.id,
                    }, '*');
                } finally {
                    setTimeout(() => this.sendDisable = false, 1000);
                }
            },
            command(func) {
                func();
            },
            newTab() {
                console.log('newTab');
            },
            saveMock(isSuccess) {
                let params = this.$refs['respData'].innerHTML;
                let mock = JSON.stringify(JSON.parse(params.replace(/<br>/g, '')));
                let reqData = {id: this.api.id};
                if (isSuccess) {
                    reqData['apiSuccessMock'] = mock;
                } else {
                    reqData['apiFailureMock'] = mock;
                }
                this.$axios.post(CONSTANT.REQUEST_URL.API_SAVE_MOCK, reqData).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.$message.success('saved');
                    }
                });
            }
        },
        mounted() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #api-test-container
        .el-input-group__prepend
            padding 5px
            border 0

            .el-input__inner
                padding 0
                background-color #f5f5f5

        .test-info
            text-align left
            position relative
            height 450px

            .headers
                font-size 12px
                line-height 15px

            .data
                left 0
                top 0
                right 0
                bottom 0
                overflow-y auto
                overflow-x hidden
                font-size 15px
                line-height 19px

</style>
