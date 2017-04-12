#!/bin/bash
TOSCAHOME="/home/pi/tosca4iot"
nohup uaserver -x $TOSCAHOME/OPC_UA/Weather_Sensor_v0.1 > $TOSCAHOME/opc_ua.log & #>>/dev/null 2>>/dev/null &