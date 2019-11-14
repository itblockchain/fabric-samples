#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Peer0 Init #########"
echo "#####################################################"
echo

echo "0.0.0.0  peer0" >> /etc/hosts
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp

echo
echo "#####################################################"
echo "##### Update channel with anchor #########"
echo "#####################################################"
echo

# Update channels with anchor peer
peer channel update -o orderer:7050 -c bcchannel -f /etc/hyperledger/configtx/Org1MSPanchors.tx --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

echo "##########################################"
echo "##### End of channel update #########"
echo "##########################################"