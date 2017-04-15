#!/bin/bash

#Git Installation
TOSCAHOME="/home/pi/tosca4iot"
apt-get -y install subversion git
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME

#UseCase Installation
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/OPCUAWeatherDashboard

chmod 777 $TOSCAHOME/OPCUAWeatherDashboard/start.sh

cd ~