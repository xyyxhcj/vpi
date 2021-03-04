<template>
  <div id="api-view-container" style="min-width: 1250px;line-height: 15px">
    <el-row>
      <el-col :span="12" style="text-align: left">
        <el-tag>{{ CONSTANT.REQUEST_TYPE[api.apiRequestType] }}</el-tag>
      </el-col>
    </el-row>
    <div style="text-align: left;margin: 5px;line-height: 30px">
      <div style="font-size: 22px">{{ api.apiUri }}</div>
      <div style="font-size: 16px">{{ api.name }}</div>
      <div style="font-size: 12px;color: #999999">
        <span class="api-edit-info">create: {{ api.createName }}</span>
        <span class="api-edit-info">update: {{ api.updateName }}</span>
        <span class="api-edit-info">updateTime: {{ api.updateTime === "" ? "":dateFormat(api.updateTime) }}</span>

        <el-button class="test-all-button" size="small" type="success" @click="testAll()">Test All</el-button>

      </div>
    </div>

    <line-text text="Test Case"/>
    <template v-if="testCaseList.length>0">
      <el-table
          align="center"
          :data="testCaseList"
          stripe
          border
          style="width: 100%">
        <el-table-column
            align="center"
            prop="name"
            label="testCaseName"
            fit
            show-overflow-tooltip
            >
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="left-start">
              <div style="max-height: 600px;max-width: 500px">
                <pre>{{ transformInfo(scope.row.requestInfo, 'Request Header', 'Request Parameter') }}</pre>
                <pre>{{ transformInfo(scope.row.responseInfo, 'Response Header', 'Response Parameter') }}</pre>
              </div>
              <div slot="reference">
                {{ scope.row.name }}
              </div>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column
            align="center"
            prop="createName"
            label="createName"
            width="200">
        </el-table-column>

        <el-table-column
            align="center"
            prop="createTime"
            :formatter="(row)=>dateFormat(row.updateTime)"
            label="createTime"
            width="200">
        </el-table-column>

        <el-table-column
            align="center"
            prop="checkField"
            label="checkField"
            width="200">

        </el-table-column>

        <el-table-column
            align="center"
            prop="checkValue"
            label="checkValue"
            width="200">
        </el-table-column>

        <el-table-column
            align="center"
            label="testResult"
            width="200">
          <template slot-scope="scope">
            <span :id="scope.row.id"></span>
          </template>
        </el-table-column>

        <el-table-column
            align="center"
            label="operation"
            width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary"  @click="toDetails(scope.row)">Details</el-button>
            <el-button size="mini" type="danger"   @click="runTest(scope)">Test</el-button>
          </template>
        </el-table-column>

      </el-table>
    </template>
  </div>
</template>

<script>
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";
import LineText from "@/components/lineText/lineText";

export default {
  name: "testCase",
  components: { LineText},
  data() {
    return {
      CONSTANT,
      UTILS,
      api: {
        projectId: this.$store.getters.selectedProjectId,
        name: '',
        apiUri: '/',
        desc: '',
        type: '',
        apiRequestType: 0,
        apiStatus: 0,
        createName:'',
        updateName:'',
        updateTime:'',
        requestHeaders:[],
        testCaseVO:{
          dataList:[]
        },
        requestParamVO: {
          dataList: [],
        },
      },
      selectedEnv: this.$route.query.selectedEnv,
      testCaseList :[],
      testCase:{},
    };
  },
  methods: {
    dateFormat(time) {
      return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
    },
    init(){
      //api相关信息
      this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.id}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.api = resp.data.data;
        }
      });
      //单元测试列表
      this.$axios.post(CONSTANT.REQUEST_URL.API_TEST_CASE_FIND_PAGE, {apiId: this.$route.query.id}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.testCaseList = resp.data.data.records;
        }
      });
    },
    toDetails(row){
      this.$router.push({
        path: '/api/testCaseDetail',
        query: {id:row.id,apiId:row.apiId,selectedEnv: this.selectedEnv}
      });
    },
    //单个测试
    runTest(scope){
      //判断有误安装插件
      let vpiPluginSign = document.getElementById('vpi-plugin-loaded');
      if (!vpiPluginSign || vpiPluginSign.innerHTML === '') {
        this.$message.error('please install vpi plugin');
        return;
      }
      this.$set(scope.row,'testDisable',true);
      let HOST = CONSTANT.HOST_URL[CONSTANT.CONFIG.getProfilesActive(CONSTANT.CONFIG.DEBUG)];
      let url = this.selectedEnv && this.selectedEnv.frontUri ? (this.selectedEnv.frontUri + this.api.apiUri)
          : this.api.apiUri;
      try {
        if (url.startsWith('/')) {
          this.$message.error('url error：' + url);
          return;
        } else if (!url.startsWith('http')) {
          url = 'http://' + url;
        }

        if(!UTILS.isJSON(this.testCaseList[scope.$index].requestInfo)){
          this.$message.error('request params error :' + this.testCaseList[scope.$index].requestInfo);
          return;
        }
        let requestInfo = JSON.parse(this.testCaseList[scope.$index].requestInfo);
        //testCase request headers
        let headers = {};
        Object.keys(requestInfo.headers).forEach(function (key) {
          headers[key] = requestInfo.headers[key];
        });
        let method = CONSTANT.REQUEST_TYPE[this.api.apiRequestType];
        if (this.api.apiRequestType !== 1) {
          // Ignore Get
          let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[this.api.requestParamType];
          headers[contentTypeName] = contentTypeValue;
        }
        //testCase request params
        let params = {};
        Object.keys(requestInfo.data).forEach(function (key) {
          params[key] = requestInfo.data[key];
        });
        if (this.api.apiRequestType === 1) {
          // use Get
          let paramStr = '';
          Object.keys(params).forEach(key => paramStr += (key + '=' + params[key] + '&'));
          let index = url.indexOf('?');
          if (index !== -1) {
            url = url.substring(0, index);
          }
          url += '?' + paramStr;
          params = paramStr;
        }
        let logHeaders = {};
        logHeaders[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH] = this.$store.getters.loginAuth;
        logHeaders[CONSTANT.CONTENT_TYPE[0][0]] = CONSTANT.CONTENT_TYPE[0][1];
        window.postMessage({
          url: url,
          requestParamType: this.api.requestParamType,
          headers: headers,
          method: method,
          params: params,
          logUrl: HOST + CONSTANT.REQUEST_URL.API_TEST_HISTORY_ADD,
          logHeaders: logHeaders,
          apiId: this.api.id,
          checkField:scope.row.checkField,
          checkValue:scope.row.checkValue,
          isTestCase:true,
          testCaseId:scope.row.id
        }, '*');
      } finally {
        setTimeout(() => scope.row.testDisable = false, 500);
      }
    },
    //批量测试
    testAll(){

      //判断有误安装插件
      let vpiPluginSign = document.getElementById('vpi-plugin-loaded');
      if (!vpiPluginSign || vpiPluginSign.innerHTML === '') {
        this.$message.error('please install vpi plugin');
        return;
      }
      let HOST = CONSTANT.HOST_URL[CONSTANT.CONFIG.getProfilesActive(CONSTANT.CONFIG.DEBUG)];
      let url = this.selectedEnv && this.selectedEnv.frontUri ? (this.selectedEnv.frontUri + this.api.apiUri)
          : this.api.apiUri;
      if (url.startsWith('/')) {
        this.$message.error('url error：' + url);
        return;
      } else if (!url.startsWith('http')) {
        url = 'http://' + url;
      }
        //遍历所有测试样例，发送请求到插件
        for(let item of this.testCaseList){

          if(!UTILS.isJSON(item.requestInfo)){
            this.$message.error('request params error :' + item.requestInfo);
            return;
          }
          let requestInfo = JSON.parse(item.requestInfo);
          //testCase request headers
          let headers = {};
          Object.keys(requestInfo.headers).forEach(function (key) {
            headers[key] = requestInfo.headers[key];
          });
          let method = CONSTANT.REQUEST_TYPE[this.api.apiRequestType];
          if (this.api.apiRequestType !== 1) {
            // Ignore Get
            let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[this.api.requestParamType];
            headers[contentTypeName] = contentTypeValue;
          }
          //testCase request params
          let params = {};
          Object.keys(requestInfo.data).forEach(function (key) {
            params[key] = requestInfo.data[key];
          });
          if (this.api.apiRequestType === 1) {
            // use Get
            let paramStr = '';
            Object.keys(params).forEach(key => paramStr += (key + '=' + params[key] + '&'));
            let index = url.indexOf('?');
            if (index !== -1) {
              url = url.substring(0, index);
            }
            url += '?' + paramStr;
            params = paramStr;
          }
          let logHeaders = {};
          logHeaders[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH] = this.$store.getters.loginAuth;
          logHeaders[CONSTANT.CONTENT_TYPE[0][0]] = CONSTANT.CONTENT_TYPE[0][1];
          window.postMessage({
            url: url,
            requestParamType: this.api.requestParamType,
            headers: headers,
            method: method,
            params: params,
            logUrl: HOST + CONSTANT.REQUEST_URL.API_TEST_HISTORY_ADD,
            logHeaders: logHeaders,
            apiId: this.api.id,
            checkField:item.checkField,
            checkValue:item.checkValue,
            isTestCase:true,
            testCaseId:item.id
          }, '*');
        }


    },
    flushEnv(env) {
      this.selectedEnv = env;
      for (let i = this.api.requestHeaders.length - 1; i >= 0; i--) {
        let header = this.api.requestHeaders[i];
        if (header.isEnvHeader) {
          this.api.requestHeaders.splice(i, 1);
        }
      }
      if (env && env.envHeader && env.envHeader !== '') {
        let envHeaders = JSON.parse(env.envHeader);
        envHeaders.forEach(header => {
          header.isEnvHeader = true;
          this.api.requestHeaders.unshift(header);
        });
        this.$refs['reqHeaders'].selectEnvHeader();
      }
    },
    transformInfo(info, headerTitle, paramTitle) {
      let infoObj = JSON.parse(info);
      let headerStr = '';
      Object.keys(infoObj.headers).forEach(key => headerStr = headerStr + key + ': ' + infoObj.headers[key] + '\r\n');
      let data = (typeof infoObj.data === 'string' && !UTILS.isJSON(infoObj.data)) ? infoObj.data : UTILS.formatJson(infoObj.data);
      return '【' + headerTitle + '】 \r\n' + headerStr + '【' + paramTitle + '】 \r\n' + data;
    },
  },
  mounted(){
    this.init();
  }
}
</script>

<style lang="stylus" rel="stylesheet/stylus">

.test-all-button {
  float: right;
  margin-right: 50px
}

.el-table .cell {
  white-space: pre-line;
}


#api-test-container
  .api-edit-info
    margin-right 30px

  height 100%

  .el-input-group__prepend
    padding 5px
    border 0

    .el-input__inner
      padding 0
      background-color #f5f5f5

  .test-info
    text-align left
    position relative

    .headers
      font-size 12px
      line-height 15px

    .data
      margin-top 5px
      left 0
      top 0
      right 0
      bottom 0
      overflow-y auto
      overflow-x hidden
      font-size 15px
      line-height 19px

    .el-tabs__header
      margin 0

    .th_content
      padding-left 7px

    .el-table td, .el-table th
      padding-top 1px
      padding-bottom 1px

      div.cell
        padding 0 1px

      .el-button
        padding 5px 5px
        margin-top: 1.5px
</style>