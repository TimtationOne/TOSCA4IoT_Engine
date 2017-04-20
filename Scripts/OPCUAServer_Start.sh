#!/bin/bash
TOSCAHOME="/home/pi/tosca4iot"
chmod 777 $TOSCAHOME/opc_ua.log
nohup uaserver -x $TOSCAHOME/OPC_UA/Weather_Sensor_v0.1.xml > $TOSCAHOME/opc_ua.log &