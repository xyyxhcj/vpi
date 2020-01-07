<template>
    <div id="env-container">
        <el-table :data="tableData" :header-cell-style="{color:'#44B549','font-weight':'bold'}">
            <el-table-column label="name" prop="name"/>
            <el-table-column label="desc" prop="desc"/>
            <el-table-column>
                <!-- eslint-disable-next-line vue/no-unused-vars -->
                <template slot="header" slot-scope="scope">
                    <el-row :gutter="11">
                        <el-col :span="8" style="margin-bottom: 5px">
                            <el-input
                                    v-model="search" size="mini" placeholder="enter keyword search"/>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="24">
                            <el-button size="mini" type="success" @click="addEnvConfig">Add</el-button>
                            <el-button size="mini" type="warning" @click="batchOperate">batch operate</el-button>
                        </el-col>
                    </el-row>
                </template>
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            @click="editEnvConfig(scope.$index, scope.row)">Edit
                    </el-button>
                    <el-button
                            size="mini"
                            type="danger"
                            @click="deleteEnvConfig(scope.$index, scope.row)">Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <edit-env-config-dialog :dialog="editDialog" :form="form"/>
    </div>
</template>

<script type="text/ecmascript-6">

    import EditEnvConfigDialog from "./editEnvConfigDialog";

    export default {
        name: 'index',
        components: {EditEnvConfigDialog},
        data() {
            return {
                envConfigList: [{
                    id: 'aa01',
                    name: '本地',
                    desc: '本地',
                    frontUri: '127.0.0.1'
                }, {
                    id: 'aa02',
                    name: '阿里',
                    desc: '',
                    frontUri: '79.2.35.1'
                }, {
                    id: 'aa03',
                    name: '腾讯',
                    desc: '腾讯云',
                    frontUri: '66.66.66.1'
                }, {
                    id: 'aa04',
                    name: '测试云',
                    desc: '测试',
                    frontUri: 'http://77.55.2.2'
                }],
                search: '',
                editDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                form: {},
            }
        },
        computed: {
            tableData() {
                return this.envConfigList.filter(data => {
                    if (!this.search) {
                        return true;
                    }
                    let name = data.name.toLowerCase();
                    let desc = data.desc ? data.desc.toLowerCase() : '';
                    let frontUri = data.frontUri ? data.frontUri.toLowerCase() : '';
                    let search = this.search.toLowerCase();
                    return name.includes(search) || desc.includes(search) || frontUri.includes(search);
                });
            }
        },
        methods: {
            editEnvConfig(index, row) {
                console.log(index, row);
            },
            deleteEnvConfig(index, row) {
                console.log(index, row);
            },
            addEnvConfig() {
                this.editDialog = {
                    show: true,
                    title: 'Add',
                    // cjTodo 2019/12/30
                    url: 'tod'
                };
                this.form = Object();
            },
            batchOperate() {
                console.log('batchOperate');
            },
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #env-container
        border 1px solid #d9d9d9
</style>
