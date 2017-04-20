#!/bin/bash
TOSCAHOME="/home/pi/tosca4iot"
chmod 777 $TOSCAHOME/weathersensor.log
nohup /usr/local/lib/node-v6.10.2-linux-armv7l/bin/node /home/pi/tosca4iot/TISensorTag2OPCUA/sensortag2opcua.js > $TOSCAHOME/weathersensor.log & #>>/dev/null 2>>/dev/null &
