#!/bin/bash

#Create Config-File
echo "Please insert the ip address of the TOSCA4IoT basestation"
read basestation_ip
echo "Please insert the port of the Message Broker [Standard:1883]"
read message_broker_port
echo "Please insert the topic on which the dashboard will subscribe to"
read topic

cat > $HOME/UseCase/Dashboard/config.properties <<EOL
basestation_ip=${basestation_ip}
message_broker_port=${message_broker_port}
topic=${topic}

EOL