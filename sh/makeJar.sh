#! /bin/bash
### need edit Start
# mongodb 连接信息
mongodbAddress='127.0.0.1:27017'
mongodbDatabase='github-ams'
mongodbUsername='vpi'
mongodbPassword='LYyVKh7spO7hzDLv'
# 后端接口地址 spring project url prefix
prodApiUrl='http://42.192.227.159:11111'
# 谷歌插件下载地址
chromePluginDownloadUrl='https://42.192.227.159/vpiChromePlugin.zip'

# 项目根目录
projectDir='/opt/project/vpi/'
# jar存放目录
jarSaveDir='/usr/local/'

### need edit End
if  [ "$projectDir" = "" ] ;then
    echo "Missing parameter: projectDir!"
    return
fi
if  [ "$jarSaveDir" = "" ] ;then
    echo "Missing parameter: jarSaveDir!"
    return
fi

# git pull
cd ${projectDir} || exit
git fetch --all
git reset --hard develop
git pull

# replace parameter
sed -i "s|\$mongodbAddress|${mongodbAddress}|g" ${projectDir}ams/src/main/resources/application-prod.yml
sed -i "s|\$mongodbDatabase|${mongodbDatabase}|g" ${projectDir}ams/src/main/resources/application-prod.yml
sed -i "s|\$mongodbUsername|${mongodbUsername}|g" ${projectDir}ams/src/main/resources/application-prod.yml
sed -i "s|\$mongodbPassword|${mongodbPassword}|g" ${projectDir}ams/src/main/resources/application-prod.yml
sed -i "s|\$prodApiUrl|${prodApiUrl}|g" ${projectDir}front/.env.production
sed -i "s|\$chromePluginDownloadUrl|${chromePluginDownloadUrl}|g" ${projectDir}front/.env.production

# build
cd front || exit

npm cache clean --force
npm config set registry http://registry.npmjs.org/
rm -rf package-lock.json

npm install
npm run build
cd ${projectDir}chromePlugin && zip -r vpiChromePlugin.zip ./*
cd ${projectDir}ams && mvn clean package -DskipTests

# close old
appPid=$(netstat -ntlp | grep 11111 | awk '{print $7}' | head -1 | grep '[0-9]\+' -o)
if  [ "$appPid" != "" ] ;then
    kill "${appPid}"
    echo "killed ${appPid}"
    sleep 15s
fi

# move to nginx file & start

mv ${projectDir}chromePlugin/vpiChromePlugin.zip ${projectDir}front/dist/
mv ${projectDir}ams/target/ams.jar ${jarSaveDir}
cd ${jarSaveDir} || exit
nohup java -jar -XX:+HeapDumpOnOutOfMemoryError ${jarSaveDir}ams.jar > vpiNohup.out 2>&1 &
tail -f ${jarSaveDir}vpiNohup.out
