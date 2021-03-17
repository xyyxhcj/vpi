#! /bin/bash
mkdir /opt/project/ -p
cd /opt/project || exit
git clone git@github.com:xyyxhcj/vpi.git
#git clone https://github.com/xyyxhcj/vpi.git
cd vpi/sh/makeJar.sh || exit
