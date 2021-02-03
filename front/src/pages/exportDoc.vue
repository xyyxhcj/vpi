<template>
    <el-container id="exportHtml">
        <el-aside width="210px" class="export-aside">
            <div style="margin-left: 7px;font-weight: bold;line-height: 30px">{{projectName}} off-line doc</div>
            <el-input placeholder="search" v-model="query.nameOrUri" @keyup.enter.native="filterApi"/>
            <div class="api-group-header">
                Group
            </div>
            <el-button class="select-all" @click="selectGroup">all</el-button>
            <el-tree :data="groups" :props="{label:'name',children:'childList'}" :expand-on-click-node="false"
                     node-key="id" default-expand-all @node-click="selectGroup" draggable highlight-current>
                <span class="api-group-node" slot-scope="{node,data}">
                    <span style="float:left;padding-left: 1px;">
                        <template v-if="node.label.length>14-data.getLevel(data)*3">
                            <el-popover popper-class="api-doc-popover" placement="top-end" :close-delay="0"
                                        trigger="hover">
                                <span style="padding:0;font-size:5px">{{node.label}}</span>
                                <span slot="reference">
                                    {{ node.label.substr(0,14-data.getLevel(data)*3)+'...' }}
                                </span>
                            </el-popover>
                        </template>
                        <template v-if="node.label.length<=14-data.getLevel(data)*3">
                            {{node.label}}
                        </template>
                    </span>
                </span>
            </el-tree>
        </el-aside>
        <el-main>
            <el-table :data="filterDataList" :header-cell-style="{color:'#44B549','font-weight':'bold'}"
                      :height="tableHeight" border
                      :row-style="{cursor:'pointer'}" @row-click="clickRow" row-key="id" ref="api-doc-table">
                <el-table-column width="100">
                    <template slot-scope="scope">
                        <el-popover trigger="hover" placement="right-start" v-if="scope.row.group">
                            <p style="font-size: 10px">Group: {{scope.row.group.name}}</p>
                            <div slot="reference">
                                <el-tag size="mini" effect="plain" style="padding: 0 4px"
                                        :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                                    {{CONSTANT.API_STATUS[scope.row.apiStatus]}}
                                </el-tag>
                            </div>
                        </el-popover>
                        <el-tag size="mini" effect="plain" style="padding: 0 4px" v-else
                                :type="scope.row.apiStatus===0?'success':
                                    scope.row.apiStatus===2||scope.row.apiStatus===8?'danger':'warning'">
                            {{CONSTANT.API_STATUS[scope.row.apiStatus]}}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="name" prop="name" width="200"/>
                <el-table-column label="apiUri" prop="apiUri" width="250" show-overflow-tooltip/>
                <el-table-column label="createName" prop="createName" width="115"/>
                <el-table-column label="updateName" prop="updateName" width="115"/>
                <el-table-column label="updateTime" width="200" :formatter="(row)=>dateFormat(row.updateTime)"/>
            </el-table>
        </el-main>
        <el-dialog :append-to-body="true" :visible.sync="showDetail" top="2px" width="85%" destroy-on-close
                   custom-class="export-api-detail">
            <el-row>
                <el-col :span="12" style="text-align: left">
                    <el-tag>{{CONSTANT.REQUEST_TYPE[selectedApi.apiRequestType]}}</el-tag>
                    <el-button style="margin-left: 10px"
                               :type="selectedApi.apiStatus===0?'success':selectedApi.apiStatus===2||selectedApi.apiStatus===8?'danger':'warning'"
                               size="mini"
                               circle plain
                               icon="el-icon-s-help">
                        {{CONSTANT.API_STATUS[selectedApi.apiStatus]}}
                    </el-button>
                </el-col>
            </el-row>
            <div style="text-align: left;margin: 5px;line-height: 30px">
                <div style="font-size: 22px">{{selectedApi.apiUri}}</div>
                <div style="font-size: 16px">{{selectedApi.name}}</div>
                <div style="font-size: 12px;color: #999999">
                    <span class="api-edit-info">group: {{selectedApi.group?selectedApi.group.name:'none'}}</span>
                    <span class="api-edit-info">create: {{selectedApi.createName}}</span>
                    <span class="api-edit-info">update: {{selectedApi.updateName}}</span>
                    <span class="api-edit-info">updateTime: {{dateFormat(selectedApi.updateTime)}}</span>
                </div>
            </div>
            <template v-if="selectedApi.requestHeaders.length>0">
                <line-text text="Request Header"/>
                <api-headers :data-list="selectedApi.requestHeaders" :config="{onlyRead:true,refPre:'req'}"/>
            </template>
            <template v-if="reqShowDataList.length>0">
                <line-text text="Request Param"/>
                <data-structure :show-list="reqShowDataList" :entity="selectedApi.requestParamVO"
                                :config="{onlyRead:true}"/>
            </template>
            <template v-if="selectedApi.responseHeaders.length>0">
                <line-text text="Response Header"/>
                <api-headers :data-list="selectedApi.responseHeaders" :config="{onlyRead:true,refPre:'resp'}"/>
            </template>
            <template v-if="respShowDataList.length>0">
                <line-text text="Response Param"/>
                <data-structure :show-list="respShowDataList" :entity="selectedApi.responseParamVO"
                                :config="{onlyRead:true}" v-if="selectedApi.responseParamType===0"/>
                <template v-else>{{selectedApi.responseParamVO.remark}}</template>
            </template>
            <el-row>
                <el-col :span="12" v-if="selectedApi.apiSuccessMock!==''">
                    <line-text style="color: #44B549" text="Success Example"/>
                    <pre class="json-content">
                    {{UTILS.isJSON(selectedApi.apiFailureMock)?UTILS.formatJson(selectedApi.apiSuccessMock):selectedApi.apiSuccessMock}}
                </pre>
                </el-col>
                <el-col :span="12" v-if="selectedApi.apiFailureMock!==''">
                    <line-text style="color: #F56C6C" text="Failure Example"/>
                    <pre class="json-content">
                    {{UTILS.isJSON(selectedApi.apiFailureMock)?UTILS.formatJson(selectedApi.apiFailureMock):selectedApi.apiFailureMock}}
                </pre>
                </el-col>
            </el-row>
        </el-dialog>
        <div style="display: none" id="apiGroupsHidden"></div>
        <div style="display: none" id="apisHidden"></div>
    </el-container>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "../common/js/constant";
import {UTILS} from "../common/js/utils";
import DataStructure from "../components/dataStructure/dataStructure";
import LineText from "../components/lineText/lineText";
import ApiHeaders from "../components/apiHeaders/apiHeaders";

export default {
        name: 'exportDoc',
        components: {DataStructure, LineText, ApiHeaders},
        props: {
            urlParams: {
                default() {
                    return UTILS.urlParse()
                }
            }
        },
        data() {
            return {
                CONSTANT,
                UTILS,
                projectName: this.urlParams.projectName,
                groups: [],
                query: {
                    projectId: this.urlParams.projectId,
                    envId: this.urlParams.envId,
                    groupIds: [],
                    apiStatus: undefined,
                    nameOrUri: '',
                },
                dataList: [],
                filterDataList: [],
                tableHeight: 0,
                reqShowDataList: [],
                respShowDataList: [],
                showDetail: false,
                selectedApi: {
                    projectId: this.urlParams.projectId,
                    name: '',
                    apiUri: '/',
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
            };
        },
        methods: {
            dateFormat(time) {
                return UTILS.formatDate(new Date(time), CONSTANT.CONFIG.DATE_FORMAT);
            },
            selectGroup(apiGroup) {
                this.query.groupIds.length = 0;
                if (!apiGroup || apiGroup instanceof MouseEvent) {
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
                    let stack = [apiGroup];
                    while (stack.length > 0) {
                        let group = stack.shift();
                        this.query.groupIds.push(group.id);
                        if (group.childList && group.childList.length > 0) {
                            group.childList.forEach(item => stack.push(item));
                        }
                    }
                }
                this.filterApi();
            },
            groupList2Tree(groupList) {
                let dict = {};
                groupList.forEach(item => dict[item.id] = item);
                for (let i = groupList.length - 1; i >= 0; i--) {
                    let item = groupList[i];
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
            },
            findApiGroups() {
                let $element = document.getElementById('apiGroupsHidden');
                let apiGroupsHtml = $element.innerHTML;
                if (apiGroupsHtml !== '') {
                    let groupList = JSON.parse(apiGroupsHtml);
                    this.groupList2Tree(groupList);
                    return;
                }
                this.groups = [];
                this.$axios.post(CONSTANT.REQUEST_URL.API_GROUP_FIND_LIST, this.query).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        let all = resp.data.data;
                        $element.innerHTML = JSON.stringify(all);
                        this.groupList2Tree(all);
                    }
                });
            },
            findAllApi() {
                let $element = document.getElementById('apisHidden');
                let apisHtml = $element.innerHTML;
                if (apisHtml !== '') {
                    this.filterDataList = this.dataList = JSON.parse(apisHtml);
                    return;
                }
                this.$axios.post(CONSTANT.REQUEST_URL.API_FIND_ALL_DETAIL, this.query).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.filterDataList = this.dataList = resp.data.data;
                        $element.innerHTML = JSON.stringify(this.dataList);
                    }
                });
            },
            filterApi() {
                let filterList = this.dataList;
                let search = this.query.nameOrUri;
                if (search && search !== '') {
                    search = search.toLowerCase();
                    filterList = filterList.filter(data =>
                        (data.name && data.name.toLowerCase().indexOf(search) !== -1)
                        || (data.apiUri && data.apiUri.toLowerCase().indexOf(search) !== -1));
                }
                if (this.query.groupIds.length > 0) {
                    filterList = filterList.filter(data => data.group && this.query.groupIds.indexOf(data.group.id) !== -1);
                }
                this.filterDataList = filterList;
            },
            selectQueryStatus(apiStatus) {
                this.query.apiStatus = apiStatus;
            },
            clickRow(row) {
                if (row.requestParamVO && row.requestParamVO.dataList) {
                    UTILS.fillShowList(row.requestParamVO.dataList, this.reqShowDataList, false, false);
                }
                if (row.responseParamVO && row.responseParamVO.dataList) {
                    UTILS.fillShowList(row.responseParamVO.dataList, this.respShowDataList, false, false);
                }
                this.selectedApi = row;
                this.showDetail = true;
            },
        },
        mounted() {
            this.findApiGroups();
            this.findAllApi();
            this.$nextTick(() => {
                this.tableHeight = window.innerHeight - 20;
            })
        },
    };
</script>
<style lang="stylus" rel="stylesheet/stylus">
    #exportHtml
        .el-main
            padding 0

        .export-aside
            border 1px solid #dcdcdc

        .api-group-header
            height 43px
            line-height 43px
            padding 0 10px
            text-align center
            font-size 14px
            background #f3f3f3
            border-bottom 1px solid #dcdcdc

        .select-all
            width 100%

        .el-tree-node__content
            height 32px

        .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content
            background-color #eee
    .api-edit-info
        margin-right 30px
    .json-content
        margin 10px
        padding 10px 0
        border-radius 4px
        text-align left
        background #e5e9f2
    .export-api-detail
        margin-right 0
</style>
