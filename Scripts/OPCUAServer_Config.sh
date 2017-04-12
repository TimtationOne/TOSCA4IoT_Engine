#!/bin/bash
TOSCAHOME="/home/pi/tosca4iot"
sudo apt-get -y install subversion git
cd ~
mkdir $TOSCAHOME
cd $TOSCAHOME
svn export https://github.com/TimtationOne/TOSCA4IoT_Engine.git/trunk/Appliations/OPC_UA
cd ~