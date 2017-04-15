#!/bin/bash

apt-get -y update & apt-get upgrade
apt-get -y remove nodejs


#Git Installation
TOSCAHOME="/home/pi/tosca4iot"
apt-get -y install subversion git
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME

#UseCase Installation
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/OPCUAWeatherDashboard