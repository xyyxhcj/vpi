<template>
    <span style="margin: 0 10px 0 5px;font-size: 8px">
        <span>Use Data Structure： </span>
        <el-dropdown trigger="click" size="mini" @command="command">
            <span style="cursor:pointer;color: #409EFF;font-size: 8px">
                <template v-if="info.referenceStructureName.length>10">
                    <el-popover popper-class="api-doc-popover" placement="top-end" :close-delay="0"
                                trigger="hover">
                        <span style="padding:0;font-size:5px">{{info.referenceStructureName}}</span>
                        <span slot="reference">
                            {{ info.referenceStructureName.substr(0,10)+'...' }}
                        </span>
                    </el-popover>
                </template>
                <template v-if="info.referenceStructureName.length<=10">
                    {{info.referenceStructureName}}
                </template>
                <i class="el-icon-arrow-down el-icon--right"/>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="disconnectDataStructure">disconnect Data Structure</el-dropdown-item>
                <el-dropdown-item :command="editDataStructure">Edit Data Structure</el-dropdown-item>
                <el-dropdown-item :command="showDataStructure">
                    reference other data structure
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </span>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'referenceInfo',
        props: {
            index: {
                type: Number,
                default: undefined
            },
            info: {
                type: Object,
                default: undefined
            }
        },
        methods: {
            command(func) {
                if (func) {
                    func();
                }
            },
            showDataStructure() {
                this.$emit('showDataStructure', this.index, this.info);
            },
            editDataStructure() {
                this.$emit('editDataStructure', this.info.referenceStructureId);
            },
            disconnectDataStructure() {
                this.$emit('disconnectDataStructure', this.info);
            },
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
