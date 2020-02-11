<template>
    <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="75%">
        <el-table :data="dataList" tooltip-effect="dark" style="width: 100%" ref="userTable" row-key="id"
                  @selection-change="selectedChange">
            <el-table-column type="selection" width="55" :selectable="notCreate" :reserve-selection="true">
            </el-table-column>
            <el-table-column prop="userName" label="userName" width="150"/>
            <el-table-column prop="loginName" label="loginName" width="150"/>
            <el-table-column prop="phone" label="phone" width="150"/>
            <el-table-column prop="email" label="email" width="150"/>
        </el-table>
        <page-template :query="query" @flush="findPage"/>
        <div slot="footer" class="footer-container">
            <el-button @click="dialog.show = false" round>Cancel</el-button>
            <el-button @click="setUsers" type="primary" round>Submit</el-button>
        </div>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";
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
                            let isSelected = UTILS.contains($this.selectedList, create, function (selectedElement, element) {
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
                    let isSelected = UTILS.contains(obj.selectedList, row, function (selectedElement, element) {
                        return selectedElement.id === element.id;
                    });
                    if (!isSelected) {
                        obj.selectedList.push(row);
                        obj.$refs['userTable'].toggleRowSelection(row);
                    }
                });
            },
            notCreate(row) {
                return row.id !== this.user.id;
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
