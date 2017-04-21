#!/bin/bash

#NodeJS Installation
apt-get -y remove nodejs
hash -d node
wget https://nodejs.org/dist/v6.10.2/node-v6.10.2-linux-armv7l.tar.xz
tar -xvf node-v6.10.2-linux-armv7l.tar.xz -C /usr/local/lib
export PATH=${PATH}:/usr/local/lib/node-v6.10.2-linux-armv7l/bin
echo PATH=${PATH}:/usr/local/lib/node-v6.10.2-linux-armv7l/bin >> /etc/environment
ln -s /usr/local/lib/node-v6.10.2-linux-armv7l/bin/node /usr/bin/node


#Git Installation
TOSCAHOME="/home/pi/tosca4iot"
sudo apt-get -y install subversion git
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/TISensorTag2OPCUA
cd TISensorTag2OPCUA

#Install Libraries
/usr/local/lib/node-v6.10.2-linux-armv7l/bin/npm install sensortag

cd ~
