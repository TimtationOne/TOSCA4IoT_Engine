#!/bin/bash

TOSCAHOME="/home/pi/tosca4iot"
cat > $TOSCAHOME/OPCUAWeatherDashboard/config.properties <<EOL
OPC_UA_URL=$1
EOL

mkdir /home/pi/.config/
mkdir /home/pi/.config/autostart
mv $TOSCAHOME/OPCUAWeatherDashboard/dashboard.desktop /home/pi/.config/autostart/