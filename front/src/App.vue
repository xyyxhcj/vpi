<template>

    <el-container id="app">
        <left-menu/>
        <el-container>
            <el-header class="header-common">
                <el-row>
                    <el-col :span="10">
                        <div class="project-path">{{$route.name}} > myTest</div>
                    </el-col>
                    <el-col :span="4">
                        <div class="project-name">
                            <el-dropdown @command="changeProject">
                              <span class="el-dropdown-link">
                                项目名称1111111111111<i class="el-icon-arrow-down el-icon--right"/>
                              </span>
                                <el-dropdown-menu>
                                    <el-dropdown-item>switch project</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </el-col>
                    <el-col :span="10">
                        <div class="user-info">vpi</div>
                    </el-col>
                </el-row>
            </el-header>
            <el-main class="main-common">
                <router-view/>
            </el-main>
        </el-container>
        <login-dialog :dialog="loginDialog"/>
    </el-container>
</template>

<script>

    import leftMenu from "@/components/leftMenu/leftMenu";
    import LoginDialog from "./components/login/loginDialog";
    // import {CONSTANT} from "./common/js/constant";
    // import {utils} from "./common/js/utils";

    export default {
        name: 'app',
        components: {LoginDialog, leftMenu},
        data() {
            return {
                loginDialog: {
                    show: !this.$store.getters.loginAuth,
                },
            }
        },
        methods: {
            changeProject() {
                this.$router.push('/');
            }
        },
        computed: {},
        created() {
            // this.$axios.post(CONSTANT.REQUEST_URL.USER_UPDATE)
        }
    }
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #app
        font-family 'Source Han Sans CN', Helvetica, Arial, sans-serif
        -webkit-font-smoothing antialiased
        -moz-osx-font-smoothing grayscale
        height 100%
        border 1px solid #eee
        text-align center
        color #2c3e50

    .header-common
        max-height 40px
        padding 13px 20px
        border-bottom 1px solid #d9d9d9
        font-size 14px
        background-color white

        .project-path
            text-align left

        .project-name
            .el-dropdown-link
                cursor: pointer
                color: #409EFF

            .el-icon-arrow-down
                font-size: 12px

        .user-info
            text-align right

    .main-common
        padding 9px
        background-color rgb(245, 245, 245)

    /*margin-top 60px*/

    /*  .el-header
          background-color #B3C0D1
          color #333
          line-height 60px
          text-align right
          font-size 12px

      .aside-common
          !*margin 0*!
          background-color rgb(238, 241, 246)
          color #333

          .aside-menu
              height 'calc(100% - %s)' % 40px

          .aside-footer
              position absolute
              bottom 0
              left 0
              height 40px
              line-height 41px
              border-top 1px solid #d9d9d9
              overflow hidden

      !*position absolute
      bottom 0
      left 0
      height 40px
      line-height 41px
      border-top 1px solid #d9d9d9
      text-indent 12px
      width 100%
      overflow hidden*!

      .aside-collapse
          width 50px

      .aside-unfold
          width 240px*/


</style>

