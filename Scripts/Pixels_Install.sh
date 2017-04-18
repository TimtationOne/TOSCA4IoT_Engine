#!/bin/bash
#Install User Interface Pixels
apt-get -y update
apt-get -y upgrade
apt-get install -y --no-install-recommends xserver-xorg
apt-get install -y --no-install-recommends xinit
apt-get install -y raspberrypi-ui-mods
apt-get install -y openjdk-8-jre