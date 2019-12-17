<template>
    <el-scrollbar wrap-class="scrollbar-wrapper">
        <el-menu class="aside-common"
                 :collapse="isCollapse" show-timeout="100"
                 :default-active="$route.path">
            <div class="aside-collapse">
                <el-menu-item type="text" @click="isCollapse=!isCollapse" style="width: 100%"
                              class="reset-padding-left">
                    <i :class="isCollapse?'el-icon-s-unfold':'el-icon-s-fold'"/>
                    <span slot="title">{{isCollapse?'展开':'折叠'}}</span>
                </el-menu-item>
            </div>
            <div>
                <template v-for="menu in MENUS">
                    <router-link :to="menu.path" :key="menu.name" v-if="!menu.noMenu">
                        <el-menu-item :index="menu.path" class="reset-padding-left">
                            <i :class="menu.meta.icon" aria-hidden="true"/>
                            <span v-if="menu.meta&&menu.meta.title" slot="title">{{menu.meta.title}}</span>
                        </el-menu-item>
                    </router-link>
                </template>
            </div>
        </el-menu>
    </el-scrollbar>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "@/common/js/constant";

    export default {
        name: 'leftMenu',
        data() {
            return {
                isCollapse: false,
                MENUS: CONSTANT.MENUS
            }
        },
        computed: {}
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .aside-common&.el-menu--collapse
        width 35px

    .aside-common
        min-width 34px
        text-align left
        margin-left 1px
        background-color rgb(238, 241, 246)
        color #333

        .reset-padding-left
            padding-left 6px !important

            .el-tooltip
                padding-left 6px !important

        .aside-collapse
            line-height 40px
            border-bottom 1px solid #d9d9d9
            overflow hidden
</style>
