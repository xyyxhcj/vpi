<template>
  <div id="api-view-container" class="api-container">
    <el-row>
      <el-col :span="12" style="text-align: left">
        <el-tag>{{ CONSTANT.REQUEST_TYPE[api.apiRequestType] }}</el-tag>
        <el-button style="margin-left: 10px"
                   :type="api.apiStatus===0?'success':api.apiStatus===2||api.apiStatus===8?'danger':'warning'"
                   size="mini"
                   round plain
                   icon="el-icon-s-help" @click="switchStatus">
          {{ CONSTANT.API_STATUS[api.apiStatus] }}
        </el-button>
      </el-col>
    </el-row>
    <div style="text-align: left;margin: 5px;line-height: 30px">
      <div style="font-size: 22px">{{ api.apiUri }}</div>
      <div style="font-size: 16px">{{ api.name }}</div>
      <div style="font-size: 16px;color: #999999">{{ api.desc }}</div>
      <div style="font-size: 12px;color: #999999">
        <span class="api-edit-info">group: {{ api.group ? api.group.name : 'none' }}</span>
        <span class="api-edit-info">create: {{ api.createName }}</span>
        <span class="api-edit-info">update: {{ api.updateName }}</span>
        <span class="api-edit-info">updateTime: {{ dateFormat(api.updateTime) }}</span>
      </div>
    </div>
    <template v-if="api.requestHeaders.length>0">
      <line-text text="Request Header"/>
      <api-headers :data-list="api.requestHeaders" :config="{onlyRead:true,refPre:'req'}"/>
    </template>
    <template v-if="reqShowDataList.length>0">
      <line-text text="Request Param"/>
      <data-structure :show-list="reqShowDataList" :entity="api.requestParamVO"
                      :config="{onlyRead:true}"/>
    </template>
    <template v-if="api.responseHeaders.length>0">
      <line-text text="Response Header"/>
      <api-headers :data-list="api.responseHeaders" :config="{onlyRead:true,refPre:'resp'}"/>
    </template>
    <template v-if="respShowDataList.length>0">
      <line-text text="Response Param"/>
      <data-structure :show-list="respShowDataList" :entity="api.responseParamVO"
                      :config="{onlyRead:true}" v-if="api.responseParamType===0"/>
      <template v-else>{{ api.responseParamVO.remark }}</template>
    </template>
    <el-row>
      <el-col :span="12" v-if="api.apiSuccessMock!==''">
        <line-text style="color: #44B549" text="Success Example"/>
        <pre class="json-content">
                    {{ UTILS.isJSON(api.apiSuccessMock) ? UTILS.formatJson(api.apiSuccessMock) : api.apiSuccessMock }}
                </pre>
      </el-col>
      <el-col :span="12" v-if="api.apiFailureMock!==''">
        <line-text style="color: #F56C6C" text="Failure Example"/>
        <pre class="json-content">
                    {{ UTILS.isJSON(api.apiFailureMock) ? UTILS.formatJson(api.apiFailureMock) : api.apiFailureMock }}
                </pre>
      </el-col>
    </el-row>
    <select-api-status-dialog :dialog="selectApiStatusDialog" @flush="init"/>
  </div>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";
import ApiHeaders from "../../components/apiHeaders/apiHeaders";
import LineText from "../../components/lineText/lineText";
import DataStructure from "../../components/dataStructure/dataStructure";
import SelectApiStatusDialog from "../../components/selectApiStatus/selectApiStatusDialog";

export default {
  name: 'detail',
  components: {SelectApiStatusDialog, DataStructure, LineText, ApiHeaders},
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
      selectedEnv: this.$route.query.selectedEnv,
      reqDefaultCard: 'requestParam',
      respDefaultCard: 'responseParam',
      reqShowDataList: [],
      respShowDataList: [],
      selectApiStatusDialog: {
        show: false,
        ids: [],
        projectId: '',
        apiStatus: '',
      }
    };
  },
  methods: {
    init() {
      this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_DETAIL, {id: this.$route.query.id}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.api = resp.data.data;
          if (this.api.requestParamVO) {
            UTILS.fillShowList(this.api.requestParamVO.dataList, this.reqShowDataList, false, false);
          }
          if (this.api.responseParamVO) {
            UTILS.fillShowList(this.api.responseParamVO.dataList, this.respShowDataList, false, false);
          }
        }
      });
    },
    dateFormat(time) {
      return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
    },
    switchStatus() {
      if (this.$store.getters.selectedProjectUserType === CONSTANT.AUTH_ROLE.READ) {
        return;
      }
      this.selectApiStatusDialog.projectId = this.api.projectId;
      this.selectApiStatusDialog.ids = [this.api.id];
      this.selectApiStatusDialog.apiStatus = this.api.apiStatus;
      this.selectApiStatusDialog.show = true;
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
#api-view-container
  .api-edit-info
    margin-right 30px

  .el-header
    background-color #B3C0D1
    color #333
    text-align center
    line-height 60px

  .el-main
    background-color #E9EEF3
    color #333
    text-align center
    line-height 160px

  .json-content
    margin 10px
    padding 10px 0
    border-radius 4px
    text-align left
    background #e5e9f2

</style>
