<template>
    <el-container id="app">
        <left-menu></left-menu>
    </el-container>
    <!--<el-container id="app">
        <el-aside :class="['aside-common',{'aside-collapse': isCollapse,'aside-unfold':!isCollapse}]">
            <div class="aside-menu">
                <el-menu
                        class="el-menu"
                        @open="handleOpen"
                        @close="handleClose"
                        :collapse="isCollapse"
                        active-text-color="#ffd04b">
                    <el-menu-item index="1">
                        <i class="el-icon-grape"></i>
                        <span slot="title">API Doc</span>
                    </el-menu-item>
                    <el-menu-item index="2">
                        <i class="el-icon-moon"></i>
                        <span slot="title">Environment Config</span>
                    </el-menu-item>
                    <el-menu-item index="3">
                        <i class="el-icon-coin"></i>
                        <span slot="title">Data Structure</span>
                    </el-menu-item>
                </el-menu>
            </div>
            <div class="aside-footer">
                <el-menu
                        class="el-menu"
                        @open="handleOpen"
                        @close="handleClose"
                        :collapse="isCollapse"
                        disabled>
                    <el-menu-item @click="isCollapse=!isCollapse">
                        <i :class="isCollapse?'el-icon-s-unfold':'el-icon-s-fold'"></i>
                        <span slot="title">{{isCollapse?'展开':'折叠'}}</span>
                    </el-menu-item>
                </el-menu>
                &lt;!&ndash; <el-button type="text" @click="isCollapse=!isCollapse">
                     <i :class="isCollapse?'el-icon-s-unfold':'el-icon-s-fold'"></i>
                     <span>{{isCollapse?'展开':'折叠'}}</span>
                 </el-button>&ndash;&gt;
            </div>
        </el-aside>

        <el-container>
            <el-header>
                &lt;!&ndash;                <el-dropdown>
                                    <i class="el-icon-setting" style="margin-right: 15px"></i>
                                    <el-dropdown-menu slot="dropdown">
                                        <el-dropdown-item>查看</el-dropdown-item>
                                        <el-dropdown-item>新增</el-dropdown-item>
                                        <el-dropdown-item>删除</el-dropdown-item>
                                    </el-dropdown-menu>
                                </el-dropdown>
                                <span>王小虎</span>&ndash;&gt;
            </el-header>

            <el-main>
                &lt;!&ndash;<el-table :data="tableData">
                    <el-table-column prop="date" label="日期" width="140">
                    </el-table-column>
                    <el-table-column prop="name" label="姓名" width="120">
                    </el-table-column>
                    <el-table-column prop="address" label="地址">
                    </el-table-column>
                </el-table>&ndash;&gt;
            </el-main>
        </el-container>
    </el-container>-->
</template>

<script>

    import leftMenu from "@/components/leftMenu/leftMenu";

    export default {
        name: 'app',
        components: {leftMenu},
        data() {
            const item = {
                date: '2019/12/31',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            };
            return {
                tableData: Array(20).fill(item),
                isCollapse: false
            }
        },
        // computed: {
        //     openOrClose() {
        //         return this.isCollapse?
        //     }
        // },
        methods: {
            handleOpen(key, keyPath) {
                console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                console.log(key, keyPath);
            }
        }
    }
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #app
        font-family 'Microsoft YaHei UI', Helvetica, Arial, sans-serif
        -webkit-font-smoothing antialiased
        -moz-osx-font-smoothing grayscale
        height 100%
        border 1px solid #eee
        text-align center
        color #2c3e50

    /*margin-top 60px*/

  /*  .el-header
        background-color #B3C0D1
        color #333
        line-height 60px
        text-align right
        font-size 12px

    .aside-common
        !*margin 0*!
        background-color rgb(238, 241, 246)
        color #333

        .aside-menu
            height 'calc(100% - %s)' % 40px

        .aside-footer
            position absolute
            bottom 0
            left 0
            height 40px
            line-height 41px
            border-top 1px solid #d9d9d9
            overflow hidden

    !*position absolute
    bottom 0
    left 0
    height 40px
    line-height 41px
    border-top 1px solid #d9d9d9
    text-indent 12px
    width 100%
    overflow hidden*!

    .aside-collapse
        width 50px

    .aside-unfold
        width 240px*/


</style>

