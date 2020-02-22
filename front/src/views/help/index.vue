<template>
    <el-collapse class="help">
        <el-collapse-item title="Initialize/初始化" class="collapse-title">
            <div class="content">
                you need install mongodb first, and set up config, default administrator account:
                admin/xyyxhcj@qq.com<br/>
                你需要安装mongodb，并且修改以下配置文件，默认的管理员账号: admin/xyyxhcj@qq.com
                <div class="title">config files:</div>
                front/vue.config.js<br/>
                front/src/common/js/constant.js<br/>
                ams/src/main/java/press/whcj/ams/common.SysConfig<br/>
                ams/src/main/resources/application.yml<br/>
                ams/src/main/resources/application-dev.yml
                <div class="title">build vue:</div>
                npm i<br/>
                npm run build
                <div class="title">run jar:</div>
                nohup java -jar ams.jar > vpiNohup.out 2>&1 &
            </div>
        </el-collapse-item>
        <el-collapse-item title="Main config desc/主要配置项说明" class="collapse-title">
            <div class="content">
                <img src="../../../public/static/img/configDesc1.png" alt=""/><br/>
                front/vue.config.js -> HOST_URL.prod: set jar access address<br/>
                front/src/common/js/constant.js -> HOST_URL.prod: set jar access address<br/>
                <br/>
                <img src="../../../public/static/img/configDesc2.png" alt=""/><br/>
                ams/src/main/java/press/whcj/ams/common.SysConfig -> ADMIN_PWD: default admin password<br/>
                <br/>
                <img src="../../../public/static/img/configDesc3.png" alt=""/><br/>
                ams/src/main/resources/application-dev.yml -><br/>
                address,database,username,password: mongodb access config<br/>
            </div>
        </el-collapse-item>
        <el-collapse-item title="Basic operation/基本操作" class="collapse-title">
            <div class="content">
                <div class="title">add user&auth/添加用户及授权:</div>
                <img src="../../../public/static/img/addUser&Auth.gif" alt=""/><br/>
                <br/>
                <div class="title">add project/添加项目:</div>
                <img src="../../../public/static/img/addProject.gif" alt=""/><br/>
                <br/>
                <div class="title">add apiGroup&api/添加接口组及接口:</div>
                <img src="../../../public/static/img/addApiGroup&Api.gif" alt=""/><br/>
                <br/>
                <div class="title">add data structure/添加数据结构:</div>
                <img src="../../../public/static/img/addApiGroup&Api.gif" alt=""/><br/>
                <br/>
                <div class="title">param use json import & reference data structure/接口参数使用json导入&引用数据结构:</div>
                <img src="../../../public/static/img/addApiGroup&Api.gif" alt=""/><br/>
                <br/>
            </div>
        </el-collapse-item>
        <el-collapse-item title="Api test/接口测试" class="collapse-title">
            <div class="content">
                <div class="title">
                    if need use test,need install chrome plugin<br/>
                    如果需要使用接口测试功能,需要安装chrome插件
                </div>
            </div>
        </el-collapse-item>
        <el-collapse-item title="Contact" class="collapse-title">
            <div class="content">
                Email: xyyxhcj@qq.com<br/>
                Wechat:<br/>
                <img src="../../../public/static/img/contact1.png" alt="" style="width: 200px;height: 200px"/>
            </div>
        </el-collapse-item>
    </el-collapse>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'index'
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .help
        .el-collapse-item__header
            padding-left 10px

            &.is-active
                color #409EFF

        .content
            padding 0 15px
            text-align left
            font-size 15px

        .title
            line-height 35px
            font-size 17px
            font-weight bold
</style>
