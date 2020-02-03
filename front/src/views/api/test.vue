<template>
    <div id="api-test-container" style="min-width: 1250px;line-height: 15px">
        <el-row>
            <el-col :span="24" style="text-align: left">
                <el-select v-model.trim="api.apiRequestType" style="width: 8%" size="mini">
                    <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
                    <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
                </el-select>
                <el-input v-model="api.apiUri" size="mini" style="width: 91%">
                    <el-input slot="prepend" v-model="selectedEnv.frontUri" size="mini"
                              style="padding:0;width: 150px" v-if="selectedEnv"/>
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
                            console.log(position[0]);
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
                console.log('send');
                let url = this.selectedEnv ? (this.selectedEnv.frontUri + this.api.apiUri) : this.api.apiUri;
                let headers = {};
                this.api.requestHeaders.forEach(item => {
                    headers[item.name] = item.value;
                });
                console.log(CONSTANT.CONTENT_TYPE[this.api.requestParamType]);
                let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[this.api.requestParamType];
                console.log(contentTypeName);
                console.log(contentTypeValue);
                // headers[contentTypeName] = contentTypeValue;
                // if ( === 0) {
                //     // json
                //     CONSTANT.CONTENT_TYPE[]
                //     headers[]
                // } else {
                //     // form
                // }
                let params = this.getParams();
                if (this.api.apiRequestType === 0) {
                    // post
                    console.log(url);
                    this.$axios.post(url, params, {headers: headers}).then(resp => {
                        console.log(resp);
                    });
                } else {

                }
                this.api.requestHeaders;
                // this.$axios.request({
                //     url: url,
                //     method: CONSTANT.REQUEST_TYPE[this.api.apiRequestType],
                //     data: this.api.requestParamVo.dataList,
                //     headers: headers,
                // }).then(resp => {
                //     console.log(resp);
                // });
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
