#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Peer1 Init #########"
echo "#####################################################"
echo

echo "0.0.0.0  peer1" >> /etc/hosts
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp

echo
echo "#####################################################"
echo "##### Adding peer to the channel: peer1 #########"
echo "#####################################################"
echo

peer channel fetch 0 bcchannel.block -c bcchannel -o orderer:7050 --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

peer channel join -b bcchannel.block --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

echo "##########################################"
echo "##### End of channel configuration #########"
echo "##########################################"