<template>
  <div id="api-doc-container" style="min-width:1250px">
    <el-container>
      <el-aside width="150px" style="font-size:6px">
        <div class="api-group-header">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="addSubGroup(null)"
                     v-if="hasAuth">
            Group
          </el-button>
        </div>
        <div :class="{'select-all':true,current:!selectedGroupId}" @click="()=>selectGroup()">all</div>
        <el-tree :data="groups" :props="{label:'name',children:'childList'}" :expand-on-click-node="false"
                 node-key="id" default-expand-all @node-click="selectGroup"
                 style="height: 86vh;overflow-y:auto"
                 draggable @node-drop="moveNode" highlight-current>
                    <span class="api-group-node" slot-scope="{node,data}">
                        <span style="float:left;padding-left: 1px" :id="data.id" :class="{current:selectedGroupId===data.id}">
                            <template v-if="node.label.length>14-data.getLevel(data)*3">
                                <el-popover popper-class="api-doc-popover" placement="top-end" :close-delay="0"
                                            trigger="hover">
                                    <span style="padding:0;font-size:5px">{{ node.label }}</span>
                                    <span slot="reference">
                                        {{ node.label.substr(0, 14 - data.getLevel(data) * 3) + '...' }}
                                    </span>
                                </el-popover>
                            </template>
                            <template v-if="node.label.length<=14-data.getLevel(data)*3">
                                {{ node.label }}
                            </template>
                        </span>
                        <div @click.stop>
                          <el-dropdown @command="command" style="float:right;padding-right: 5px" trigger="click">
                              <span class="el-dropdown-link">
                                  <i class="el-icon-arrow-down el-icon-more"/>
                              </span>
                              <el-dropdown-menu slot="dropdown">
                                  <el-dropdown-item :command="()=>addSubGroup(data)">add sub group</el-dropdown-item>
                                  <el-dropdown-item :command="()=>editSubGroup(data)">edit</el-dropdown-item>
                                  <el-dropdown-item :command="()=>delApiGroup(data)"
                                                    style="color: red">delete</el-dropdown-item>
                              </el-dropdown-menu>
                          </el-dropdown>
                        </div>
                    </span>
        </el-tree>
      </el-aside>
      <el-main>
        <div style="width: auto;background-color:#fff;text-align: left;padding:5px 0 0 10px">
          <el-row type="flex">
            <el-col style="width: 155px">
              <el-dropdown size="mini" @command="command" trigger="click">
                <el-button size="mini"
                           :type="query.apiStatus===undefined?'primary':
                                   (query.apiStatus===0?'success':
                                        query.apiStatus===2||query.apiStatus===8?'danger':'warning')">
                  {{ query.apiStatus === undefined ? 'Filter status' : CONSTANT.API_STATUS[query.apiStatus] }}
                  <i class="el-icon-arrow-down el-icon--right"/>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item class="api-status-option" :command="()=>selectQueryStatus()">
                    All
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option success"
                                    :command="()=>selectQueryStatus(0)">
                    {{ CONSTANT.API_STATUS[0] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(7)">
                    {{ CONSTANT.API_STATUS[7] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(6)">
                    {{ CONSTANT.API_STATUS[6] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(1)">
                    {{ CONSTANT.API_STATUS[1] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(5)">
                    {{ CONSTANT.API_STATUS[5] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(4)">
                    {{ CONSTANT.API_STATUS[4] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(3)">
                    {{ CONSTANT.API_STATUS[3] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option warning"
                                    :command="()=>selectQueryStatus(8)">
                    {{ CONSTANT.API_STATUS[8] }}
                  </el-dropdown-item>
                  <el-dropdown-item class="api-status-option error"
                                    :command="()=>selectQueryStatus(2)">
                    {{ CONSTANT.API_STATUS[2] }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-col>
            <el-col style="width: 200px">
              <el-input placeholder="search name or uri" v-model.trim="query.nameOrUri" clearable
                        @keyup.enter.native="findApiPage" size="mini" style="top:2px;width: 150px"/>
            </el-col>
            <el-col>
              <el-button icon="el-icon-search" @click="findApiPage" circle size="mini"/>
            </el-col>
          </el-row>
        </div>
        <el-table :data="dataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                  height="85vh"
                  :row-style="{cursor:'pointer'}" @row-click="clickRow" row-key="id" ref="api-doc-table" size="small">
          <el-table-column type="selection" :width="showSelect?'20':'1'"/>
          <el-table-column width="81">
            <template slot-scope="scope">
              <el-tag size="mini" effect="plain" style="padding: 0 4px"
                      :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                {{ CONSTANT.API_STATUS[scope.row.apiStatus] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Group" width="110" :formatter="(row)=>row.group?row.group.name:''"
                           style="font-size: 8px"/>
          <el-table-column label="Name" prop="name" width="200" show-overflow-tooltip/>
          <el-table-column label="Api uri" prop="apiUri" width="250" show-overflow-tooltip/>
          <el-table-column label="Create name" prop="createName" width="90"/>
          <el-table-column label="Update name" prop="updateName" width="90"/>
          <el-table-column label="Update time" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
          <el-table-column v-if="hasAuth" width="420">
            <template slot="header">
              <el-row>
                <el-col :span="24">
                  <template v-if="!showSelect">
                    <el-button size="mini" type="success" @click="addApi">Add</el-button>
                    <el-button size="mini" type="warning" @click="batchOperate">
                      Batch Operate
                    </el-button>
                  </template>
                  <template v-else>
                    <el-button size="mini" @click="showSelect=false">Cancel</el-button>
                    <el-button size="mini" type="primary" @click="switchStatus">
                      Switch Status
                    </el-button>
                    <el-button size="mini" type="primary" @click="moveGroup">
                      Move Group
                    </el-button>
                    <el-button size="mini" type="danger" @click="delApi">Batch Delete
                    </el-button>
                  </template>
                </el-col>
              </el-row>
            </template>
            <template slot-scope="scope">
              <div @click.stop>
                <el-button size="mini" @click="testApi(scope.row)">Test</el-button>
                <el-button size="mini" @click="editApi(scope.row)">Edit</el-button>
                <el-button size="mini" @click="viewApiByTab(scope.row)" type="success" plain>New Tab</el-button>
                <el-dropdown @command="command" trigger="click">
                <span class="el-dropdown-link">
                  <i class="el-icon-arrow-down el-icon-more"/>
                </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="()=>copyApi(scope.row)">Copy</el-dropdown-item>
                    <el-dropdown-item :command="()=>delApi(scope.row)" style="color: red">Delete
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <page-template :query="query" @flush="findApiPage"/>
      </el-main>
    </el-container>
    <edit-api-group-dialog :dialog="editApiGroupDialog" :form="editApiGroupForm" @flush="findApiGroups"/>
    <confirm-dialog :dialog="delConfirmDialog" :form="delForm" @flush="delConfirmDialog.flush"/>
    <select-api-status-dialog :dialog="selectApiStatusDialog" @flush="findApiPage"/>
    <select-api-group-dialog :dialog="selectApiGroupDialog" @flush="findApiPage" ref="select-api-group-dialog"/>
  </div>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";
import EditApiGroupDialog from "./editApiGroupDialog";
import PageTemplate from "../../components/pageTemplate/pageTemplate";
import ConfirmDialog from "../../components/confirm/confirmDialog";
import SelectApiStatusDialog from "../../components/selectApiStatus/selectApiStatusDialog";
import SelectApiGroupDialog from "../../components/selectApiGroup/selectApiGroupDialog";

export default {
  name: 'index',
  components: {SelectApiGroupDialog, SelectApiStatusDialog, ConfirmDialog, PageTemplate, EditApiGroupDialog},
  data() {
    return {
      CONSTANT,
      selectedProjectUserType: this.$store.getters.selectedProjectUserType,
      projectId: this.$store.getters.selectedProjectId,
      groups: [],
      editApiGroupDialog: {
        show: false,
        title: '',
        url: ''
      },
      editApiGroupForm: {},
      dataList: [],
      query: {
        groupIds: [],
        apiStatus: undefined,
        nameOrUri: '',
        page: {
          current: 1,
          size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
          total: 0,
        }
      },
      selectedGroupId: '',
      selectedGroup: undefined,
      delConfirmDialog: {
        show: false,
        title: 'Delete Confirm',
        content: '',
        url: '',
        flush: () => {
        }
      },
      delForm: {ids: [], projectId: this.$store.getters.selectedProjectId},
      showSelect: false,
      selectApiStatusDialog: {
        show: false,
        ids: [],
        projectId: '',
        apiStatus: '',
      },
      selectApiGroupDialog: {
        show: false,
        ids: [],
        projectId: this.$store.getters.selectedProjectId,
      },
    }
  },
  computed: {
    hasAuth() {
      return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
    },
  },
  methods: {
    dateFormat(time) {
      return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
    },
    selectGroup(apiGroup) {
      if (!apiGroup) {
        // click all
        let selectElement = document.getElementsByClassName('el-tree-node is-current is-focusable');
        if (selectElement.length > 0) {
          selectElement[0].classList.remove('is-current');
        }
        this.selectedGroupId = '';
        this.selectedGroup = undefined;
      } else {
        this.selectedGroupId = apiGroup.id;
        this.selectedGroup = apiGroup;
      }
      this.findApiPage();
    },
    command(func) {
      if (func) {
        func();
      }
    },
    addSubGroup(data) {
      this.editApiGroupForm = {
        name: '',
        projectId: this.projectId,
        parentId: '',
      };
      if (data) {
        this.editApiGroupForm.parentId = data.id;
      }
      this.editApiGroupDialog = {
        show: true,
        title: 'Add Api Group',
        url: CONSTANT.REQUEST_URL.API_GROUP_ADD,
      };
    },
    editSubGroup(data) {
      this.editApiGroupForm = JSON.parse(JSON.stringify(data));
      this.editApiGroupDialog = {
        show: true,
        title: 'Edit Api Group',
        url: CONSTANT.REQUEST_URL.API_GROUP_EDIT,
      };
    },
    findApiGroups() {
      return new Promise(resolve => {
        this.groups = [];
        this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_FIND_LIST, {projectId: this.projectId}).then(resp => {
          if (UTILS.checkResp(resp)) {
            let all = resp.data.data;
            let dict = {};
            all.forEach(item => dict[item.id] = item);
            for (let i = all.length - 1; i >= 0; i--) {
              let item = all[i];
              item.getLevel =
                  (item) => (item.parentId && item.parentId !== '') ?
                      dict[item.parentId].getLevel(dict[item.parentId]) + 1 : 0;
              if (!item.parentId || item.parentId === '') {
                this.groups.splice(0, 0, item);
              } else if (dict[item.parentId].childList) {
                dict[item.parentId].childList.splice(0, 0, item);
              } else {
                dict[item.parentId].childList = [item];
              }
            }
          }
          resolve();
        });
      })
    },
    findApiPage() {
      return new Promise(resolve => {
        this.showSelect = false;
        this.selectApiStatusDialog.apiStatus = '';
        this.query.projectId = this.projectId;
        this.query.groupIds = [];
        if (this.selectedGroup && this.selectedGroup.id) {
          // get all child group id
          let stack = Array(this.selectedGroup);
          while (stack.length > 0) {
            let pop = stack.pop();
            this.query.groupIds.push(pop.id);
            if (pop.childList && pop.childList.length > 0) {
              pop.childList.forEach(child => stack.push(child));
            }
          }
        }
        UTILS.findPage(this, this, CONSTANT.REQUEST_URL.API_FIND_PAGE, () => resolve());
      });
    },
    addApi() {
      this.$router.push({
        path: '/api/edit',
        query: {groupId: this.selectedGroupId}
      });

    },
    moveNode(before, after, inner) {
      let parentId = '';
      switch (inner) {
        case 'before':
          parentId = after.data.parentId;
          break;
        case 'inner':
        case 'after':
          parentId = after.data.id;
          break;
        default:
      }
      if (parentId !== before.data.parentId) {
        before.data.parentId = parentId;
        this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_EDIT, before.data).then(resp => {
          if (UTILS.checkResp(resp)) {
            this.findApiGroups();
          }
        });
      }
    },
    batchOperate() {
      this.showSelect = true;
    },
    editApi(api) {
      this.$router.push({
        path: '/api/edit',
        query: {id: api.id}
      });
    },
    clickRow(row) {
      if (this.showSelect) {
        this.$refs['api-doc-table'].toggleRowSelection(row);
      } else {
        if (this.selectedGroupId) {
          this.$store.dispatch('selectGroupId', this.selectedGroupId);
        } else {
          this.$store.dispatch('selectGroupId', undefined);
        }
        this.$router.push({
          path: '/api/detail',
          query: {
            id: row.id,
          },
        });
      }
    },
    delApiGroup(group) {
      this.delForm.id = group.id;
      this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api group: {name}?',
          {name: group.name});
      this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_GROUP_DELETE;
      this.delConfirmDialog.flush = () => this.findApiGroups();
      this.delConfirmDialog.show = true;
    },
    viewApiByTab(api) {
      let newTab = this.$router.resolve({
        path: '/api/detail',
        query: {id: api.id},
      });
      window.open(newTab.href, '_blank');
    },
    copyApi(api) {
      this.$router.push({
        path: '/api/edit',
        query: {id: api.id, copy: true}
      });
    },
    testApi(api) {
      this.$router.push({
        path: '/api/test',
        query: {
          id: api.id,
          selectedEnv: this.selectedEnv,
        }
      });
    },
    delApi(api) {
      if (api.id !== undefined) {
        this.delForm.ids = [api.id];
        this.delConfirmDialog.content = UTILS.formatStr('Are you sure delete api:  {name}?',
            {name: api.name});
      } else {
        let selection = this.$refs['api-doc-table'].selection;
        if (!selection || selection.length === 0) {
          this.$message.error('please select first');
          return;
        }
        this.delForm.ids = [];
        selection.forEach(row => this.delForm.ids.push(row.id));
      }
      this.delConfirmDialog.url = CONSTANT.REQUEST_URL.API_REMOVE;
      this.delConfirmDialog.flush = () => this.findApiPage();
      this.delConfirmDialog.content = 'Are you sure delete api?';
      this.delConfirmDialog.show = true;
    },
    switchStatus() {
      let selection = this.$refs['api-doc-table'].selection;
      if (!selection || selection.length === 0) {
        this.$message.error('please select first');
        return;
      }
      this.selectApiStatusDialog.projectId = this.projectId;
      this.selectApiStatusDialog.ids = [];
      selection.forEach(row => this.selectApiStatusDialog.ids.push(row.id));
      this.selectApiStatusDialog.show = true;
    },
    moveGroup() {
      let selection = this.$refs['api-doc-table'].selection;
      if (!selection || selection.length === 0) {
        this.$message.error('please select first');
        return;
      }
      this.selectApiGroupDialog.ids = [];
      selection.forEach(row => this.selectApiGroupDialog.ids.push(row.id));
      this.selectApiGroupDialog.show = true;
      this.$refs['select-api-group-dialog'].findApiGroups();
    },
    selectQueryStatus(apiStatus) {
      this.query.apiStatus = apiStatus;
      this.findApiPage();
    }
  },
  created() {
    let selectedGroupId = this.$store.getters.selectedGroupId;
    if (selectedGroupId) {
      this.findApiGroups().then(() => {
        document.getElementById(selectedGroupId).click();
      });
    } else {
      this.findApiGroups();
      this.findApiPage();
    }
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus">
.api-status-option
  &.success
    color #44B549

  &.warning
    color #E6A23C

  &.error
    color #F56C6C

#api-doc-container
  border 1px solid #d9d9d9

  .el-tree-node__content
    height 30px

  .el-table__header-wrapper
    height 40px

  div.cell
    padding 0 5px

  .el-button
    margin-top 1.5px

  .select-all
    text-align left
    padding-left 10px
    cursor pointer
  .select-all:hover
    background-color #F5F7FA
  .select-all.current
    background-color #F2F6FC


  .el-aside
    background-color #fff
    color #333
    text-align center
    line-height 26px

    .api-group-node
      width 100%
    .current
      font-weight bold

  .api-group-header
    text-align left
    padding 5px
    background #FFF

  .el-tree-node__expand-icon
    padding 1px

  .el-main
    padding 0 0 0 5px

    .el-dropdown-link
      padding 3px 10px
      content ''
      top 0
</style>
