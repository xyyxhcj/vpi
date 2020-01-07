<template>
    <el-dialog :append-to-body="true" :visible.sync="dialog.show"
               :close-on-click-modal="false" center width="78%" @submit.native.prevent>
        <el-container>
            <el-aside width="200px">
                <el-menu class="auth-aside" :default-active="$route.path">
                    <div>
                        <template v-for="menu in MENUS">
                            <router-link :to="menu.path" :key="menu.name" v-if="!menu.noMenu">
                                <el-menu-item :index="menu.path" class="reset-padding-left">
                                    <span v-if="menu.meta&&menu.meta.title" slot="title">{{menu.meta.title}}</span>
                                </el-menu-item>
                            </router-link>
                        </template>
                    </div>
                </el-menu>
            </el-aside>
            <el-container>
                <el-header>1</el-header>
                <el-main>2</el-main>
            </el-container>
        </el-container>
    </el-dialog>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "@/common/js/constant";

    export default {
        name: 'editAuthDialog',
        props:{
            dialog: {
                type: Object,
                default() {
                    return {
                        show: false,
                    }
                }
            },
        },
        data() {
            return {
                MENUS: CONSTANT.MENUS
            }
        },
        computed: {}
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .auth-aside
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
</style>
