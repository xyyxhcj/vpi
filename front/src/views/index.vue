<template>
    <div id="project-container">
        <el-table
                :data="tableData"
                :header-cell-style="{color:'#44B549','font-weight':'bold'}">
            <el-table-column label="name" prop="name"/>
            <el-table-column label="projectVersion" prop="projectVersion" width="120"/>
            <el-table-column label="projectType" prop="projectType" width="100"/>
            <el-table-column label="desc" prop="desc"/>
            <el-table-column label="updateTime" prop="updateTime" width="200"/>
            <el-table-column>
                <!-- eslint-disable-next-line vue/no-unused-vars -->
                <template slot="header" slot-scope="scope">
                    <el-button size="mini" type="success" @click="addProjectGroup">Add ProjectGroup</el-button>
                    <el-button size="mini" type="success" @click="addProject">Add Project</el-button>
                    <el-button size="mini" type="warning" @click="batchOperate">batch operate</el-button>
                </template>
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            @click="edit(scope.$index, scope.row)">Edit
                    </el-button>
                    <el-button
                            size="mini"
                            type="danger"
                            @click="del(scope.$index, scope.row)">Delete
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--        <env-config-edit-dialog :dialog="editDialog" :form="form"/>-->
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'index',
        data() {
            return {
                editDialog: {
                    show: false,
                    title: '',
                    url: ''
                },
                form: {},
                tableData:[],
            };
        },
        methods: {
            edit() {

            },
            del() {

            },
            addProjectGroup() {

            },
            addProject() {

            },
            batchOperate() {

            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #project-container
        border 1px solid #d9d9d9
        background-color white
</style>
