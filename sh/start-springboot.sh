#! /bin/bash
# build vue project,and put into jar(springboot),without nginx
# config variable dbConfig:../conf/application-prod.yml
chromePluginDownloadUrl='https://www.whcj.press/vpiChromePlugin.zip'

cd ../front || exit

# replace vue parameter
sed -i "s|\$chromePluginDownloadUrl|${chromePluginDownloadUrl}|g" .env.production

# build vue
npm cache clean --force
npm config set registry http://registry.npmjs.org/
rm -rf package-lock.json
npm install
npm run build

# move to jar
rm -rf ../ams/src/main/resources/static && mkdir ../ams/src/main/resources/static
mv dist/* ../ams/src/main/resources/static

# build jar
cd ../ams || exit
mvn clean install -Dmaven.test.skip=true
# run(add ext config)
mv target/ams.jar ../ && cd ..
nohup java -jar ams.jar --spring.config.location=classpath:/application.yml,conf/application-prod.yml > vpi.log 2>&1 &
