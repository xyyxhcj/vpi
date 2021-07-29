<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show" destroy-on-close
               :close-on-click-modal="false" center width="75%">
        <el-table :data="dataList" tooltip-effect="dark" style="width: 100%" ref="userTable" row-key="id"
                  @selection-change="selectedChange">
            <el-table-column type="selection" width="55" :selectable="notCurrUser" :reserve-selection="true">
            </el-table-column>
            <el-table-column prop="userName" label="User name" width="150"/>
            <el-table-column prop="loginName" label="Login name" width="150"/>
            <el-table-column prop="phone" label="Phone" width="150"/>
            <el-table-column prop="email" label="Email" width="150"/>
        </el-table>
        <page-template :query="query" @flush="()=>dialog.forDel?findPageForDelete():findPage()"/>
        <div slot="footer" class="footer-container">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button v-if="!dialog.forDel" @click="setUsers" type="primary" round>Submit</el-button>
            <el-button v-else @click="delUsers" type="danger" round>Delete</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";
import {UTILS} from "@/common/js/utils";
import PageTemplate from "../pageTemplate/pageTemplate";

export default {
        name: 'selectUserDialog',
        components: {PageTemplate},
        props: {
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                        title: '',
                        url: '',
                        project: Object,
                        forDel: false,
                    };
                }
            },
        },
        data() {
            return {
                user: this.$store.getters.user,
                CONSTANT,
                dataList: [],
                query: {
                    page: {
                        current: 1,
                        size: CONSTANT.CONFIG.PAGE_SIZE_DEFAULT,
                        total: 0,
                    }
                },
                projectUserQuery: {
                    projectId: '',
                },
                selectedList: [],
            }
        },
        computed: {},
        created() {
        },
        methods: {
            setUsers() {
                for (let i = this.selectedList.length - 1; i >= 0; i--) {
                    let selected = this.selectedList[i];
                    if (selected.id === this.user.id) {
                        this.selectedList.splice(i, 1);
                        continue;
                    }
                    selected.userId = selected.id;
                    if (!selected.userType) {
                        selected.userType = CONSTANT.AUTH_ROLE.READ;
                    }
                }
                this.$axios.post(this.dialog.url, {
                    id: this.dialog.project.id,
                    projectUsers: this.selectedList
                }).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.dialog.show = false;
                        // clear selected
                        this.selectedList = [];
                        this.$refs['userTable'].clearSelection();
                        this.$nextTick(() => {
                            this.$emit('flush');
                        });
                    }
                });
            },
            delUsers() {
                let ids = [];
                this.selectedList.forEach(selected => {
                    if (selected.id !== this.user.id) {
                        ids.push(selected.id);
                    }
                });
                this.$axios.post(this.dialog.url, {ids: ids}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.dialog.show = false;
                        // clear selected
                        this.selectedList = [];
                        this.$refs['userTable'].clearSelection();
                        this.$nextTick(() => {
                            this.$emit('flush');
                        });
                    }
                });
            },
            findPage() {
                this.projectUserQuery.projectId = this.dialog.project.id;
                this.$axios.post(CONSTANT.REQUEST_URL.PROJECT_FIND_PROJECT_USER, this.projectUserQuery).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        UTILS.findPage(this, this, CONSTANT.REQUEST_URL.USER_FIND_PAGE, function ($this) {
                            $this.selectRows(resp.data.data, $this);
                            let create = {id: $this.user.id};
                          let isSelected = UTILS.contains($this.selectedList, create, (selectedElement, element) => {
                            return selectedElement.id === element.id;
                          });
                            if (!isSelected) {
                                $this.selectedList.push(create);
                                $this.$refs['userTable'].toggleRowSelection(create);
                            }
                        });
                    }
                });
            },
            selectedChange(selectedRows) {
                this.selectedList = selectedRows;
            },
            selectRows(rows, obj) {
                let dataDict = {};
                obj.dataList.forEach(data => dataDict[data.id] = data);
                rows.forEach(row => {
                    let dataDictElement = dataDict[row.id];
                    if (dataDictElement && row.userType !== undefined && row.userType !== null) {
                        dataDictElement.userType = row.userType;
                    }
                  let isSelected = UTILS.contains(obj.selectedList, row, (selectedElement, element) => {
                    return selectedElement.id === element.id;
                  });
                    if (!isSelected) {
                        obj.selectedList.push(row);
                        obj.$refs['userTable'].toggleRowSelection(row);
                    }
                });
            },
            notCurrUser(row) {
                return row.id !== this.user.id;
            },
            findPageForDelete() {
                UTILS.findPage(this, this, CONSTANT.REQUEST_URL.USER_FIND_PAGE)
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .footer-container
        text-align left

    .selectInput
        margin-right 100px
        width 500px
</style>
