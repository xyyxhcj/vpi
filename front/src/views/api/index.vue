<template>
    <el-container id="api-container" style="min-width: 1250px">
        <el-header height="35px" style="padding:3px 5px;;text-align: left;line-height: 25px;">
            <template v-if="$route.path==='/api/edit'">
                <el-button type="success" size="mini" @click="save">Save</el-button>
                <el-button size="mini" @click="$router.go(-1)">Cancel</el-button>
            </template>
            <template v-if="$route.path==='/api/detail'">
                <el-link class="a-link">Test</el-link>
                <el-link class="a-link">Edit</el-link>
                <el-link class="a-link">Copy</el-link>
                <el-link class="a-link">Delete</el-link>
            </template>
        </el-header>
        <el-main>
            <router-view ref="api-sub-router"/>
        </el-main>
    </el-container>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'index',
        methods: {
            save() {
                this.$refs['api-sub-router'].save();
            }
        },
        created() {
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #api-container
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

        .a-link
            margin 5px
</style>
