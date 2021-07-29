<template>
  <div class="container">
    <el-button class="page-content" icon="el-icon-arrow-left" @click="prevEvent" size="mini"
               :disabled="query.page.current === 1"></el-button>
    <span class="page-content">{{ this.query.page.current }}</span>
    <el-button class="page-content" icon="el-icon-arrow-right" @click="nextEvent" size="mini"
               :disabled="query.page.total < size"></el-button>
  </div>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "@/common/js/constant";

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
          prevEvent() {
            this.query.page.current = this.query.page.current - 1;
            this.$emit('flush');
          },
          nextEvent() {
            this.query.page.current = this.query.page.current + 1;
            this.$emit('flush');
          },
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
.container
  padding 7px
  background-color #fff
  .page-content
    padding 7px
</style>
