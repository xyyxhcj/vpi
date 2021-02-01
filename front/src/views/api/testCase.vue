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
        <span class="api-edit-info">updateTime: {{ api.updateTime == "" ? "":dateFormat(api.updateTime) }}</span>
      </div>
    </div>

    <line-text text="Test Case"/>
    <template v-if="testCaseList.length>0">
      <el-table
          :data="testCaseList"
          stripe
          border
          style="width: 100%">
        <el-table-column
            align="center"
            prop="requestInfo"
            label="请求"
            width="180">
        </el-table-column>
        <el-table-column
            header-align="center"
            label="响应"
            show-overflow-tooltip
            >
          <template slot-scope="scope">
            <pre  class="data">{{UTILS.formatJson(scope.row.responseInfo)}}</pre>
          </template>
        </el-table-column>
        <el-table-column
            align="center"
            prop="createName"
            label="测试人"
            width="180%">
        </el-table-column>
        <el-table-column
            align="center"
            prop="createTime"
            :formatter="(row)=>dateFormat(row.updateTime)"
            label="测试时间"
            width="180">
        </el-table-column>
        <el-table-column
            align="center"
            prop="status"
            label="状态"
            width="180">
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
        testCaseVO:{
          dataList:[]
        }
      },
      testCaseList :[],
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
          for(let item of this.testCaseList){
            //处理请求参数的展示
            let reqObj = JSON.parse(item.requestInfo);
            let reqStr = '';
            Object.keys(reqObj).forEach(key => reqStr = reqStr + key + ': ' + reqObj[key] + '\r\n');
            item.requestInfo = reqStr;
          }
        }
      });
    }
  },
  mounted(){
    this.init();
  }
}
</script>

<style lang="stylus" rel="stylesheet/stylus">

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