<template>
  <el-container id="api-container" class="api-container">
    <el-header height="45px" style="padding:10px;text-align: left;line-height: 25px;">
      <template v-if="$route.path==='/api/edit'">
        <el-button @click="save" size="mini" type="success" plain>Save</el-button>
        <el-button @click="view" size="mini" type="warning" plain>Cancel</el-button>
      </template>
      <el-button v-if="['/api/test','/api/testCase'].includes($route.path)" @click="view" size="mini" type="success" plain>
        View
      </el-button>
      <el-button v-if="['/api/detail','/api/testCase'].includes($route.path)" @click="test" size="mini" type="success" plain>
        Test
      </el-button>
      <el-button v-if="['/api/detail','/api/test'].includes($route.path)" @click="testCase" size="mini" type="success" plain>
        Test Case
      </el-button>
      <el-button v-if="hasAuth&&['/api/detail','/api/test','/api/testCase'].includes($route.path)"
                 @click="edit" size="mini" type="primary" plain>
        Edit
      </el-button>
      <template v-if="$route.path==='/api/detail'&&hasAuth">
        <el-button @click="copy" size="mini" type="primary">Copy</el-button>
        <el-button @click="del" size="mini" type="danger">Delete</el-button>
      </template>

      <el-select v-model="selectedEnvName" filterable placeholder="choose environment" clearable
                 @change="selectEnv" style="float: right;margin-right: 30px" size="mini">
        <el-option v-for="item in envConfigList" :key="item.id" :value="item">
          <el-tooltip :content="item.frontUri" placement="left">
            <span style="width: 100%">{{ item.name }}</span>
          </el-tooltip>
        </el-option>
      </el-select>
    </el-header>
    <el-main>
      <router-view ref="api-sub-router"/>
    </el-main>
    <confirm-dialog :dialog="delConfirmDialog" :form="delForm" @flush="delConfirmDialog.flush"/>
  </el-container>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";
import ConfirmDialog from "../../components/confirm/confirmDialog";

export default {
  name: 'index',
  components: {ConfirmDialog},
  data() {
    return {
      selectedProjectUserType: this.$store.getters.selectedProjectUserType,
      projectId: this.$store.getters.selectedProjectId,
      envConfigList: [],
      selectedEnv: undefined,
      selectedEnvName: '',
      delConfirmDialog: {
        show: false,
        title: 'Delete Confirm',
        content: 'Are you sure delete api ?',
        url: CONSTANT.REQUEST_URL.API_REMOVE,
        flush: () => {
          this.$router.push('/apiDoc');
        }
      },
      delForm: {
        ids: [this.$route.query.id],
        projectId: this.$store.getters.selectedProjectId,
      },
    }
  },
  computed: {
    hasAuth() {
      return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
    }
  },
  methods: {
    init() {
      this.$axios.post(CONSTANT.REQUEST_URL.API_ENV_FIND_LIST, {projectId: this.projectId}).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.envConfigList = resp.data.data;
        }
      });
    },
    selectEnv(env) {
      this.selectedEnv = env;
      this.selectedEnvName = env.name;
      let $ref = this.$refs['api-sub-router'];
      if ($ref.flushEnv) {
        $ref.flushEnv(env);
      }
    },
    save() {
      this.$refs['api-sub-router'].save();
    },
    edit() {
      this.$router.push({
        path: '/api/edit',
        query: {id: this.$route.query.id}
      });
    },
    del() {
      this.delConfirmDialog.show = true;
    },
    test() {
      this.$router.push({
        path: '/api/test',
        query: {
          id: this.$route.query.id,
          selectedEnv: this.selectedEnv,
        }
      });
    },
    view() {
      if (this.$route.query.id) {
        this.$router.push({
          path: '/api/detail',
          query: {id: this.$route.query.id}
        });
      } else {
        this.$router.go(-1);
      }
    },
    copy() {
      this.$router.push({
        path: '/api/edit',
        query: {id: this.$route.query.id, copy: true}
      });
    },
    testCase() {
      this.$router.push({
        path: '/api/testCase',
        query: {id: this.$route.query.id, copy: true, selectedEnv: this.selectedEnv}
      });
    }
  },
  created() {
    this.init();
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
#api-container
  height 100%
  border 1px solid #d9d9d9

  .el-header
    background-color #fff
    color #333
    text-align center
    line-height 60px

  .el-main
    background-color #fff
    color #333
    text-align center
    line-height 160px
</style>
