#!/bin/bash

nohup /usr/local/lib/node-v5.10.1-linux-armv7l/bin/node /home/pi/tosca4iot/TISensorTag2OPCUA/sensortag2opcua.js $1 > sensortag.log & #>>/dev/null 2>>/dev/null &