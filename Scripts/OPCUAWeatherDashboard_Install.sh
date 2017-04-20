#!/bin/bash

#Git Installation
TOSCAHOME="/home/pi/tosca4iot"
apt-get -y install subversion git oracle-java8-jdk
update-alternatives --set java /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/bin/java 
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME

#UseCase Installation
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/OPCUAWeatherDashboard

chmod 777 $TOSCAHOME/OPCUAWeatherDashboard/start.sh

cd ~