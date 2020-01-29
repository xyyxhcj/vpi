<template>
    <div id="api-view-container" style="min-width: 1250px">
        <el-container>
            <el-header>Header</el-header>
            <el-main>Main</el-main>
        </el-container>
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'detail',
        created() {
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #api-view-container
        border 1px solid #d9d9d9

        .el-header
            background-color #B3C0D1
            color #333
            text-align center
            line-height 60px

        .el-main
            background-color #E9EEF3
            color #333
            text-align center
            line-height 160px

</style>
