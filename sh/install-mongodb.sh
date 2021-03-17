#! /bin/bash
cat>/etc/yum.repos.d/mongodb-org-4.0.repo<<EOF
[mongodb-org-4.0]
name = MongoDB Repository
baseurl = https://repo.mongodb.org/yum/redhat/\$releasever/mongodb-org/4.0/x86_64/
gpgcheck = 1
enabled = 1
gpgkey = https://www.mongodb.org/static/pgp/server-4.0.asc
EOF
yum makecache
sudo yum install -y mongodb-org

<<'COMMENT'
...
#edit config
vim /etc/mongod.conf
net:
  bindIp: 0.0.0.0
#security:
#  authorization: enabled
COMMENT

#enable: mongod
#mongod -f /etc/mongod.conf
#mongod -f /etc/mongod.conf --shutdown
#客户端连接:

<<'COMMENT'
...
# create account
mongo
use admin
db.createUser(
  {
    user: "root",
    pwd: "$rootPassword",
    roles: [
       { role: "userAdminAnyDatabase", db: "admin" }
    ]
  }
)
db.createUser({user:"vpi",pwd:"LYyVKh7spO7hzDLv",roles:[{role:"dbOwner",db:"github-ams"}]})
db.grantRolesToUser('vpi',[{ role: "readWrite", db: "github-ams" }])

COMMENT
