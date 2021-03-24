<template>
  <div id="api-test-case-container" class="api-container">
    <el-row>
      <el-col :span="24" style="text-align: left">
        <el-select v-model.trim="api.apiRequestType" style="width: 8%" size="mini">
          <el-option :label="CONSTANT.REQUEST_TYPE[0]" :value="0"/>
          <el-option :label="CONSTANT.REQUEST_TYPE[1]" :value="1"/>
        </el-select>
        <el-input v-model="api.apiUri" size="mini" style="width: 91%">
          <template slot="prepend">{{ selectedEnv ? selectedEnv.frontUri : '' }}</template>
        </el-input>
      </el-col>
    </el-row>
    <div style="text-align: left;margin: 5px;line-height: 30px">
      <div style="font-size: 22px">{{ api.apiUri }}</div>
      <div style="font-size: 16px">{{ api.name }}</div>
      <div style="font-size: 12px;color: #999999">
        <span class="api-edit-info">create: {{ api.createName }}</span>
        <span class="api-edit-info">update: {{ api.updateName }}</span>
        <span class="api-edit-info">updateTime: {{ api.updateTime === "" ? "" : dateFormat(api.updateTime) }}</span>
        <el-button class="test-all-button" :disabled="!this.testCaseList || this.testCaseList.length === 0" size="mini"
                   type="success" @click="testAll()">Test All
        </el-button>
      </div>
    </div>
    <line-text text="Test Case"/>
    <template v-if="testCaseList&&testCaseList.length>0">
      <el-table align="center" :data="testCaseList" stripe border>
        <el-table-column align="center" label="testCaseName" fit show-overflow-tooltip>
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="left-start">
              <div class="test-info-popover">
                <pre>{{ transformInfo(scope.row.requestInfo, 'Request Header', 'Request Parameter') }}</pre>
                <pre>{{ transformInfo(scope.row.responseInfo, 'Response Header', 'Response Parameter') }}</pre>
              </div>
              <div slot="reference">
                {{ scope.row.name }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="createName" label="createName" width="200"/>
        <el-table-column align="center" :formatter="(row)=>dateFormat(row.createTime)" label="createTime" width="200"/>
        <el-table-column align="center" prop="checkField" label="checkField" width="200"/>
        <el-table-column align="center" prop="checkValue" label="checkValue" width="200"/>
        <el-table-column align="center" label="testResult" width="200">
          <template slot-scope="scope">
            <el-popover
                placement="bottom"
                trigger="click">
              <div class="test-info-popover" :id="scope.row.id+'-popover'"/>
              <div slot="reference" :id="scope.row.id" class="cursor"/>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column align="center" label="operation">
          <template #default="scope">
            <el-button size="mini" type="primary" @click="runTest(scope.row)" :loading="scope.row.isLoading4Test">Test
            </el-button>
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
import {PLUGIN_UTILS} from "@/common/js/pluginUtils";

export default {
  name: "testCase",
  components: {LineText},
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
        createName: '',
        updateName: '',
        updateTime: '',
        requestHeaders: [],
        testCaseVO: {
          dataList: []
        },
        requestParamVO: {
          dataList: [],
        },
      },
      selectedEnv: this.$route.query.selectedEnv,
      testCaseList: [],
      testCase: {},
    };
  },
  methods: {
    dateFormat(time) {
      return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
    },
    init() {
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
    getHeaders(requestInfo) {
      let headers = {};
      Object.keys(requestInfo.headers).forEach(key => headers[key] = requestInfo.headers[key]);
      return headers;
    },
    getParams(requestInfo) {
      let params = {};
      Object.keys(requestInfo.data).forEach(function (key) {
        params[key] = requestInfo.data[key];
      });
      return params;
    },
    rowTest(row) {
      const element = document.getElementById(row.id);
      element.innerText = '';
      element.background = '';
      PLUGIN_UTILS.setLoading(row, 'isLoading4Test');
      let requestInfo = JSON.parse(row.requestInfo)
      PLUGIN_UTILS.send2Plugin(this.selectedEnv, this.api, this.getHeaders(requestInfo)
          , this.getParams(requestInfo)
          , {
            checkField: row.checkField,
            checkValue: row.checkValue,
            testCaseId: row.id,
            errorBackground: 'rgba(245,108,108,0.4)',
            failBackground: 'rgba(230,162,60,0.4)',
          });
    },
    //单个测试
    runTest(row) {
      PLUGIN_UTILS.checkValid().then(() => {
        this.rowTest(row);
      }).catch();
    },
    //批量测试
    testAll() {
      PLUGIN_UTILS.checkValid().then(() => {
        this.testCaseList.forEach(row => {
          this.rowTest(row);
        })
      }).catch();
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
  mounted() {
    this.init();
  }
}
</script>

<style lang="stylus" rel="stylesheet/stylus">

#api-test-case-container
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

  .test-all-button
    float: right;
    margin-right: 50px

  .test-info-popover
    max-height 600px
    max-width 800px

  .cursor
    cursor pointer
    &:hover
      color #409EFF
</style>
