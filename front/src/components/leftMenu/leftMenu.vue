<template>
    <el-menu class="aside-common"
             :collapse="isCollapse" show-timeout="100" hide-timeout="10"
             :default-active="$route.path">
        <div class="aside-collapse" ref="aside-collapse">
            <el-menu-item type="text" @click="isCollapse=!isCollapse"
                          class="reset-padding-left">
                <i :class="isCollapse?'el-icon-s-unfold':'el-icon-s-fold'"/>
                <span slot="title">{{isCollapse?'unfold':'collapse'}}</span>
            </el-menu-item>
        </div>
        <div>
            <template v-for="menu in MENUS">
                <router-link :to="menu.path" :key="menu.path" v-if="!menu.noMenu">
                    <el-menu-item :index="menu.path" class="reset-padding-left">
                        <i :class="menu.meta.icon" aria-hidden="true"/>
                        <span v-if="menu.meta&&menu.meta.title" slot="title">{{menu.meta.title}}</span>
                    </el-menu-item>
                </router-link>
            </template>
        </div>
    </el-menu>
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
        mounted() {
            let resizeTimer = null;
            let $this = this;
            window.onresize = function () {
                if (resizeTimer) {
                    clearTimeout(resizeTimer)
                }
                resizeTimer = setTimeout(function () {
                    if (!$this.isCollapse && $this.$refs['aside-collapse'].offsetWidth < 185) {
                        $this.isCollapse = true;
                    }
                }, 400);
            }
        },
        computed: {},
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .aside-common&.el-menu--collapse
        width 35px

    .aside-common
        min-width 34px
        text-align left
        margin-left 1px
        background-color whitesmoke
        box-shadow: 1px 0 1px #bdbdbd;

        .reset-padding-left
            padding-left 6px !important

            .el-tooltip
                padding-left 6px !important

        .el-menu-item.reset-padding-left.is-active
            background-color #e5e7e8

        .aside-collapse
            line-height 40px
            overflow hidden

            .is-active
                color #303133
</style>
