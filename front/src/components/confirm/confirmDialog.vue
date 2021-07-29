<template>
  <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show" width="450px" destroy-on-close>
    <div class="content">{{ dialog.content }}</div>
    <span class="dialog-footer" slot="footer">
                <el-button @click="dialog.show = false" round>Cancel</el-button>
                <el-button @click="submitForm" type="danger" round>Enter</el-button>
            </span>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import {UTILS} from "@/common/js/utils";

export default {
  name: 'confirmDialog',
  props: {
    dialog: {
      type: Object,
      default() {
        return {
          show: false,
          title: '',
          content: '',
          url: ''
        }
      }
    },
    form: Object
  },
  methods: {
    submitForm() {
      this.dialog.show = false;
      this.$axios.post(this.dialog.url, this.form).then(resp => {
        if (UTILS.checkResp(resp)) {
          this.$emit('flush');
        } else {
          this.$emit('error', resp.data);
        }
      });
    }
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
.content
  font-size: 18px
  font-weight: bold
  color: #F56C6C
  margin-left: auto
  margin-width: auto

.el-dialog__body
  padding 10px
</style>
