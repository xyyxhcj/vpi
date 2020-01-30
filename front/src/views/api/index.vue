<template>
    <el-container id="api-container" style="min-width: 1250px">
        <el-header height="35px" style="padding:3px 5px;;text-align: left;line-height: 25px;">
            <template v-if="$route.path==='/api/edit'">
                <el-button type="success" size="mini">Save</el-button>
                <el-button size="mini" @click="$router.go(-1)">Cancel</el-button>
            </template>
            <template v-if="$route.path==='/api/detail'">
                <el-link>Edit</el-link>
                <el-link>Copy</el-link>
                <el-link>Delete</el-link>
            </template>
        </el-header>
        <el-main>
            <router-view/>
        </el-main>
    </el-container>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'index',
        created() {
            console.log(this.$route.path);
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

</style>
