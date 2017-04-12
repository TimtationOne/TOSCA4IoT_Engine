#!/bin/bash

apt-get -y update & apt-get upgrade
apt-get -y remove nodejs
hash -d node
wget https://nodejs.org/dist/v5.10.1/node-v5.10.1-linux-armv7l.tar.gz 
tar -xvf node-v5.10.1-linux-armv7l.tar.gz -C /usr/local/lib
export PATH=${PATH}:/usr/local/lib/node-v5.10.1-linux-armv7l/bin

#Git Installation
TOSCAHOME="/home/pi/tosca4iot"
sudo apt-get -y install subversion git
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/TISensorTag2OPCUA
cd TISensorTag2OPCUA

#Install Libraries
/usr/local/lib/node-v5.10.1-linux-armv7l/bin/npm install sensortag

cd ~
