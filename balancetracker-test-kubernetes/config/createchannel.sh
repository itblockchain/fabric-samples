#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Creating the channel #########"
echo "#####################################################"
echo

peer channel create -o orderer:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx

echo
echo "#####################################################"
echo "##### Adding peer to the channel #########"
echo "#####################################################"
echo

peer channel join -b mychannel.block

echo "##########################################"
echo "##### End of channel adding #########"
echo "##########################################"

