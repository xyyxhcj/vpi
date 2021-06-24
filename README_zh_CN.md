在线API接口管理平台，提供API自动化测试、团队协作等功能，旨在解决由于前后端分离导致的开发效率低下问题
本项目为前后端分离项目
#### 特性：
1. 支持主流请求方式，并且提供了强大的版本管理功能，可以随时随地回滚API信息。同时支持数据结构管理、状态码管理等常用管理功能。

2. API接口测试，支持在线、跨域、自动化测试等功能。配合测试用例可以非常方便地对比请求结果与模型，找出API可能出现的问题。

3. API自动化测试，在UI界面模式下，你不需要编写任何代码即可创建数据相互关联的API测试用例。简化了开发测试人员的API测试工作，每次开发完成只需要一个键即可自动测试所有API，帮助了解项目API的健康状况。

4. 强大的团队协作功能，你可以通过URL快速邀请成员或者加入某个项目，提供了全面的日志追踪以及权限管理功能
   
        待实现功能：
        文档分享和导出，在线生成接口文档。
        支持Swagger、Postman、RAP、RestClint等数据导入，无需重新录入API信息，一键导入即可切换平台


### 快速入门
#### 部署要求
- JDK 8+
- mongodb-org-4.0(init/install-mongodb.sh)
- nginx
- chrome插件(可选)
#### 构建工具
- git
- maven
- node

可以使用 <a href="sh/makeJar.sh">sh/makeJar.sh</a> 进行项目配置，启动！
#### 配置文件:
<a href="front/.env.production">front/.env.production</a>  
<a href="ams/src/main/java/press/whcj/ams/common/Constant.java">ams/src/main/java/press/whcj/ams/common/Constant.java#SysConfig</a>  
<a href="ams/src/main/resources/application-prod.yml">ams/src/main/resources/application-prod.yml</a>  

#### 演示地址:
http://42.192.227.159  
测试账号/密码: lx/123456
#### 帮助文档:  
http://42.192.227.159/help  

