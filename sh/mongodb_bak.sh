#!/bin/sh
HOST='127.0.0.1:27017'
DB_NAME='github-ams'
USER='whcj'
PASSWORD='password'
# mongodump path
DUMP=/usr/bin/mongodump
# temporary output folder
OUT_DIR=/usr/mongodb_bak/temp
# save folder
SAVE_DIR=/usr/mongodb_bak/backup
DATE=$(date -d 'today' +'%Y-%m-%d-%H-%M-%S')
EXPIRE_DAYS=7
FILE_NAME="mongodb_bak_${DATE}.tar.gz"
rm -rf ${OUT_DIR}
mkdir -p "${OUT_DIR}/${DATE}"
mkdir -p ${SAVE_DIR}
# save
$DUMP -h ${HOST} -d ${DB_NAME} -o "${OUT_DIR}/${DATE}" -u ${USER} -p ${PASSWORD}
# save to .tar.gz
tar -zcvPf "${SAVE_DIR}/${FILE_NAME}" "${OUT_DIR}/${DATE}"
# remove expire file
find ${SAVE_DIR}/ -mtime +${EXPIRE_DAYS} -delete
# send to other server
#scp ${SAVE_DIR}/${FILE_NAME} jochen@123.123.123.123:/usr/mongodb_bak/${SAVE_DIR}


