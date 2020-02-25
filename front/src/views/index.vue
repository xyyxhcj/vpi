<template>
    <div id="project-container">
        <el-collapse v-model="defaultCollapse" accordion @change="clickCollapse">
            <el-collapse-item title="My Project" name="my">
                <group-project-table :config="ownerTableConfig" ref="owner-table"/>
            </el-collapse-item>
            <el-collapse-item title="Other Project" name="other">
                <group-project-table :config="otherTableConfig" ref="other-table"/>
            </el-collapse-item>
        </el-collapse>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../common/js/constant";
    import GroupProjectTable from "../components/groupProjectTable/groupProjectTable";

    export default {
        name: 'index',
        components: {GroupProjectTable},
        data() {
            return {
                CONSTANT,
                user: this.$store.getters.user,
                tableData: [],
                selectedGroup: undefined,
                defaultCollapse: 'my',
                ownerTableConfig: {
                    reqUri: CONSTANT.REQUEST_URL.PROJECT_FIND_LIST_BY_GROUP_FOR_OWNER,
                    isOwner: true,
                    refPre:'owner',
                },
                otherTableConfig: {
                    reqUri: CONSTANT.REQUEST_URL.PROJECT_FIND_LIST_BY_GROUP_FOR_OTHER,
                    isOwner: false,
                    refPre:'other',
                },
            };
        },
        methods: {
            findListByOwner() {
                this.$refs['owner-table'].findList();
            },
            clickCollapse(activeNames) {
                if ('my' === activeNames) {
                    this.findListByOwner();
                } else {
                    this.$refs['other-table'].findList();
                }
            }
        },
        mounted() {
            this.findListByOwner();
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    #project-container
        border 1px solid #d9d9d9
        background-color white
        .el-collapse-item__header
            padding-left 10px
            font-size 18px
            font-weight 400
            background-color #F2F6FC
            &.is-active
                color #409EFF
        .el-collapse-item__content
            padding-bottom 0

        .el-button
            padding 5px 5px
            margin-top: 1.5px
</style>
