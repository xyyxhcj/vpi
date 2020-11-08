<template>
  <el-dialog :append-to-body="true" :title="dialog.title" :visible.sync="dialog.show"
             :close-on-click-modal="false" center width="35%">
    <el-form :model="form" ref="form" label-width="120px" :rules="form_rules">
      <el-form-item label="password" prop="password">
        <el-input v-model.trim="form.password" type="password"/>
      </el-form-item>
      <el-form-item label="rePassword" prop="rePassword">
        <el-input v-model.trim="form.rePassword" type="password"/>
      </el-form-item>
      <el-form-item label="old password" prop="oldPwd">
        <el-input v-model.trim="form.oldPwd" type="password"/>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="dialog.show = false" round>Cancel</el-button>
      <el-button @click="submitForm" type="primary" round>Submit</el-button>
    </div>
  </el-dialog>
</template>

<script type="text/ecmascript-6">
import {UTILS} from "../../common/js/utils";

export default {
  name: 'editPasswordDialog',
  props: {
    dialog: {
      type: Object,
      default() {
        return {
          show: false,
          title: '',
          url: ''
        }
      }
    },
    form: {
      type: Object,
      default() {
        return {
          password: '',
          rePassword: '',
          oldPwd: '',
        }
      }
    }
  },
  data() {
    let validateRePassword = (rule, value, callback) => {
      console.log(value, this.form.password);
      if (value !== this.form.password) {
        callback(new Error('re-password error'));
      } else {
        callback();
      }
    };
    return {
      form_rules: {
        password: [
          {required: true, message: 'enter password'}
        ],
        rePassword: [
          {required: true, validator: validateRePassword, trigger: 'blur'},
        ],
        oldPwd: [
          {required: true, message: 'enter old password'}
        ],
      }
    };
  },
  methods: {
    submitForm() {
      let form = this.$refs['form'];
      form.validate((valid) => {
        if (valid) {
          this.$axios.post(this.dialog.url, this.form).then(resp => {
            UTILS.showResult(this, resp, () => form.resetFields());
          });
        }
      });
    }
  }
};
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
