#!/bin/bash

TOSCAHOME="/home/pi/tosca4iot"
cat > $TOSCAHOME/OPCUAWeatherDashboard/config.properties <<EOL
OPC_UA_URL=$1
EOL

mv $TOSCAHOME/OPCUAWeatherDashboard/dashboard.desktop ~/.config/autostart/