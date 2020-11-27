<template>
    <div class="container">
    <el-button icon="el-icon-arrow-left" style="padding:10px" @click="prevEvent" :disabled="this.query.page.current == 1 ? true:false" ></el-button>
     {{this.query.page.current}}
    <el-button icon="el-icon-arrow-right" style="padding:10px" @click="nextEvent" :disabled="this.query.page.total < 20 ? true : false"></el-button>
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
            return {
            CONSTANT: CONSTANT,
            total: 0,
            size: 20,
            currentPage: 1
            }
        },
        methods: {
            prevEvent(){
            this.query.page.current = this.query.page.current-1;
            this.$emit('flush');
            },
            nextEvent(){
            this.query.page.current = this.query.page.current+1;
            this.$emit('flush');
            },
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .container
        padding: 10px
        background-color: #fff
</style>
