<template>
    <div class="container">
        <el-pagination background
                :current-page="query.page.current"
                :page-size="query.page.size"
                :page-sizes="CONSTANT.CONFIG.PAGE_SIZES"
                :total="query.page.total"
                @current-change="currentChange"
                @size-change="pageSizeChange"
                layout="total, sizes, prev, pager, next">
        </el-pagination>
    </div>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";

    export default {
        name: 'pageTemplate',
        props: {
            query: Object
        },
        data() {
            return {CONSTANT: CONSTANT}
        },
        methods: {
            pageSizeChange(val) {
                this.query.page.size = val;
                this.$emit('flush');
            },
            currentChange(val) {
                this.query.page.current = val;
                this.$emit('flush');
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .container
        padding-top: 10px
        background-color: #fff
</style>
