<template>
  <div id="api-test-container" style="min-width: 1250px;line-height: 15px">


    <div id="test-up">

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
      <div style="text-align: left;margin: 5px;line-height: 30px;">{{ api.name }}</div>
      <div style="text-align: left;margin: 5px;line-height: 30px;color: #999999">{{ api.desc }}</div>
      <el-tabs type="card" v-model="reqDefaultCard" style="line-height: 25px">
        <el-tab-pane label="Request Header">
          <api-headers :data-list="api.requestHeaders" ref="reqHeaders"
                       :config="{onlyRead:false,test:true,refPre:'req'}"/>
        </el-tab-pane>
        <el-tab-pane label="Request Param" name="requestParam">
          <div style="text-align: left;margin-left: 15px" v-if="api.apiRequestType!==1">
            <el-radio v-model.trim="api.requestParamType" :label="0" size="mini">
              {{ CONSTANT.REQUEST_PARAM_TYPE[0] }}
            </el-radio>
            <el-radio v-model.trim="api.requestParamType" :label="1" size="mini">
              {{ CONSTANT.REQUEST_PARAM_TYPE[1] }}
            </el-radio>
          </div>
          <data-structure :show-list="reqShowDataList" :entity="api.requestParamVO"
                          ref="reqDataStructure" :config="{test:true}"/>
        </el-tab-pane>
      </el-tabs>
      <div style="text-align: left;margin: 10px">
        <el-dropdown size="small" split-button type="success" @command="command"
                     @click="()=>!sendDisable?send():''">
          {{ !sendDisable ? 'Send' : 'Wait...' }}
          <el-dropdown-menu>
            <el-dropdown-item :command="newTab">New Tab</el-dropdown-item>
            <el-dropdown-item :command="downloadChromePlugin">Download Chrome Plugin</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <el-tabs type="card" class="test-info" @tab-click="clickTab" v-model="testInfoDefaultCard" id="test-info">
      <el-tab-pane label="Response Info" name="respInfo">
        <line-text style="color: #44B549" text="Headers"/>
        <div id="resp-headers" ref="respHeaders" class="headers"></div>
        <line-text style="color: #44B549" text="Response Body"/>
        <el-dropdown size="mini" split-button type="primary" @click="saveMock(true)" @command="command">
          Save Success Mock
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="()=>saveMock(false)">saveFailureMock</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <pre id="resp-data" ref="respData" class="data"/>
      </el-tab-pane>
      <el-tab-pane label="Request Info" name="reqInfo">
        <line-text style="color: #44B549" text="Headers"/>
        <div id="req-headers" ref="respHeaders" class="headers"></div>
        <line-text style="color: #44B549" text="Request Body"/>
        <pre id="req-data" ref="reqData" class="data"/>
      </el-tab-pane>
      <el-tab-pane label="Test History" name="testHistory">
        <el-table :data="testHistory.dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  :row-style="{cursor:'pointer'}" @row-click="selectTestHistory" ref="test-history-table"
                  border stripe>
          <el-table-column type="selection" v-if="testHistoryShowSelect" width="20"/>
          <el-table-column label="url" width="400" show-overflow-tooltip class-name="th_content">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="left-start">
                <div style="max-height: 600px;max-width: 500px">
                  <pre>{{ transformInfo(scope.row.requestInfo, 'Request Header', 'Request Parameter') }}</pre>
                  <pre>{{ transformInfo(scope.row.responseInfo, 'Response Header', 'Response Parameter') }}</pre>
                </div>
                <div slot="reference">
                  <el-tag size="mini" v-if="scope.row.method">{{ scope.row.method }}</el-tag>
                  {{ scope.row.url }}
                </div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="requestTime" width="200" class-name="th_content">
            <template slot-scope="scope">
              {{ scope.row.requestTime ? scope.row.requestTime + 'ms' : '' }}
            </template>
          </el-table-column>
          <el-table-column label="testName" prop="createName" width="150" class-name="th_content"/>
          <el-table-column label="testTime" width="200" :formatter="(row)=>dateFormat(row.createTime)"
                           class-name="th_content"/>
          <el-table-column>
            <template slot="header">
              <el-row>
                <el-col :span="24">
                  <template v-if="!testHistoryShowSelect">
                    <el-button size="mini" type="warning" @click="testHistoryBatchOperate">
                      Batch Operate
                    </el-button>
                  </template>
                  <template v-else>
                    <el-button size="mini" type="danger" @click.stop="delTestHistory">
                      Batch Delete
                    </el-button>
                    <el-button size="mini" @click.stop="testHistoryShowSelect=false">
                      Cancel
                    </el-button>
                  </template>
                </el-col>
              </el-row>
            </template>
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click.stop="delTestHistory(scope.row)">Delete
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <page-template :query="testHistory.query" @flush="testHistoryFindPage"/>
      </el-tab-pane>
    </el-tabs>
    <confirm-dialog :dialog="delConfirmDialog" :form="delForm" @flush="testHistoryFindPage"/>
  </div>


</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "../../common/js/constant";
import {UTILS} from "../../common/js/utils";
import ApiHeaders from "../../components/apiHeaders/apiHeaders";
import DataStructure from "../../components/dataStructure/dataStructure";
import LineText from "../../components/lineText/lineText";
import PageTemplate from "../../components/pageTemplate/pageTemplate";
import ConfirmDialog from "../../components/confirm/confirmDialog";

export default {
  name: 'test',
  components: {ConfirmDialog, PageTemplate, LineText, DataStructure, ApiHeaders},
  data() {
    return {
      CONSTANT,
      UTILS,
      api: {
        id: '',
        projectId: this.$store.getters.selectedProjectId,
        name: '',
        apiUri: '/',
        desc: '',
        type: '',
        apiRequestType: 0,
        apiStatus: 0,
        requestParamType: 0,
        requestParamVO: {
          dataList: [],
        },
        responseParamType: 0,
        responseParamVO: {
          dataList: [],
        },
        requestHeaders: [],
        responseHeaders: [],
        apiSuccessMock: '',
        apiFailureMock: '',
      },
      testCase:{
        name:"",
        checkField:"",
        checkValue:"",
        requestInfo:"",
        responseInfo:"",
        method:"",
      },
      reqDefaultCard: 'requestParam',
      testInfoDefaultCard: 'respInfo',
      reqShowDataList: [],
      respShowDataList: [],
      responseInfo: {},
      requestInfo: {},
      selectedEnv: this.$route.query.selectedEnv,
      sendDisable: false,
      testHistory: {
        query: {
          page: {
            current: 1,
            size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
            total: 0,
          }
        },
        dataList: [],
      },
      delConfirmDialog: {
        show: false,
        title: 'Delete Confirm',
        content: 'Are you sure delete history ?',
        url: CONSTANT.REQUEST_URL.API_TEST_HISTORY_DELETE,
      },
      delForm: {
        ids: [],
        projectId: this.$store.getters.selectedProjectId,
      },
      testHistoryShowSelect: false,
    }
  },
  methods: {
    delTestHistory(row) {
      if (row.id) {
        this.delForm.ids = [row.id];
      } else {
        let selection = this.$refs['test-history-table'].selection;
        if (!selection || selection.length === 0) {
          this.$message.error('please select first');
          return;
        }
        this.delForm.ids = [];
        selection.forEach(row => this.delForm.ids.push(row.id));
      }
      this.delConfirmDialog.show = true;
    },
    testHistoryFindPage() {
      this.testHistoryShowSelect = false;
      UTILS.findPage(this, this.testHistory, CONSTANT.REQUEST_URL.API_TEST_HISTORY_FIND_PAGE);
    },
    testHistoryBatchOperate() {
      this.testHistoryShowSelect = true;
    },
    selectTestHistory(row) {
      if (this.testHistoryShowSelect) {
        this.$refs['test-history-table'].toggleRowSelection(row);
      } else {
        // show test data
        this.testInfoDefaultCard = 'respInfo';
        this.flushEnv();
        this.api.apiUri = row.url;
        // show request info
        let requestInfo = JSON.parse(row.requestInfo);
        let reqHeaderStr = '';
        let newHeaders = [];
        //处理成字符串
        Object.keys(requestInfo.headers).forEach(key =>
            reqHeaderStr = reqHeaderStr + key + ': ' + requestInfo.headers[key] + '\r\n'
        );
        //加入到新的请求头数组里
        Object.keys(requestInfo.headers).forEach(key => {
          if (key !== 'Content-Type') {
            newHeaders.push({
              name: key,
              requireType: 0,
              value: requestInfo.headers[key]
            })
          }
        });
        document.getElementById('req-headers').innerText = reqHeaderStr;
        this.api.requestHeaders = newHeaders;
        this.$refs['reqHeaders'].selectAll();
        this.$refs['reqHeaders'].init();

        let paramsJsonStr = (typeof requestInfo.data === 'string' && !UTILS.isJSON(requestInfo.data)) ?
            requestInfo.data : UTILS.formatJson(requestInfo.data);
        document.getElementById('req-data').innerText = paramsJsonStr;
        //如果不是json格式的字符串，则不进行参数自动填充
        if (UTILS.isJSON(paramsJsonStr)) {
          let params = UTILS.json2ViewData(requestInfo.data, -1);
          //重新赋值
          UTILS.fillShowList(params, this.reqShowDataList);
          //初始化
          this.$refs['reqDataStructure'] && this.$refs['reqDataStructure'].init();
        }

        // show response info
        let responseInfo = JSON.parse(row.responseInfo);
        let respHeaderStr = '';
        Object.keys(responseInfo.headers).forEach(key => respHeaderStr = respHeaderStr + key + ': ' + responseInfo.headers[key] + '\r\n');
        document.getElementById('resp-headers').innerText = respHeaderStr;
        document.getElementById('resp-data').innerText =
            (typeof responseInfo.data === 'string' && !UTILS.isJSON(responseInfo.data)) ?
                responseInfo.data : UTILS.formatJson(responseInfo.data);
      }
    },
    transformInfo(info, headerTitle, paramTitle) {
      let infoObj = JSON.parse(info);
      let headerStr = '';
      Object.keys(infoObj.headers).forEach(key => headerStr = headerStr + key + ': ' + infoObj.headers[key] + '\r\n');
      let data = (typeof infoObj.data === 'string' && !UTILS.isJSON(infoObj.data)) ? infoObj.data : UTILS.formatJson(infoObj.data);
      return '【' + headerTitle + '】 \r\n' + headerStr + '【' + paramTitle + '】 \r\n' + data;
    },
    dateFormat(time) {
      return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
    },
    clickTab(tab) {
      if ('testHistory' === tab.name) {
        this.testHistory.query.apiId = this.api.id;
        this.testHistoryFindPage();
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
    init() {
      this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.apiId}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.api = resp.data.data;
        }
      });
      this.$axios.post(CONSTANT.REQUEST_URL.API_TEST_CASE_DETAIL, {id: this.$route.query.id}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.testCase = resp.data.data;
          //处理请求头和请求参数，响应头和响应参数
          let requestInfo = JSON.parse(this.testCase.requestInfo);
          let reqHeaderStr = '';
          let newHeaders = [];
          //处理成字符串
          Object.keys(requestInfo.headers).forEach(key =>
              reqHeaderStr = reqHeaderStr + key + ': ' + requestInfo.headers[key] + '\r\n'
          );
          //加入到新的请求头数组里
          Object.keys(requestInfo.headers).forEach(key => {
            if (key !== 'Content-Type') {
              newHeaders.push({
                name: key,
                requireType: 0,
                value: requestInfo.headers[key]
              })
            }
          });
          document.getElementById('req-headers').innerText = reqHeaderStr;
          this.api.requestHeaders = newHeaders;
          this.$refs['reqHeaders'].selectAll();
          this.$refs['reqHeaders'].init();

          let paramsJsonStr = (typeof requestInfo.data === 'string' && !UTILS.isJSON(requestInfo.data)) ?
              requestInfo.data : UTILS.formatJson(requestInfo.data);
          document.getElementById('req-data').innerText = paramsJsonStr;
          //是json格式的参数才进行自动填充
          if (UTILS.isJSON(paramsJsonStr)) {
            let params = UTILS.json2ViewData(requestInfo.data, -1);
            //重新赋值
            UTILS.fillShowList(params, this.reqShowDataList);
            //初始化
            this.$refs['reqDataStructure'] && this.$refs['reqDataStructure'].init();
          }

          // show response info
          let responseInfo = JSON.parse(this.testCase.responseInfo);
          let respHeaderStr = '';
          Object.keys(responseInfo.headers).forEach(key => respHeaderStr = respHeaderStr + key + ': ' + responseInfo.headers[key] + '\r\n');
          document.getElementById('resp-headers').innerText = respHeaderStr;
          document.getElementById('resp-data').innerText =
              (typeof responseInfo.data === 'string' && !UTILS.isJSON(responseInfo.data)) ?
                  responseInfo.data : UTILS.formatJson(responseInfo.data);
        }
      });
    },
    mergeHeader() {
      if (!this.selectedEnv || !this.selectedEnv.envHeader || this.selectedEnv.envHeader === '') {
        return this.api.requestHeaders;
      }
      let headers = JSON.parse(this.selectedEnv.envHeader);
      this.api.requestHeaders.forEach(header => headers.push(header));
      return headers;
    },
    getParams() {
      let params = {};
      let stack = [];
      this.api.requestParamVO.dataList.forEach(data => {
        let paramKey = data.paramKey;
        if (!data.selected || paramKey === '') {
          return;
        }
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
        } else if (data.value !== '') {
          if (data.paramType !== CONSTANT.PARAM_TYPE.ARRAY) {
            value = data.value;
          } else {
            value = eval('(' + data.value + ')');
          }
        }
        params[paramKey] = value;
      });
      while (stack.length > 0) {
        let {keys: keys, value: pop} = stack.shift();
        let paramKey = pop.paramKey;
        if (!pop.selected || paramKey === '') {
          continue;
        }
        let value;
        if (pop.subList.length > 0) {
          if (pop.paramType === CONSTANT.PARAM_TYPE.ARRAY) {
            // array
            value = [];
          } else {
            // object
            value = {};
          }
          // save key(each level)
          let keyList = keys.slice(0);
          keyList.push(paramKey);
          pop.subList.forEach(item => {
            stack.push({
              keys: keyList,
              value: item
            })
          });
        } else if (pop.value !== '') {
          if (pop.paramType !== CONSTANT.PARAM_TYPE.ARRAY) {
            value = pop.value;
          } else {
            value = eval('(' + pop.value + ')');
          }
        }
        let position = params;
        keys.forEach(key => {
          position = position[key]
        });
        if (Array.isArray(position)) {
          // array,split & push
          if (!value) {
            continue;
          }
          let split = value.split(CONSTANT.CONFIG.ARRAY_SPLIT_STR);
          for (let i = 0; i < split.length; i++) {
            if (i === position.length) {
              position.push({});
            }
            position[i][paramKey] = split[i];
          }
        } else {
          // object
          position[paramKey] = value;
        }
      }
      return params;
    },
    send() {
      if (UTILS.isNotInstallPlugin()) {
        this.$message.error('please install vpi plugin');
        return;
      }
      // empty test info
      document.getElementById('req-headers').innerText = '';
      document.getElementById('req-data').innerText = '';
      document.getElementById('resp-headers').innerText = '';
      document.getElementById('resp-data').innerText = '';
      this.testInfoDefaultCard = 'respInfo';
      let HOST = CONSTANT.HOST_URL[CONSTANT.CONFIG.getProfilesActive(CONSTANT.CONFIG.DEBUG)];
      this.sendDisable = true;
      let url = this.selectedEnv && this.selectedEnv.frontUri ? (this.selectedEnv.frontUri + this.api.apiUri)
          : this.api.apiUri;
      try {
        if (url.startsWith('/')) {
          this.$message.error('url error：' + url);
          return;
        } else if (!url.startsWith('http')) {
          url = 'http://' + url;
        }
        this.$refs['reqHeaders'] && this.$refs['reqHeaders'].signSelected();
        this.$refs['reqDataStructure'] && this.$refs['reqDataStructure'].signSelected();
        let headers = {};
        let requestInfo = JSON.parse(this.testCase.requestInfo);
        Object.keys(requestInfo.headers).forEach(key => headers[key] = requestInfo.headers[key]);
        let method = CONSTANT.REQUEST_TYPE[this.api.apiRequestType];
        if (this.api.apiRequestType !== 1) {
          // Ignore Get
          let [contentTypeName, contentTypeValue] = CONSTANT.CONTENT_TYPE[this.api.requestParamType];
          headers[contentTypeName] = contentTypeValue;
        }
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
          testCaseInfo: {
            checkField: this.testCase.checkField,
            checkValue: this.testCase.checkValue,
            testCaseId: this.testCase.testCaseId,
          }
        }, '*');
      } finally {
        setTimeout(() => this.sendDisable = false, 500);
      }
    },
    command(func) {
      if (func) {
        func();
      }
    },
    newTab() {
      let newTab = this.$router.resolve({
        path: '/api/test',
        query: {id: this.api.id},
      });
      window.open(newTab.href, '_blank');
    },
    downloadChromePlugin() {
      let a = document.createElement('a');
      a.href = CONSTANT.REQUEST_URL.CHROME_PLUGIN_DOWNLOAD;
      a.click();
    },
    saveMock(isSuccess) {
      const $ref = this.$refs['respData'];
      if (!$ref || $ref.innerHTML === '') {
        this.$message.error('mock is empty!');
        return;
      }
      let cleanText = $ref.innerHTML.replace(/<br>/g, '');
      let mock = UTILS.isJSON(cleanText) ? JSON.stringify(JSON.parse(cleanText)) : cleanText;
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
    },
  },
  mounted() {
    this.init();
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus">
#api-test-container
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
