#!/bin/bash

nohup node /home/pi/tosca4iot/TISensorTag2OPCUA/sensortag2opcua.js $1 > sensortag.log & #>>/dev/null 2>>/dev/null &