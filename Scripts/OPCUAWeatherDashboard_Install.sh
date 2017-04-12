#!/bin/bash

apt-get -y update & apt-get upgrade
apt-get -y remove nodejs


#Git Installation
HOME="/home/pi"
apt-get -y install subversion git
cd ~
mkdir $HOME/UseCase
cd $HOME/UseCase/

#UseCase Installation
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Applications/OPCUAWeatherDashboard