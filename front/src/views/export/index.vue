<template>
    <!--    export all api-->
    <el-container id="exportHtml">
        <el-aside width="200px">
            <el-input placeholder="search" v-model="queryParam"/>
            <div class="api-group-header">
                Group
            </div>
            <div class="select-all" @click="selectGroup">all</div>
            <el-tree :data="groups" :props="{label:'name',children:'childList'}" :expand-on-click-node="false"
                     node-key="id" default-expand-all @node-click="selectGroup"
                     draggable highlight-current>
                <span class="api-group-node" slot-scope="{node,data}">
                    <span style="float:left;padding-left: 1px">
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
        <el-main>Main</el-main>
    </el-container>
</template>

<script type="text/ecmascript-6">
    import {UTILS} from "../../common/js/utils";
    import {CONSTANT} from "../../common/js/constant";

    export default {
        name: 'index',
        data() {
            return {
                CONSTANT,
                projectId: this.$store.getters.selectedProjectId,
                projectName: this.$store.getters.selectedProjectName,
                groups: [],
                queryParam: '',
                activeTab: 'api',

            };
        },
        methods: {
            exportHtml() {
                let val = document.querySelector('textarea').value;
                let blob = new Blob([val]);
                this.href = URL.createObjectURL(blob);
            },
            command(func) {
                if (func) {
                    func();
                }
            },
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
            findApiGroups() {
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
                });
            },
            findApiPage() {
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
                UTILS.findPage(this, this, CONSTANT.REQUEST_URL.API_FIND_PAGE);
            },
            clickRow(row) {
            },
        },
        created() {
            this.findApiGroups();
            //this.findApiPage();
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #exportHtml
        .api-group-header
            height 43px
            line-height 43px
            padding 0 10px
            font-size 14px
            background #f3f3f3
            border-bottom 1px solid #dcdcdc

        .select-all
            height 30px
            line-height 30px
            cursor pointer
</style>
