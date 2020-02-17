<template>
    <el-dropdown size="mini" trigger="click" :hide-timeout="10" @command="command" placement="bottom">
        <span class="more-operate">
            <i class="el-icon-more"/>
        </span>
        <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="()=>showDataStructure()">reference data structure
            </el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'moreOperate',
        props: {
            info: {
                index: undefined,
                row: undefined,
                showDataStructure: undefined
            }
        },
        methods: {
            command(func) {
                func();
            },
            showDataStructure() {
                this.info.showDataStructure(this.info.index, this.info.row);
            }
        },
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
