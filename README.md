Vpi is a powerful All-in-one API management and testing platform, we devote to making APIs Management easier!  
<a href="README_zh_CN.md">中文文档</a>  

# Getting Started
* The separation of front-end and backend   
use <a href="sh/makeJar.sh">sh/makeJar.sh</a>
    #### Config:  
        front/.env.production
        ams/src/main/java/press.whcj.ams.common.Constant.SysConfig
        ams/src/main/resources/application-prod.yml    
    #### Dependencies: 
        jdk1.8  
        chromePlugin(Optional)    
        mongodb(init/install-mongodb.sh)    
        nginx  
        maven
        node(npm7.5.4)

* Build an application with a monolithic architecture(put the html into jar after building)     
  use <a href="sh/start-springboot.sh">sh/start-springboot.sh</a>   
    
    #### Dependencies: 
        jdk1.8  
        chromePlugin(Optional)
        mongodb(init/install-mongodb.sh)    
        maven(Optional)
        node(npm7.5.4)(Optional)
  *Version v1.03+, run jar with your configuration of mongoDB*
    ```
    # mongoDB configurations: conf/application-prod.yml
    nohup java -jar ams.jar --spring.config.location=classpath:/application.yml,conf/application-prod.yml > vpi.log 2>&1 &
    ```

## Demo:   
http://www.whcj.press  
Test account: lx/123456  
## Help:    
http://www.whcj.press/#/help
